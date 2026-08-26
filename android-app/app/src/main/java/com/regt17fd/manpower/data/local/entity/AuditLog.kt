package com.regt17fd.manpower.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Minimal audit trail; Milestone 2's Admin Panel adds a viewer screen for this. */
@Entity(tableName = "audit_log")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val userId: Long,
    val userName: String,
    val action: String,
    val detail: String,
)
