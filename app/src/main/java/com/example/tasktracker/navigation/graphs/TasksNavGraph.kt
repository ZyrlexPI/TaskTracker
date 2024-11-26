package com.example.tasktracker.navigation.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tasktracker.enums.TaskStatus
import com.example.tasktracker.navigation.models.BottomBarGraph
import com.example.tasktracker.navigation.models.TasksGraph
import com.example.tasktracker.screens.main.tasks.TasksScreenScaffold
import com.example.tasktracker.screens.tasks.listTask.ListTaskScreenScaffold

@Composable
fun TasksNavigationGraph(
    padding: PaddingValues,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        route = BottomBarGraph.TASKS,
        startDestination = TasksGraph.TASKS_ROOT
    ) {
        composable(route = TasksGraph.TASKS_ROOT) {
            TasksScreenScaffold(
                padding = padding,
                onClick =
                    arrayOf(
                        { navController.navigate(TasksGraph.TASK_NEW_LIST) },
                        { navController.navigate(TasksGraph.TASK_IN_PROGRESS_LIST) },
                        { navController.navigate(TasksGraph.TASK_COMPLETED_LIST) },
                    ),
            )
        }

        composable(route = TasksGraph.TASK_NEW_LIST) {
            ListTaskScreenScaffold(
                padding = padding,
                status = TaskStatus.NEW_TASK,
            )
        }

        composable(route = TasksGraph.TASK_IN_PROGRESS_LIST) {
            ListTaskScreenScaffold(
                padding = padding,
                status = TaskStatus.IN_PROGRESS,
            )
        }

        composable(route = TasksGraph.TASK_COMPLETED_LIST) {
            ListTaskScreenScaffold(
                padding = padding,
                status = TaskStatus.COMPLETED,
            )
        }
    }
}
