package com.example.tasktracker.navigation.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.tasktracker.navigation.models.UserProfileGraph
import com.example.tasktracker.screens.userProfile.SettingsScreen
import com.example.tasktracker.screens.userProfile.UserDataScreen
import com.example.tasktracker.screens.userProfile.company.CompanyScreen
import com.example.tasktracker.screens.userProfile.company.CreateCompanyScreen
import com.example.tasktracker.screens.userProfile.company.JoinCompanyScreen
import com.example.tasktracker.screens.userProfile.security.ChangePasswordScreen
import com.example.tasktracker.screens.userProfile.security.SecurityScreen
import com.example.tasktracker.services.firebase.CompanyViewModel
import com.example.tasktracker.services.firebase.UserViewModel

fun NavGraphBuilder.userProfileNavigationGraph(
    padding: PaddingValues,
    navController: NavHostController,
    userViewModel: UserViewModel,
    companyViewModel: CompanyViewModel,
    snackBarHostState: SnackbarHostState
) {
    navigation(
        route = UserProfileGraph.USER_PROFILE_START,
        startDestination = UserProfileGraph.USER_DATA
    ) {
        composable(route = UserProfileGraph.USER_DATA) {
            UserDataScreen(
                padding = padding,
                snackBarHostState = snackBarHostState,
                userViewModel = userViewModel,
            )
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
                snackBarHostState = snackBarHostState,
                onClick = { navController.navigate(UserProfileGraph.USER_PROFILE_START) },
            )
        }

        composable(route = UserProfileGraph.COMPANY) {
            CompanyScreen(
                padding = padding,
                snackBarHostState = snackBarHostState,
                onClick =
                    arrayOf(
                        { navController.navigate(UserProfileGraph.COMPANY_JOIN) },
                        { navController.navigate(UserProfileGraph.COMPANY_ADD) },
                        { navController.navigate(UserProfileGraph.USER_PROFILE_START) },
                    ),
                companyViewModel = companyViewModel,
                userViewModel = userViewModel,
            )
        }

        composable(route = UserProfileGraph.COMPANY_JOIN) {
            JoinCompanyScreen(
                padding = padding,
                snackBarHostState = snackBarHostState,
                onClick = arrayOf({ navController.popBackStack() }),
                userViewModel = userViewModel,
                companyViewModel = companyViewModel,
            )
        }
        composable(route = UserProfileGraph.COMPANY_ADD) {
            CreateCompanyScreen(
                padding = padding,
                snackBarHostState = snackBarHostState,
                onClick = arrayOf({ navController.popBackStack() }),
                userViewModel = userViewModel,
            )
        }
    }
}
