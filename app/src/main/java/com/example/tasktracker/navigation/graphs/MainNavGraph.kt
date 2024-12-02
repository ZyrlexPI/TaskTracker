package com.example.tasktracker.navigation.graphs

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.tasktracker.services.firebase.getUser

@Composable
fun MainNavGraph(
    navController: NavHostController,
    padding: PaddingValues,
    returnInAuth: () -> Unit,
) {
    val userViewModel = hiltViewModel<UserViewModel>()
    val companyViewModel = hiltViewModel<CompanyViewModel>()
    val tasksViewModel = hiltViewModel<TasksViewModel>()
    val userData = userViewModel.dataUser.collectAsStateWithLifecycle().value
    val companyData = companyViewModel.dataCompany.collectAsStateWithLifecycle().value
    Log.d("UserDataInMainScreen", userData.toString())
    Log.d("CompanyDataInMainScreen", companyData.toString())

    /** Загрузка данных о пользователе и организации */
    LaunchedEffect(Unit) {
        userViewModel.get(getUser())
        Log.d("UserDataInMainScreen_LE", userData.toString())
        companyViewModel.getCurrentCompany(userData)
        Log.d("CompanyDataInMainScreen_LE", companyData.toString())
    }

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
                returnInAuth = returnInAuth,
            )
        }
    }
}
