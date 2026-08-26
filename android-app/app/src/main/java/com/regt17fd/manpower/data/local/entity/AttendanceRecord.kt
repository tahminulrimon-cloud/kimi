package com.regt17fd.manpower.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.regt17fd.manpower.data.model.AttendanceStatus
import com.regt17fd.manpower.data.model.DutyAssignment

/**
 * Daily attendance/distribution record — spec section 3B. One row per
 * person per date; the unique index enforces that so re-entering today's
 * status for someone updates the existing row instead of duplicating it.
 */
@Entity(
    tableName = "attendance_records",
    indices = [Index(value = ["date", "serviceNumber"], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = Personnel::class,
            parentColumns = ["serviceNumber"],
            childColumns = ["serviceNumber"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // ISO yyyy-MM-dd
    val serviceNumber: String,
    val status: AttendanceStatus,
    val location: String? = null,
    val dutyAssignment: DutyAssignment = DutyAssignment.NORMAL_DUTY,
    val remarks: String? = null,
    val timeIn: String? = null,
    val timeOut: String? = null,
    val vehicleAllotted: String? = null,
    val recordedByUserId: Long = 0,
    val recordedAt: Long = System.currentTimeMillis(),
)
