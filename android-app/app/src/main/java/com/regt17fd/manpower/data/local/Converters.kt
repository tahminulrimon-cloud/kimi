package com.regt17fd.manpower.data.local

import androidx.room.TypeConverter
import com.regt17fd.manpower.data.model.Appointment
import com.regt17fd.manpower.data.model.AttendanceStatus
import com.regt17fd.manpower.data.model.BloodGroup
import com.regt17fd.manpower.data.model.DutyAssignment
import com.regt17fd.manpower.data.model.MedicalCategory
import com.regt17fd.manpower.data.model.Rank
import com.regt17fd.manpower.data.model.Section
import com.regt17fd.manpower.data.model.SubUnit
import com.regt17fd.manpower.data.model.Trade
import com.regt17fd.manpower.data.model.UserRole

/** Each enum is stored as its `name` string; Room needs an explicit converter per type. */
class Converters {
    @TypeConverter fun toRank(v: String) = enumValueOf<Rank>(v)
    @TypeConverter fun fromRank(v: Rank) = v.name

    @TypeConverter fun toTrade(v: String) = enumValueOf<Trade>(v)
    @TypeConverter fun fromTrade(v: Trade) = v.name

    @TypeConverter fun toAppointment(v: String) = enumValueOf<Appointment>(v)
    @TypeConverter fun fromAppointment(v: Appointment) = v.name

    @TypeConverter fun toSubUnit(v: String) = enumValueOf<SubUnit>(v)
    @TypeConverter fun fromSubUnit(v: SubUnit) = v.name

    @TypeConverter fun toSection(v: String) = enumValueOf<Section>(v)
    @TypeConverter fun fromSection(v: Section) = v.name

    @TypeConverter fun toBloodGroup(v: String) = enumValueOf<BloodGroup>(v)
    @TypeConverter fun fromBloodGroup(v: BloodGroup) = v.name

    @TypeConverter fun toMedicalCategory(v: String) = enumValueOf<MedicalCategory>(v)
    @TypeConverter fun fromMedicalCategory(v: MedicalCategory) = v.name

    @TypeConverter fun toAttendanceStatus(v: String) = enumValueOf<AttendanceStatus>(v)
    @TypeConverter fun fromAttendanceStatus(v: AttendanceStatus) = v.name

    @TypeConverter fun toDutyAssignment(v: String) = enumValueOf<DutyAssignment>(v)
    @TypeConverter fun fromDutyAssignment(v: DutyAssignment) = v.name

    @TypeConverter fun toUserRole(v: String) = enumValueOf<UserRole>(v)
    @TypeConverter fun fromUserRole(v: UserRole) = v.name
}
