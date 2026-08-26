package com.regt17fd.manpower.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.regt17fd.manpower.data.local.entity.AttendanceRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    /** Upsert-by-day semantics: REPLACE relies on the (date, serviceNumber) unique index. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: AttendanceRecord)

    @Query("SELECT * FROM attendance_records WHERE date = :date")
    fun observeForDate(date: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE date = :date AND serviceNumber = :serviceNumber LIMIT 1")
    suspend fun getForPersonAndDate(date: String, serviceNumber: String): AttendanceRecord?

    @Query("SELECT * FROM attendance_records WHERE serviceNumber = :serviceNumber ORDER BY date DESC")
    fun observeHistoryForPerson(serviceNumber: String): Flow<List<AttendanceRecord>>
}
