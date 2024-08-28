package com.example.tasktracker.navigation.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tasktracker.navigation.models.BottomBar_Graph
import com.example.tasktracker.navigation.models.RootGraph
import com.example.tasktracker.navigation.models.UserProfileGraph
import com.example.tasktracker.screens.main.MainScreen
import com.example.tasktracker.screens.main.NotificationsScreen
import com.example.tasktracker.screens.main.TasksScreen
import com.example.tasktracker.screens.main.UserProfileScreen
import com.example.tasktracker.services.firebase.CompanyService
import com.example.tasktracker.services.firebase.UserService
import com.example.tasktracker.services.firebase.getUser

@Composable
fun HomeScreenNavGraph(
    navController: NavHostController,
    padding: PaddingValues,
    titleTopAppBar: MutableState<String>,
    snackBarHostState: SnackbarHostState
) {
    val userService = hiltViewModel<UserService>()
    val companyService = hiltViewModel<CompanyService>()
    val userData = userService.dataUser.collectAsState().value

    /** Загрузка данных о пользователе и организации */
    LaunchedEffect(Unit) {
        userService.get(getUser())
        companyService.getCurrentCompany(userData)
    }

    NavHost(
        navController = navController,
        route = RootGraph.MAIN,
        startDestination = BottomBar_Graph.HOME
    ) {
        composable(route = BottomBar_Graph.HOME) {
            titleTopAppBar.value = "Главный экран"
            MainScreen(padding)
        }

        composable(route = BottomBar_Graph.TASKS) {
            titleTopAppBar.value = "Задачи"
            TasksScreen(padding)
        }

        composable(route = BottomBar_Graph.NOTIFICATIONS) {
            titleTopAppBar.value = "Уведомления"
            NotificationsScreen(padding)
        }

        composable(route = BottomBar_Graph.USER_PROFILE) {
            titleTopAppBar.value = "Профиль"
            UserProfileScreen(
                padding = padding,
                onClick =
                    arrayOf(
                        { navController.navigate(UserProfileGraph.USER_DATA) },
                        { navController.navigate(UserProfileGraph.SECURITY) },
                        { navController.navigate(UserProfileGraph.SETTINGS) },
                        { navController.navigate(UserProfileGraph.COMPANY) }
                    ),
            )
        }

        userProfileNavigationGraph(
            padding = padding,
            navController = navController,
            userService = userService,
            companyService = companyService,
            titleTopAppBar = titleTopAppBar,
            snackBarHostState = snackBarHostState
        )
    }
}
