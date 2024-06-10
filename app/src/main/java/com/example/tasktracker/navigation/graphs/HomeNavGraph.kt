package com.example.tasktracker.navigation.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tasktracker.data.User
import com.example.tasktracker.navigation.models.BottomBar_Graph
import com.example.tasktracker.navigation.models.RootGraph
import com.example.tasktracker.screens.main.MainScreen
import com.example.tasktracker.screens.main.NotificationsScreen
import com.example.tasktracker.screens.main.TasksScreen
import com.example.tasktracker.services.firebase.CompanyService
import com.example.tasktracker.services.firebase.UserService
import com.example.tasktracker.services.firebase.getUser

@Composable
fun HomeScreenNavGraph(
    navController: NavHostController,
    padding: PaddingValues,
) {
    val userService = remember { mutableStateOf(UserService()) }
    val companyService = remember { mutableStateOf(CompanyService()) }
    val userData = remember { mutableStateOf(User()) }
    val isLoadingUser = remember { mutableStateOf(true) }
    userData.value = userService.value.dataUser.collectAsState().value
    val isLoadingCompany = remember { mutableStateOf(false) }
    LaunchedEffect(isLoadingUser.value) {
        if (isLoadingUser.value) {
            userService.value.get(getUser())
            isLoadingCompany.value = true
            isLoadingUser.value = false
        }
    }
    LaunchedEffect(isLoadingCompany.value) {
        if (isLoadingCompany.value) {
            companyService.value.getCurrentCompany(userData.value)
            isLoadingCompany.value = false
        }
    }
    NavHost(
        navController = navController,
        route = RootGraph.MAIN,
        startDestination = BottomBar_Graph.HOME
    ) {
        composable(route = BottomBar_Graph.HOME) { MainScreen(padding) }

        composable(route = BottomBar_Graph.TASKS) { TasksScreen(padding) }

        composable(route = BottomBar_Graph.NOTIFICATIONS) { NotificationsScreen(padding) }

        userProfileNavigationGraph(
            padding,
            navController = navController,
            userService = userService,
            companyService = companyService,
        )
    }
}
