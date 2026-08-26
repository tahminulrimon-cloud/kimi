package com.regt17fd.manpower.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.regt17fd.manpower.data.model.Appointment
import com.regt17fd.manpower.data.model.BloodGroup
import com.regt17fd.manpower.data.model.MedicalCategory
import com.regt17fd.manpower.data.model.Rank
import com.regt17fd.manpower.data.model.Section
import com.regt17fd.manpower.data.model.SubUnit
import com.regt17fd.manpower.data.model.Trade

/** Personnel master record — spec section 3A. Service number is the natural key. */
@Entity(tableName = "personnel")
data class Personnel(
    @PrimaryKey val serviceNumber: String,
    val rank: Rank,
    val name: String,
    val trade: Trade,
    val appointment: Appointment,
    val subUnit: SubUnit,
    val section: Section,
    val dateOfJoining: Long,
    val dateOfBirth: Long,
    val bloodGroup: BloodGroup,
    val contactNumber: String,
    val nextOfKin: String,
    val nokContact: String,
    val medicalCategory: MedicalCategory,
    val weaponNumber: String? = null,
    val photoUri: String? = null,
    val isActive: Boolean = true,
)
