package com.example.tasktracker.navigation.graphs

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.tasktracker.navigation.models.AuthGraph
import com.example.tasktracker.navigation.models.RootGraph
import com.example.tasktracker.screens.auth.AuthorizationScreen
import com.example.tasktracker.screens.auth.RecoveryScreen
import com.example.tasktracker.screens.auth.RegistrationScreen

fun NavGraphBuilder.authNavigationGraph(navController: NavHostController) {
    navigation(route = RootGraph.AUTHENTICATION, startDestination = AuthGraph.AUTHORIZATION) {
        composable(route = AuthGraph.AUTHORIZATION) {
            AuthorizationScreen(
                navigateToHomeScreen = { navigateToHome(navController) },
                navigateToRegistrationScreen = { navController.navigate(AuthGraph.REGISTRATION) },
                navigateToRecoveryScreen = { navController.navigate(AuthGraph.RECOVERY) },
            )
        }

        composable(route = AuthGraph.REGISTRATION) {
            RegistrationScreen(navigateToHomeScreen = { navigateToHome(navController) })
        }

        composable(route = AuthGraph.RECOVERY) {
            RecoveryScreen(
                navigateToAuthScreen = { navController.navigate(AuthGraph.AUTHORIZATION) }
            )
        }
    }
}

/** Для того, чтобы перейти на главный экран и при этом невозможно было вернуться назад */
private fun navigateToHome(navController: NavHostController) {
    navController.navigate(RootGraph.MAIN) {
        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
    }
}
