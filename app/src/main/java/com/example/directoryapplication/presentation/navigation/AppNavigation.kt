package com.example.directoryapplication.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.directoryapplication.presentation.auth.LoginScreen
import com.example.directoryapplication.presentation.auth.RegisterScreen
import com.example.directoryapplication.presentation.directory.AddEditEmployeeScreen
import com.example.directoryapplication.presentation.directory.DetailScreen
import com.example.directoryapplication.presentation.directory.DirectoryScreen
import com.google.firebase.auth.FirebaseAuth

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
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
fun AppNavigation(
    isDarkTheme: Boolean = false,
    onThemeToggle: () -> Unit = {}
) {
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
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Directory.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Directory.route) { backStackEntry ->
            val addEditResult = backStackEntry
                .savedStateHandle
                .getStateFlow("should_refresh", false)
                .collectAsState()

            DirectoryScreen(
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                onEmployeeClick = { navController.navigate(Screen.Detail.createRoute(it)) },
                onAddClick = { navController.navigate(Screen.AddEmployee.route) },
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Directory.route) { inclusive = true }
                    }
                },
                shouldRefresh = addEditResult.value,
                onRefreshHandled = {
                    backStackEntry.savedStateHandle["should_refresh"] = false
                }
            )
        }

        // ... (Detail, AddEmployee и EditEmployee оставляете как были у вас) ...
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("employeeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("employeeId") ?: return@composable

            DetailScreen(
                employeeId = id,
                navController = navController,
                onBack = { navController.popBackStack() },
                onEdit = {
                    navController.navigate(Screen.EditEmployee.createRoute(id))
                },
                onDeleted = {
                    try {
                        navController.getBackStackEntry(Screen.Directory.route)
                            .savedStateHandle["should_refresh"] = true
                    } catch (e: Exception) { }
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.AddEmployee.route) {
            AddEditEmployeeScreen(
                employeeId = null,
                onBack = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("should_refresh", true)
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.EditEmployee.route,
            arguments = listOf(navArgument("employeeId") { type = NavType.IntType })
        ) { backStack ->
            val id = backStack.arguments?.getInt("employeeId") ?: return@composable
            AddEditEmployeeScreen(
                employeeId = id,
                onBack = {
                    navController.getBackStackEntry(Screen.Directory.route)
                        .savedStateHandle["should_refresh"] = true
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("should_refresh", true)
                    navController.popBackStack()
                }
            )
        }
    }
}