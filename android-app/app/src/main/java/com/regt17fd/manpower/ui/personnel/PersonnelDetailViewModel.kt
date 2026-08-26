package com.regt17fd.manpower.ui.personnel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.regt17fd.manpower.auth.SessionManager
import com.regt17fd.manpower.data.local.entity.AttendanceRecord
import com.regt17fd.manpower.data.local.entity.Personnel
import com.regt17fd.manpower.data.model.UserRole
import com.regt17fd.manpower.data.repository.AttendanceRepository
import com.regt17fd.manpower.data.repository.PersonnelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonnelDetailViewModel @Inject constructor(
    private val personnelRepository: PersonnelRepository,
    attendanceRepository: AttendanceRepository,
    sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val serviceNumber: String = checkNotNull(savedStateHandle["serviceNumber"])

    private val _personnel = MutableStateFlow<Personnel?>(null)
    val personnel: StateFlow<Personnel?> = _personnel.asStateFlow()

    val attendanceHistory: StateFlow<List<AttendanceRecord>> =
        attendanceRepository.observeHistory(serviceNumber)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val isAdmin: StateFlow<Boolean> = sessionManager.currentUser
        .map { it?.role == UserRole.ADMIN }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    init {
        viewModelScope.launch {
            _personnel.value = personnelRepository.getByServiceNumber(serviceNumber)
        }
    }
}
