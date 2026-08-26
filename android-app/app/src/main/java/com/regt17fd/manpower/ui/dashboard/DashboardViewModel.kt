package com.regt17fd.manpower.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.regt17fd.manpower.auth.SessionManager
import com.regt17fd.manpower.data.local.entity.AttendanceRecord
import com.regt17fd.manpower.data.local.entity.Personnel
import com.regt17fd.manpower.data.model.AttendanceStatus
import com.regt17fd.manpower.data.model.SubUnit
import com.regt17fd.manpower.data.repository.AttendanceRepository
import com.regt17fd.manpower.data.repository.PersonnelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class SubUnitBreakdown(
    val subUnit: SubUnit,
    val authorised: Int,
    val present: Int,
) {
    val percentage: Int get() = if (authorised == 0) 0 else (present * 100) / authorised
    val isCriticalShortage: Boolean get() = authorised > 0 && percentage < 70
}

data class DashboardUiState(
    val userLine: String = "",
    val totalAuthorised: Int = 0,
    val totalPresent: Int = 0,
    val onDuty: Int = 0,
    val onLeave: Int = 0,
    val sick: Int = 0,
    val tdOrCourse: Int = 0,
    val awol: Int = 0,
    val subUnitBreakdown: List<SubUnitBreakdown> = emptyList(),
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    personnelRepository: PersonnelRepository,
    attendanceRepository: AttendanceRepository,
    sessionManager: SessionManager,
) : ViewModel() {

    private val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    val uiState: StateFlow<DashboardUiState> = combine(
        personnelRepository.observeAll(),
        attendanceRepository.observeForDate(today),
    ) { personnel, records ->
        buildState(personnel, records, sessionManager)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())

    private fun buildState(
        personnel: List<Personnel>,
        records: List<AttendanceRecord>,
        sessionManager: SessionManager,
    ): DashboardUiState {
        val byServiceNumber = records.associateBy { it.serviceNumber }
        val user = sessionManager.currentUser.value

        var present = 0
        var onDuty = 0
        var onLeave = 0
        var sick = 0
        var tdOrCourse = 0
        var awol = 0

        personnel.forEach { p ->
            when (byServiceNumber[p.serviceNumber]?.status) {
                AttendanceStatus.PRESENT -> present++
                AttendanceStatus.ANNUAL_LEAVE, AttendanceStatus.CASUAL_LEAVE -> onLeave++
                AttendanceStatus.SICK_LOCAL, AttendanceStatus.SICK_HOSPITAL -> sick++
                AttendanceStatus.TD, AttendanceStatus.COURSE -> tdOrCourse++
                AttendanceStatus.AWOL -> awol++
                else -> {}
            }
            byServiceNumber[p.serviceNumber]?.let { rec ->
                if (rec.status == AttendanceStatus.PRESENT && rec.dutyAssignment.name != "NORMAL_DUTY") onDuty++
            }
        }

        val breakdown = SubUnit.entries.map { su ->
            val group = personnel.filter { it.subUnit == su }
            val presentInSubUnit = group.count { byServiceNumber[it.serviceNumber]?.status == AttendanceStatus.PRESENT }
            SubUnitBreakdown(subUnit = su, authorised = group.size, present = presentInSubUnit)
        }.filter { it.authorised > 0 }

        return DashboardUiState(
            userLine = user?.let { "${it.rank.displayName} ${it.fullName}" } ?: "",
            totalAuthorised = personnel.size,
            totalPresent = present,
            onDuty = onDuty,
            onLeave = onLeave,
            sick = sick,
            tdOrCourse = tdOrCourse,
            awol = awol,
            subUnitBreakdown = breakdown,
        )
    }
}
