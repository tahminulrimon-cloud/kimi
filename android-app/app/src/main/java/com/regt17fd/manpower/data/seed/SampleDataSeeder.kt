package com.regt17fd.manpower.data.seed

import at.favre.lib.crypto.bcrypt.BCrypt
import com.regt17fd.manpower.data.local.AppDatabase
import com.regt17fd.manpower.data.local.entity.AttendanceRecord
import com.regt17fd.manpower.data.local.entity.Personnel
import com.regt17fd.manpower.data.local.entity.User
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * First-run data: the default admin account (forced password change on
 * first login, per spec) and 10 dummy personnel records with a mix of
 * ranks/sub-units/statuses so the dashboard shows real numbers immediately.
 * Runs once, from [AppDatabase]'s Room.Callback.onCreate.
 */
object SampleDataSeeder {

    private val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    suspend fun seed(db: AppDatabase) {
        seedDefaultAdmin(db)
        seedPersonnelAndAttendance(db)
    }

    private suspend fun seedDefaultAdmin(db: AppDatabase) {
        if (db.userDao().count() > 0) return
        val hash = BCrypt.withDefaults().hashToString(12, "admin".toCharArray())
        db.userDao().insert(
            User(
                username = "admin",
                passwordHash = hash,
                role = UserRole.ADMIN,
                rank = Rank.CAPTAIN,
                fullName = "Default Administrator",
                mustChangePassword = true,
            ),
        )
    }

    private suspend fun seedPersonnelAndAttendance(db: AppDatabase) {
        if (db.personnelDao().count() > 0) return

        fun daysAgoMillis(days: Int): Long {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -days)
            return cal.timeInMillis
        }

        val today = dateFmt.format(Calendar.getInstance().time)

        data class Seed(
            val svcNo: String,
            val rank: Rank,
            val name: String,
            val trade: Trade,
            val appointment: Appointment,
            val subUnit: SubUnit,
            val section: Section,
            val status: AttendanceStatus,
            val duty: DutyAssignment,
        )

        val samples = listOf(
            Seed("BD-10234", Rank.HAVILDAR, "Sample — Hav Karim Ullah", Trade.GUNNER, Appointment.HAVILDAR, SubUnit.A_BATTERY, Section.SECTION_1, AttendanceStatus.PRESENT, DutyAssignment.NORMAL_DUTY),
            Seed("BD-10891", Rank.NAIK, "Sample — Nk Robiul Islam", Trade.GUNNER, Appointment.NAIK, SubUnit.A_BATTERY, Section.SECTION_2, AttendanceStatus.PRESENT, DutyAssignment.GUARD_DUTY),
            Seed("BD-11045", Rank.SEPOY, "Sample — Gnr Anisur Rahman", Trade.GUNNER, Appointment.GUNNER, SubUnit.A_BATTERY, Section.SECTION_1, AttendanceStatus.SICK_LOCAL, DutyAssignment.OFF),
            Seed("BD-11302", Rank.LANCE_NAIK, "Sample — L/Bdr Shahin Alam", Trade.SIGNAL_OPERATOR, Appointment.LANCE_BOMBARDIER, SubUnit.HQ_BATTERY, Section.SIGNAL_SECTION, AttendanceStatus.PRESENT, DutyAssignment.CP_DUTY),
            Seed("BD-11588", Rank.SEPOY, "Sample — Gnr Delwar Hossain", Trade.DRIVER_MT, Appointment.GUNNER, SubUnit.MT_PARK, Section.HQ_SECTION, AttendanceStatus.ANNUAL_LEAVE, DutyAssignment.OFF),
            Seed("BD-11734", Rank.NAIB_SUBEDAR, "Sample — Nb Sub Fazlul Kader", Trade.CLERK, Appointment.NAIB_SUBEDAR, SubUnit.REGIMENTAL_HQ, Section.ADMIN_SECTION, AttendanceStatus.PRESENT, DutyAssignment.ADMIN_DUTY),
            Seed("BD-12010", Rank.SEPOY, "Sample — Gnr Mizanur Rahman", Trade.GUNNER, Appointment.GUNNER, SubUnit.B_BATTERY, Section.SECTION_3, AttendanceStatus.TD, DutyAssignment.OFF),
            Seed("BD-12266", Rank.HAVILDAR, "Sample — Hav Nurul Amin", Trade.MEDIC, Appointment.HAVILDAR, SubUnit.REGIMENTAL_AID_POST, Section.HQ_SECTION, AttendanceStatus.PRESENT, DutyAssignment.STANDBY),
            Seed("BD-12518", Rank.SEPOY, "Sample — Gnr Sohel Rana", Trade.GUNNER, Appointment.GUNNER, SubUnit.C_BATTERY, Section.SECTION_4, AttendanceStatus.COURSE, DutyAssignment.TRAINING),
            Seed("BD-12771", Rank.SECOND_LIEUTENANT, "Sample — 2Lt Tanvir Ahmed", Trade.GUNNER, Appointment.OFFICER, SubUnit.B_BATTERY, Section.HQ_SECTION, AttendanceStatus.PRESENT, DutyAssignment.NORMAL_DUTY),
        )

        samples.forEachIndexed { idx, s ->
            db.personnelDao().insert(
                Personnel(
                    serviceNumber = s.svcNo,
                    rank = s.rank,
                    name = s.name,
                    trade = s.trade,
                    appointment = s.appointment,
                    subUnit = s.subUnit,
                    section = s.section,
                    dateOfJoining = daysAgoMillis(365 * (2 + idx % 6)),
                    dateOfBirth = daysAgoMillis(365 * (22 + idx)),
                    bloodGroup = BloodGroup.entries[idx % BloodGroup.entries.size],
                    contactNumber = "01700-${100000 + idx}",
                    nextOfKin = "Sample — Next of Kin ${idx + 1}",
                    nokContact = "01800-${200000 + idx}",
                    medicalCategory = MedicalCategory.A1,
                    weaponNumber = if (s.trade == Trade.GUNNER) "WPN-${5000 + idx}" else null,
                ),
            )
            db.attendanceDao().upsert(
                AttendanceRecord(
                    date = today,
                    serviceNumber = s.svcNo,
                    status = s.status,
                    dutyAssignment = s.duty,
                    recordedByUserId = 0,
                ),
            )
        }
    }
}
