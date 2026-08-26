package com.regt17fd.manpower.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.regt17fd.manpower.data.model.Rank
import com.regt17fd.manpower.data.model.UserRole

@Entity(tableName = "users", indices = [Index(value = ["username"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val passwordHash: String,
    val role: UserRole,
    val rank: Rank,
    val fullName: String,
    val mustChangePassword: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
)
