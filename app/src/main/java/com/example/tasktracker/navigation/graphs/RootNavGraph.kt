package com.example.tasktracker.navigation.graphs

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tasktracker.navigation.models.RootGraph
import com.example.tasktracker.screens.ScaffoldScreen

@Composable
fun RootNavigationGraph(navController: NavHostController, startDestination: String) {
    NavHost(
        navController = navController,
        route = RootGraph.ROOT,
        startDestination = startDestination
    ) {
        authNavigationGraph(navController = navController)
        composable(route = RootGraph.MAIN) { ScaffoldScreen() }
    }
}
