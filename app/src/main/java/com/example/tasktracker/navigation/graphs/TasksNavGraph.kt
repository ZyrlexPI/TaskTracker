package com.example.tasktracker.navigation.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.tasktracker.enums.TaskStatus
import com.example.tasktracker.navigation.models.TasksGraph
import com.example.tasktracker.screens.tasks.listTask.ListTaskScreen
import com.example.tasktracker.services.firebase.CompanyViewModel
import com.example.tasktracker.services.firebase.UserViewModel

fun NavGraphBuilder.tasksNavigationGraph(
    padding: PaddingValues,
    navController: NavHostController,
    userViewModel: UserViewModel,
    companyViewModel: CompanyViewModel,
    snackBarHostState: SnackbarHostState
) {
    navigation(route = TasksGraph.TASKS_START, startDestination = TasksGraph.TASK_LIST) {
        composable(route = TasksGraph.TASK_NEW_LIST) {
            ListTaskScreen(
                padding = padding,
                status = TaskStatus.NEW_TASK,
            )
        }
        composable(route = TasksGraph.TASK_IN_PROGRESS_LIST) {
            ListTaskScreen(
                padding = padding,
                status = TaskStatus.IN_PROGRESS,
            )
        }
        composable(route = TasksGraph.TASK_COMPLETED_LIST) {
            ListTaskScreen(
                padding = padding,
                status = TaskStatus.COMPLETED,
            )
        }
    }

    // Добавить Задачи в прогрессе и Завершенные
}
