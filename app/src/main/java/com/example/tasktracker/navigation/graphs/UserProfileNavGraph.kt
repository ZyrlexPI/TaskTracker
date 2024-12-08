package com.example.tasktracker.navigation.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tasktracker.navigation.models.BottomBarGraph
import com.example.tasktracker.navigation.models.UserProfileGraph
import com.example.tasktracker.screens.main.userProfile.UserProfileScreenScaffold
import com.example.tasktracker.screens.userProfile.changePassword.ChangePasswordScreenScaffold
import com.example.tasktracker.screens.userProfile.company.CompanyScreenScaffold
import com.example.tasktracker.screens.userProfile.createCompany.CreateCompanyScreenScaffold
import com.example.tasktracker.screens.userProfile.joinCompany.JoinCompanyScreenScaffold
import com.example.tasktracker.screens.userProfile.security.SecurityScreenScaffold
import com.example.tasktracker.screens.userProfile.settings.SettingsScreenScaffold
import com.example.tasktracker.screens.userProfile.userData.UserDataScreenScaffold
import com.example.tasktracker.services.firebase.CompanyViewModel
import com.example.tasktracker.services.firebase.UserViewModel

@Composable
fun UserProfileNavigationGraph(
    padding: PaddingValues,
    navController: NavHostController = rememberNavController(),
    userViewModel: UserViewModel,
    companyViewModel: CompanyViewModel,
    prefs: DataStore<Preferences>,
    returnInAuth: () -> Unit,
) {
    NavHost(
        navController = navController,
        route = BottomBarGraph.USER_PROFILE,
        startDestination = UserProfileGraph.USER_PROFILE_ROOT
    ) {
        composable(route = UserProfileGraph.USER_PROFILE_ROOT) {
            UserProfileScreenScaffold(
                padding = padding,
                onClick =
                    arrayOf(
                        { navController.navigate(UserProfileGraph.USER_DATA) },
                        { navController.navigate(UserProfileGraph.SECURITY) },
                        { navController.navigate(UserProfileGraph.SETTINGS) },
                        { navController.navigate(UserProfileGraph.COMPANY) },
                        returnInAuth,
                    ),
            )
        }

        composable(route = UserProfileGraph.USER_DATA) {
            UserDataScreenScaffold(
                padding = padding,
                userViewModel = userViewModel,
            )
        }
        composable(route = UserProfileGraph.SECURITY) {
            SecurityScreenScaffold(
                padding = padding,
                onClick =
                    arrayOf(
                        { navController.navigate(UserProfileGraph.CHANGE_PASSWORD) },
                    ),
            )
        }
        composable(route = UserProfileGraph.SETTINGS) {
            SettingsScreenScaffold(
                prefs = prefs,
                padding = padding,
            )
        }

        composable(route = UserProfileGraph.CHANGE_PASSWORD) {
            ChangePasswordScreenScaffold(
                padding = padding,
                onClick = { navController.navigate(UserProfileGraph.USER_PROFILE_ROOT) },
            )
        }

        composable(route = UserProfileGraph.COMPANY) {
            CompanyScreenScaffold(
                padding = padding,
                onClick =
                    arrayOf(
                        { navController.navigate(UserProfileGraph.COMPANY_JOIN) },
                        { navController.navigate(UserProfileGraph.COMPANY_ADD) },
                        { navController.navigate(UserProfileGraph.USER_PROFILE_ROOT) },
                        { navController.navigate(UserProfileGraph.USER_DATA) },
                    ),
                companyViewModel = companyViewModel,
                userViewModel = userViewModel,
            )
        }

        composable(route = UserProfileGraph.COMPANY_JOIN) {
            JoinCompanyScreenScaffold(
                padding = padding,
                onClick = arrayOf({ navController.popBackStack() }),
                userViewModel = userViewModel,
                companyViewModel = companyViewModel,
            )
        }
        composable(route = UserProfileGraph.COMPANY_ADD) {
            CreateCompanyScreenScaffold(
                padding = padding,
                onClick = arrayOf({ navController.popBackStack() }),
                userViewModel = userViewModel,
                companyViewModel = companyViewModel,
            )
        }
    }
}
