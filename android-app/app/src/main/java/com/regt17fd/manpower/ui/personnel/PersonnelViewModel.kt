package com.regt17fd.manpower.ui.personnel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.regt17fd.manpower.auth.SessionManager
import com.regt17fd.manpower.data.local.entity.Personnel
import com.regt17fd.manpower.data.model.Appointment
import com.regt17fd.manpower.data.model.BloodGroup
import com.regt17fd.manpower.data.model.MedicalCategory
import com.regt17fd.manpower.data.model.Rank
import com.regt17fd.manpower.data.model.Section
import com.regt17fd.manpower.data.model.SubUnit
import com.regt17fd.manpower.data.model.Trade
import com.regt17fd.manpower.data.model.UserRole
import com.regt17fd.manpower.data.repository.PersonnelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonnelListViewModel @Inject constructor(
    private val personnelRepository: PersonnelRepository,
    val sessionManager: SessionManager,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val results: StateFlow<List<Personnel>> = _query
        .flatMapLatest { q -> if (q.isBlank()) personnelRepository.observeAll() else personnelRepository.search(q) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val isAdmin: StateFlow<Boolean> = sessionManager.currentUser
        .map { it?.role == UserRole.ADMIN }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun onQueryChange(value: String) {
        _query.value = value
    }
}

data class PersonnelFormState(
    val serviceNumber: String = "",
    val isEditingExisting: Boolean = false,
    val rank: Rank = Rank.SEPOY,
    val name: String = "",
    val trade: Trade = Trade.GUNNER,
    val appointment: Appointment = Appointment.GUNNER,
    val subUnit: SubUnit = SubUnit.A_BATTERY,
    val section: Section = Section.SECTION_1,
    val dateOfJoining: Long = System.currentTimeMillis(),
    val dateOfBirth: Long = System.currentTimeMillis(),
    val bloodGroup: BloodGroup = BloodGroup.O_POS,
    val contactNumber: String = "",
    val nextOfKin: String = "",
    val nokContact: String = "",
    val medicalCategory: MedicalCategory = MedicalCategory.A1,
    val weaponNumber: String = "",
    val photoUri: String? = null,
    val error: String? = null,
    val saved: Boolean = false,
)

@HiltViewModel
class PersonnelEditViewModel @Inject constructor(
    private val personnelRepository: PersonnelRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _formState = MutableStateFlow(PersonnelFormState())
    val formState: StateFlow<PersonnelFormState> = _formState.asStateFlow()

    fun loadForEdit(serviceNumber: String) {
        viewModelScope.launch {
            personnelRepository.getByServiceNumber(serviceNumber)?.let { p ->
                _formState.value = PersonnelFormState(
                    serviceNumber = p.serviceNumber,
                    isEditingExisting = true,
                    rank = p.rank,
                    name = p.name,
                    trade = p.trade,
                    appointment = p.appointment,
                    subUnit = p.subUnit,
                    section = p.section,
                    dateOfJoining = p.dateOfJoining,
                    dateOfBirth = p.dateOfBirth,
                    bloodGroup = p.bloodGroup,
                    contactNumber = p.contactNumber,
                    nextOfKin = p.nextOfKin,
                    nokContact = p.nokContact,
                    medicalCategory = p.medicalCategory,
                    weaponNumber = p.weaponNumber.orEmpty(),
                    photoUri = p.photoUri,
                )
            }
        }
    }

    fun update(transform: (PersonnelFormState) -> PersonnelFormState) {
        _formState.value = transform(_formState.value).copy(error = null)
    }

    fun save() {
        val f = _formState.value
        val actor = sessionManager.currentUser.value ?: return
        if (f.serviceNumber.isBlank() || f.name.isBlank()) {
            _formState.value = f.copy(error = "Service number and name are required")
            return
        }
        val personnel = Personnel(
            serviceNumber = f.serviceNumber.trim(),
            rank = f.rank,
            name = f.name.trim(),
            trade = f.trade,
            appointment = f.appointment,
            subUnit = f.subUnit,
            section = f.section,
            dateOfJoining = f.dateOfJoining,
            dateOfBirth = f.dateOfBirth,
            bloodGroup = f.bloodGroup,
            contactNumber = f.contactNumber.trim(),
            nextOfKin = f.nextOfKin.trim(),
            nokContact = f.nokContact.trim(),
            medicalCategory = f.medicalCategory,
            weaponNumber = f.weaponNumber.ifBlank { null },
            photoUri = f.photoUri,
        )
        viewModelScope.launch {
            if (f.isEditingExisting) {
                personnelRepository.update(personnel, actor)
            } else {
                personnelRepository.create(personnel, actor)
            }
            _formState.value = f.copy(saved = true)
        }
    }
}
