package com.example.directoryapplication.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.directoryapplication.presentation.auth.LoginScreen
import com.example.directoryapplication.presentation.directory.AddEditEmployeeScreen
import com.example.directoryapplication.presentation.directory.DetailScreen
import com.example.directoryapplication.presentation.directory.DirectoryScreen
import com.google.firebase.auth.FirebaseAuth

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Directory : Screen("directory")
    object Detail : Screen("detail/{employeeId}") {
        fun createRoute(id: Int) = "detail/$id"
    }
    object AddEmployee : Screen("add_employee")
    object EditEmployee : Screen("edit_employee/{employeeId}") {
        fun createRoute(id: Int) = "edit_employee/$id"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val isLoggedIn = FirebaseAuth.getInstance().currentUser != null

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Screen.Directory.route else Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Directory.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Directory.route) {
            DirectoryScreen(
                onEmployeeClick = { navController.navigate(Screen.Detail.createRoute(it)) },
                onAddClick = { navController.navigate(Screen.AddEmployee.route) },
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Directory.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("employeeId") { type = NavType.IntType })
        ) { backStack ->
            val id = backStack.arguments?.getInt("employeeId") ?: return@composable
            DetailScreen(
                employeeId = id,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Screen.EditEmployee.createRoute(id)) }
            )
        }

        composable(Screen.AddEmployee.route) {
            AddEditEmployeeScreen(
                employeeId = null,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditEmployee.route,
            arguments = listOf(navArgument("employeeId") { type = NavType.IntType })
        ) { backStack ->
            val id = backStack.arguments?.getInt("employeeId") ?: return@composable
            AddEditEmployeeScreen(
                employeeId = id,
                onBack = { navController.popBackStack() }
            )
        }
    }
}