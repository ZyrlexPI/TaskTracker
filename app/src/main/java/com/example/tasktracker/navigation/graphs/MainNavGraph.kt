package com.example.tasktracker.navigation.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
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
fun HomeScreenNavGraph(
    navController: NavHostController,
    padding: PaddingValues,
    returnInAuth: () -> Unit,
) {
    val userViewModel = hiltViewModel<UserViewModel>()
    val companyViewModel = hiltViewModel<CompanyViewModel>()
    val tasksViewModel = hiltViewModel<TasksViewModel>()
    val userData = userViewModel.dataUser.collectAsState().value

    /** Загрузка данных о пользователе и организации */
    LaunchedEffect(Unit) {
        userViewModel.get(getUser())
        companyViewModel.getCurrentCompany(userData)
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
