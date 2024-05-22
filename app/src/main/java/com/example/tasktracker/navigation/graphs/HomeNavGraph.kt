package com.example.tasktracker.navigation.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tasktracker.navigation.models.BottomBar_Graph
import com.example.tasktracker.navigation.models.RootGraph
import com.example.tasktracker.screens.main.MainScreen
import com.example.tasktracker.screens.main.NotificationsScreen
import com.example.tasktracker.screens.main.TasksScreen

@Composable
fun HomeScreenNavGraph(
    navController: NavHostController,
    padding: PaddingValues,
) {

    NavHost(
        navController = navController,
        route = RootGraph.MAIN,
        startDestination = BottomBar_Graph.HOME
    ) {
        composable(route = BottomBar_Graph.HOME) { MainScreen(padding) }

        composable(route = BottomBar_Graph.TASKS) { TasksScreen(padding) }

        composable(route = BottomBar_Graph.NOTIFICATIONS) { NotificationsScreen(padding) }

        userProfileNavigationGraph(padding, navController = navController)
    }
}
