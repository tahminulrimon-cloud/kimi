package com.regt17fd.manpower.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.regt17fd.manpower.data.local.entity.AuditLog
import kotlinx.coroutines.flow.Flow

@Dao
interface AuditDao {
    @Insert
    suspend fun insert(entry: AuditLog)

    @Query("SELECT * FROM audit_log ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<AuditLog>>
}
