package com.regt17fd.manpower.data.repository

import com.regt17fd.manpower.data.local.dao.AttendanceDao
import com.regt17fd.manpower.data.local.dao.AuditDao
import com.regt17fd.manpower.data.local.entity.AttendanceRecord
import com.regt17fd.manpower.data.local.entity.AuditLog
import com.regt17fd.manpower.data.local.entity.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceRepository @Inject constructor(
    private val attendanceDao: AttendanceDao,
    private val auditDao: AuditDao,
) {
    fun observeForDate(date: String): Flow<List<AttendanceRecord>> = attendanceDao.observeForDate(date)

    fun observeHistory(serviceNumber: String): Flow<List<AttendanceRecord>> =
        attendanceDao.observeHistoryForPerson(serviceNumber)

    suspend fun getForPersonAndDate(date: String, serviceNumber: String): AttendanceRecord? =
        attendanceDao.getForPersonAndDate(date, serviceNumber)

    suspend fun record(record: AttendanceRecord, actor: User) {
        attendanceDao.upsert(record.copy(recordedByUserId = actor.id))
        auditDao.insert(
            AuditLog(
                userId = actor.id,
                userName = actor.fullName,
                action = "ATTENDANCE_RECORD",
                detail = "${record.serviceNumber} on ${record.date} -> ${record.status.displayName}",
            ),
        )
    }
}
