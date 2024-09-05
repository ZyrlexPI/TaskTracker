package com.example.tasktracker.navigation.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.tasktracker.services.firebase.CompanyViewModel
import com.example.tasktracker.services.firebase.UserViewModel
import com.example.tasktracker.services.firebase.getUser

@Composable
fun HomeScreenNavGraph(
    navController: NavHostController,
    padding: PaddingValues,
    snackBarHostState: SnackbarHostState,
    returnInAuth: () -> Unit,
) {
    val userViewModel = hiltViewModel<UserViewModel>()
    val companyViewModel = hiltViewModel<CompanyViewModel>()
    val userData = userViewModel.dataUser.collectAsState().value

    /** Загрузка данных о пользователе и организации */
    LaunchedEffect(Unit) {
        userViewModel.get(getUser())
        companyViewModel.getCurrentCompany(userData)
    }

    NavHost(
        navController = navController,
        route = RootGraph.MAIN,
        startDestination = BottomBar_Graph.HOME
    ) {
        composable(route = BottomBar_Graph.HOME) { MainScreen(padding) }

        composable(route = BottomBar_Graph.TASKS) { TasksScreen(padding) }

        composable(route = BottomBar_Graph.NOTIFICATIONS) { NotificationsScreen(padding) }

        composable(route = BottomBar_Graph.USER_PROFILE) {
            UserProfileScreen(
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

        userProfileNavigationGraph(
            padding = padding,
            navController = navController,
            userViewModel = userViewModel,
            companyViewModel = companyViewModel,
            snackBarHostState = snackBarHostState
        )
    }
}
