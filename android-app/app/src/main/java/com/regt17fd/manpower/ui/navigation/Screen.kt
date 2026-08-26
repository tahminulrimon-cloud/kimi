package com.regt17fd.manpower.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object ChangePassword : Screen("change_password")
    object Dashboard : Screen("dashboard")
    object PersonnelList : Screen("personnel_list")
    object AttendanceEntry : Screen("attendance_entry")

    object PersonnelDetail : Screen("personnel_detail/{serviceNumber}") {
        fun path(serviceNumber: String) = "personnel_detail/$serviceNumber"
    }

    object PersonnelEdit : Screen("personnel_edit?serviceNumber={serviceNumber}") {
        fun pathForCreate() = "personnel_edit"
        fun pathForEdit(serviceNumber: String) = "personnel_edit?serviceNumber=$serviceNumber"
    }
}
