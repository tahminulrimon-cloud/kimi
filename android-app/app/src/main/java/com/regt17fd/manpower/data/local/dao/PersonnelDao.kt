package com.regt17fd.manpower.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.regt17fd.manpower.data.local.entity.Personnel
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonnelDao {
    @Query("SELECT * FROM personnel WHERE isActive = 1 ORDER BY name")
    fun observeAll(): Flow<List<Personnel>>

    @Query("SELECT * FROM personnel WHERE serviceNumber = :serviceNumber LIMIT 1")
    suspend fun getByServiceNumber(serviceNumber: String): Personnel?

    @Query(
        """SELECT * FROM personnel WHERE isActive = 1 AND
           (name LIKE '%' || :query || '%' OR serviceNumber LIKE '%' || :query || '%')
           ORDER BY name""",
    )
    fun search(query: String): Flow<List<Personnel>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(personnel: Personnel)

    @Update
    suspend fun update(personnel: Personnel)

    @Delete
    suspend fun delete(personnel: Personnel)

    @Query("SELECT COUNT(*) FROM personnel WHERE isActive = 1")
    suspend fun count(): Int
}
