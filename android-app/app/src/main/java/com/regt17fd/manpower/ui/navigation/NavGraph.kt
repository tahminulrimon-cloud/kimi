package com.regt17fd.manpower.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.regt17fd.manpower.auth.SessionManager
import com.regt17fd.manpower.ui.attendance.AttendanceEntryScreen
import com.regt17fd.manpower.ui.dashboard.DashboardScreen
import com.regt17fd.manpower.ui.login.ChangePasswordScreen
import com.regt17fd.manpower.ui.login.LoginScreen
import com.regt17fd.manpower.ui.personnel.PersonnelDetailScreen
import com.regt17fd.manpower.ui.personnel.PersonnelEditScreen
import com.regt17fd.manpower.ui.personnel.PersonnelListScreen

/**
 * Every authenticated route is guarded implicitly: the app starts at
 * [Screen.Login] and only reaches the rest of the graph via
 * [LoginScreen]'s onLoginSuccess callback, which itself checks
 * `mustChangePassword` first. There is no deep link into an authenticated
 * screen, so no separate per-route auth check is needed for Milestone 1.
 */
@Composable
fun ManpowerNavGraph(sessionManager: SessionManager, navController: NavHostController = rememberNavController()) {
    val currentUser by sessionManager.currentUser.collectAsState()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    val destination = if (currentUser?.mustChangePassword == true) {
                        Screen.ChangePassword.route
                    } else {
                        Screen.Dashboard.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
            )
        }
        composable(Screen.ChangePassword.route) {
            ChangePasswordScreen(
                onDone = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.ChangePassword.route) { inclusive = true }
                    }
                },
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onOpenPersonnel = { navController.navigate(Screen.PersonnelList.route) },
                onOpenAttendanceEntry = { navController.navigate(Screen.AttendanceEntry.route) },
            )
        }
        composable(Screen.PersonnelList.route) {
            PersonnelListScreen(
                onOpenDetail = { serviceNumber -> navController.navigate(Screen.PersonnelDetail.path(serviceNumber)) },
                onCreateNew = { navController.navigate(Screen.PersonnelEdit.pathForCreate()) },
            )
        }
        composable(
            route = Screen.PersonnelDetail.route,
            arguments = listOf(navArgument("serviceNumber") { type = NavType.StringType }),
        ) {
            PersonnelDetailScreen(
                onEdit = { serviceNumber -> navController.navigate(Screen.PersonnelEdit.pathForEdit(serviceNumber)) },
            )
        }
        composable(
            route = Screen.PersonnelEdit.route,
            arguments = listOf(
                navArgument("serviceNumber") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ),
        ) { backStackEntry ->
            val serviceNumber = backStackEntry.arguments?.getString("serviceNumber")
            PersonnelEditScreen(
                serviceNumber = serviceNumber,
                onSaved = { navController.popBackStack() },
            )
        }
        composable(Screen.AttendanceEntry.route) {
            AttendanceEntryScreen()
        }
    }
}
