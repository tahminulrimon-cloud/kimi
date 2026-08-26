package com.regt17fd.manpower.data.model

/**
 * All fixed dropdown vocabularies from the spec (section 7). These are
 * plain Kotlin enums for Milestone 1 — not admin-editable at runtime.
 * Making "master data" admin-editable needs a DB-backed lookup table
 * instead of a compiled enum; that's Milestone 2's Admin Panel work
 * (see android-app/README.md "Deferred to later milestones").
 */

enum class UserRole(val displayName: String) {
    STANDARD_OFFICER("Standard Officer"),
    ADMIN("Admin"),
}

enum class Rank(val displayName: String) {
    SEPOY("Sepoy"),
    LANCE_NAIK("Lance Naik"),
    NAIK("Naik"),
    HAVILDAR("Havildar"),
    NAIB_SUBEDAR("Naib Subedar"),
    SUBEDAR("Subedar"),
    SUBEDAR_MAJOR("Subedar Major"),
    SECOND_LIEUTENANT("Second Lieutenant"),
    LIEUTENANT("Lieutenant"),
    CAPTAIN("Captain"),
    MAJOR("Major"),
    LIEUTENANT_COLONEL("Lieutenant Colonel"),
}

enum class Trade(val displayName: String) {
    GUNNER("Gunner"),
    SIGNAL_OPERATOR("Signal Operator"),
    DRIVER_MT("Driver (MT)"),
    DRIVER_GNR("Driver (Gnr)"),
    CLERK("Clerk"),
    COOK("Cook"),
    MEDIC("Medic"),
    ARMOURER("Armourer"),
    FITTER("Fitter"),
    ELECTRICIAN("Electrician"),
    RADIO_OPERATOR("Radio Operator"),
    SURVEYOR("Surveyor"),
}

enum class Appointment(val displayName: String) {
    DETACHMENT_COMMANDER("Detachment Commander"),
    GUNNER("Gunner"),
    LANCE_BOMBARDIER("Lance Bombardier"),
    BOMBARDIER("Bombardier"),
    NAIK("Naik"),
    HAVILDAR("Havildar"),
    NAIB_SUBEDAR("Naib Subedar"),
    SUBEDAR("Subedar"),
    SUBEDAR_MAJOR("Subedar Major"),
    OFFICER("Officer"),
}

enum class SubUnit(val displayName: String) {
    REGIMENTAL_HQ("Regimental Headquarters"),
    HQ_BATTERY("Headquarters Battery"),
    A_BATTERY("A Battery"),
    B_BATTERY("B Battery"),
    C_BATTERY("C Battery"),
    WORKSHOP("Workshop"),
    MT_PARK("MT Park"),
    REGIMENTAL_AID_POST("Regimental Aid Post"),
}

enum class Section(val displayName: String) {
    SECTION_1("1 Section"),
    SECTION_2("2 Section"),
    SECTION_3("3 Section"),
    SECTION_4("4 Section"),
    HQ_SECTION("Headquarters Section"),
    SIGNAL_SECTION("Signal Section"),
    ADMIN_SECTION("Admin Section"),
}

enum class BloodGroup(val displayName: String) {
    A_POS("A+"), A_NEG("A-"),
    B_POS("B+"), B_NEG("B-"),
    AB_POS("AB+"), AB_NEG("AB-"),
    O_POS("O+"), O_NEG("O-"),
}

enum class MedicalCategory(val displayName: String) {
    A1("A1"), A2("A2"), A3("A3"),
    B1("B1"), B2("B2"),
    C("C"), D("D"), E("E"),
    TEMPORARILY_UNFIT("Temporarily Unfit"),
    PERMANENTLY_UNFIT("Permanently Unfit"),
}

/** Present is treated as the "manned" state; every other value counts against strength. */
enum class AttendanceStatus(val displayName: String) {
    PRESENT("Present"),
    ABSENT("Absent"),
    SICK_LOCAL("Sick (Local)"),
    SICK_HOSPITAL("Sick (Hospital)"),
    ANNUAL_LEAVE("Annual Leave"),
    CASUAL_LEAVE("Casual Leave"),
    TD("TD (Temporary Duty)"),
    COURSE("Course"),
    DETACHED("Detached"),
    AWOL("AWOL"),
    UNDER_ARREST("Under Arrest"),
    PRISON("Prison"),
    MATERNITY_PATERNITY("Maternity/Paternity"),
    RETIREMENT("Retirement"),
    DISCHARGE("Discharge"),
}

enum class DutyAssignment(val displayName: String) {
    NORMAL_DUTY("Normal Duty"),
    GUARD_DUTY("Guard Duty"),
    PICKET("Picket"),
    QRT("QRT"),
    CP_DUTY("CP Duty"),
    ADMIN_DUTY("Admin Duty"),
    TRAINING("Training"),
    STANDBY("Standby"),
    REST_DAY("Rest Day"),
    OFF("Off"),
}
