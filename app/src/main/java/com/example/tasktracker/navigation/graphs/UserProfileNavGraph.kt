package com.example.tasktracker.navigation.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.tasktracker.navigation.models.BottomBar_Graph
import com.example.tasktracker.navigation.models.UserProfileGraph
import com.example.tasktracker.screens.main.UserProfileScreen
import com.example.tasktracker.screens.userProfile.SettingsScreen
import com.example.tasktracker.screens.userProfile.UserDataScreen
import com.example.tasktracker.screens.userProfile.company.CompanyScreen
import com.example.tasktracker.screens.userProfile.company.CreateCompanyScreen
import com.example.tasktracker.screens.userProfile.company.JoinCompanyScreen
import com.example.tasktracker.screens.userProfile.security.ChangePasswordScreen
import com.example.tasktracker.screens.userProfile.security.SecurityScreen
import com.example.tasktracker.services.firebase.CompanyService
import com.example.tasktracker.services.firebase.UserService

fun NavGraphBuilder.userProfileNavigationGraph(
    padding: PaddingValues,
    navController: NavHostController,
    userService: MutableState<UserService>,
    companyService: MutableState<CompanyService>
) {
    navigation(
        route = UserProfileGraph.USER_PROFILE_START,
        startDestination = BottomBar_Graph.USER_PROFILE
    ) {
        composable(route = BottomBar_Graph.USER_PROFILE) {
            UserProfileScreen(
                padding = padding,
                onClick =
                    arrayOf(
                        { navController.navigate(UserProfileGraph.USER_DATA) },
                        { navController.navigate(UserProfileGraph.SECURITY) },
                        { navController.navigate(UserProfileGraph.SETTINGS) },
                        { navController.navigate(UserProfileGraph.COMPANY) }
                    ),
                companyService = companyService,
                userService = userService,
            )
        }
        composable(route = UserProfileGraph.USER_DATA) {
            UserDataScreen(padding = padding, userService = userService)
        }
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
            ChangePasswordScreen(
                padding = padding,
                onClick =
                    arrayOf(
                        { navController.navigate(UserProfileGraph.USER_PROFILE_START) },
                    )
            )
        }

        composable(route = UserProfileGraph.COMPANY) {
            CompanyScreen(
                padding = padding,
                onClick =
                    arrayOf(
                        { navController.navigate(UserProfileGraph.COMPANY_JOIN) },
                        { navController.navigate(UserProfileGraph.COMPANY_ADD) },
                        { navController.navigate(UserProfileGraph.USER_PROFILE_START) },
                    ),
                companyService = companyService,
                userService = userService
            )
        }

        composable(route = UserProfileGraph.COMPANY_JOIN) {
            JoinCompanyScreen(
                padding = padding,
                onClick = arrayOf({ navController.popBackStack() }),
                userService = userService,
                companyService = companyService
            )
        }
        composable(route = UserProfileGraph.COMPANY_ADD) {
            CreateCompanyScreen(
                padding = padding,
                onClick = arrayOf({ navController.popBackStack() }),
                userService = userService
            )
        }
    }
}
