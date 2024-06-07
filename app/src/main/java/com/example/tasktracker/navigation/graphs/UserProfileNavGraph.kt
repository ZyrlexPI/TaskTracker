package com.example.tasktracker.navigation.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.tasktracker.navigation.models.BottomBar_Graph
import com.example.tasktracker.navigation.models.UserProfileGraph
import com.example.tasktracker.screens.main.UserProfileScreen
import com.example.tasktracker.screens.userProfile.ChangePasswordScreen
import com.example.tasktracker.screens.userProfile.SecurityScreen
import com.example.tasktracker.screens.userProfile.SettingsScreen
import com.example.tasktracker.screens.userProfile.UserDataScreen

fun NavGraphBuilder.userProfileNavigationGraph(
    padding: PaddingValues,
    navController: NavHostController,
) {
    navigation(
        route = BottomBar_Graph.USER_PROFILE,
        startDestination = UserProfileGraph.USER_PROFILE_START
    ) {
        composable(route = UserProfileGraph.USER_PROFILE_START) {
            UserProfileScreen(
                padding = padding,
                onClick =
                    arrayOf(
                        { navController.navigate(UserProfileGraph.USER_DATA) },
                        { navController.navigate(UserProfileGraph.SECURITY) },
                        { navController.navigate(UserProfileGraph.SETTINGS) }
                    )
            )
        }
        composable(route = UserProfileGraph.USER_DATA) { UserDataScreen(padding = padding) }
        composable(route = UserProfileGraph.SECURITY) {
            SecurityScreen(
                padding = padding,
                onClick =
                    arrayOf(
                        { navController.navigate(UserProfileGraph.CHANGE_PASSWORD) },
                    )
            )
        }
        composable(route = UserProfileGraph.SETTINGS) { SettingsScreen(padding = padding) }
        composable(route = UserProfileGraph.CHANGE_PASSWORD) {
            ChangePasswordScreen(padding = padding)
        }
    }
}
