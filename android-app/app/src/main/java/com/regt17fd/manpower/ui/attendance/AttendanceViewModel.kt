package com.regt17fd.manpower.ui.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.regt17fd.manpower.auth.SessionManager
import com.regt17fd.manpower.data.local.entity.AttendanceRecord
import com.regt17fd.manpower.data.local.entity.Personnel
import com.regt17fd.manpower.data.model.AttendanceStatus
import com.regt17fd.manpower.data.model.DutyAssignment
import com.regt17fd.manpower.data.repository.AttendanceRepository
import com.regt17fd.manpower.data.repository.PersonnelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class RosterRow(val personnel: Personnel, val record: AttendanceRecord?)

data class AttendanceUiState(
    val date: String = "",
    val roster: List<RosterRow> = emptyList(),
)

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val personnelRepository: PersonnelRepository,
    private val attendanceRepository: AttendanceRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    val uiState: StateFlow<AttendanceUiState> = combine(
        personnelRepository.observeAll(),
        attendanceRepository.observeForDate(today),
    ) { personnel, records ->
        val byServiceNumber = records.associateBy { it.serviceNumber }
        AttendanceUiState(
            date = today,
            roster = personnel.map { RosterRow(it, byServiceNumber[it.serviceNumber]) },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AttendanceUiState())

    fun setStatus(
        personnel: Personnel,
        status: AttendanceStatus,
        duty: DutyAssignment,
        location: String?,
        remarks: String?,
    ) {
        val actor = sessionManager.currentUser.value ?: return
        viewModelScope.launch {
            attendanceRepository.record(
                AttendanceRecord(
                    date = today,
                    serviceNumber = personnel.serviceNumber,
                    status = status,
                    dutyAssignment = duty,
                    location = location,
                    remarks = remarks,
                ),
                actor,
            )
        }
    }
}
