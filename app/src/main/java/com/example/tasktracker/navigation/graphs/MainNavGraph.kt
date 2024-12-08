package com.example.tasktracker.navigation.graphs

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tasktracker.navigation.models.BottomBarGraph
import com.example.tasktracker.navigation.models.RootGraph
import com.example.tasktracker.screens.main.notifications.NotificationsScreenScaffold
import com.example.tasktracker.services.firebase.CompanyViewModel
import com.example.tasktracker.services.firebase.TasksViewModel
import com.example.tasktracker.services.firebase.UserViewModel

@Composable
fun MainNavGraph(
    navController: NavHostController,
    padding: PaddingValues,
    prefs: DataStore<Preferences>,
    returnInAuth: () -> Unit,
) {
    val userViewModel = hiltViewModel<UserViewModel>()
    val companyViewModel = hiltViewModel<CompanyViewModel>()
    val tasksViewModel = hiltViewModel<TasksViewModel>()
    val userData = userViewModel.dataUser.collectAsStateWithLifecycle().value
    val companyData = companyViewModel.dataCompany.collectAsStateWithLifecycle().value
    Log.d("UserDataInMainScreen", userData.toString())
    Log.d("CompanyDataInMainScreen", companyData.toString())

    NavHost(
        navController = navController,
        route = RootGraph.MAIN,
        startDestination = BottomBarGraph.HOME
    ) {
        composable(route = BottomBarGraph.HOME) {
            HomeNavigationGraph(
                padding = padding,
                userViewModel = userViewModel,
                companyViewModel = companyViewModel,
                tasksViewModel = tasksViewModel,
            )
        }

        composable(route = BottomBarGraph.TASKS) {
            TasksNavigationGraph(
                padding = padding,
                userViewModel = userViewModel,
                companyViewModel = companyViewModel,
                tasksViewModel = tasksViewModel,
            )
        }

        composable(route = BottomBarGraph.NOTIFICATIONS) { NotificationsScreenScaffold(padding) }

        composable(route = BottomBarGraph.USER_PROFILE) {
            UserProfileNavigationGraph(
                padding = padding,
                userViewModel = userViewModel,
                companyViewModel = companyViewModel,
                prefs = prefs,
                returnInAuth = returnInAuth,
            )
        }
    }
}
