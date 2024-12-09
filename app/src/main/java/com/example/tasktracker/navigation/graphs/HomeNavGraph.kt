package com.example.tasktracker.navigation.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tasktracker.navigation.models.BottomBarGraph
import com.example.tasktracker.navigation.models.HomeGraph
import com.example.tasktracker.navigation.models.TasksGraph
import com.example.tasktracker.screens.main.home.HomeScreenScaffold
import com.example.tasktracker.screens.tasks.infoTask.InfoTaskScreenScaffold
import com.example.tasktracker.viewModels.CompanyViewModel
import com.example.tasktracker.viewModels.TasksViewModel
import com.example.tasktracker.viewModels.UserViewModel

@Composable
fun HomeNavigationGraph(
    padding: PaddingValues,
    navController: NavHostController = rememberNavController(),
    userViewModel: UserViewModel,
    companyViewModel: CompanyViewModel,
    tasksViewModel: TasksViewModel,
) {
    NavHost(
        navController = navController,
        route = BottomBarGraph.HOME,
        startDestination = HomeGraph.HOME_ROOT
    ) {
        composable(route = HomeGraph.HOME_ROOT) {
            HomeScreenScaffold(
                padding = padding,
                navigationToLastViewTask = { navController.navigate(TasksGraph.TASK_INFO) },
                userViewModel = userViewModel,
                tasksViewModel = tasksViewModel,
            )
        }

        composable(route = TasksGraph.TASK_INFO) {
            InfoTaskScreenScaffold(
                padding = padding,
                userViewModel = userViewModel,
                tasksViewModel = tasksViewModel,
                returnToTaskList = { navController.popBackStack() },
            )
        }
    }
}
