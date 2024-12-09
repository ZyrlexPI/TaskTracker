package com.example.tasktracker.navigation.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tasktracker.enums.TaskStatus
import com.example.tasktracker.enums.TaskViewOption
import com.example.tasktracker.navigation.models.BottomBarGraph
import com.example.tasktracker.navigation.models.TasksGraph
import com.example.tasktracker.screens.main.tasks.TasksScreenScaffold
import com.example.tasktracker.screens.tasks.infoTask.InfoTaskScreenScaffold
import com.example.tasktracker.screens.tasks.listTask.ListTaskScreenScaffold
import com.example.tasktracker.viewModels.CompanyViewModel
import com.example.tasktracker.viewModels.TasksViewModel
import com.example.tasktracker.viewModels.UserViewModel

@Composable
fun TasksNavigationGraph(
    padding: PaddingValues,
    navController: NavHostController = rememberNavController(),
    userViewModel: UserViewModel,
    companyViewModel: CompanyViewModel,
    tasksViewModel: TasksViewModel,
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
                        { navController.navigate(TasksGraph.TASK_AUTHOR_LIST) },
                        { navController.navigate(TasksGraph.TASK_ARCHIVED_LIST) },
                    ),
                userViewModel = userViewModel,
                companyViewModel = companyViewModel,
                tasksViewModel = tasksViewModel,
            )
        }

        composable(route = TasksGraph.TASK_NEW_LIST) {
            ListTaskScreenScaffold(
                padding = padding,
                status = TaskStatus.NEW_TASK,
                viewingOption = TaskViewOption.EXECUTOR,
                navigateToInfoTask = { navController.navigate(TasksGraph.TASK_INFO) },
                userViewModel = userViewModel,
                tasksViewModel = tasksViewModel,
            )
        }

        composable(route = TasksGraph.TASK_IN_PROGRESS_LIST) {
            ListTaskScreenScaffold(
                padding = padding,
                status = TaskStatus.IN_PROGRESS,
                viewingOption = TaskViewOption.EXECUTOR,
                { navController.navigate(TasksGraph.TASK_INFO) },
                userViewModel = userViewModel,
                tasksViewModel = tasksViewModel,
            )
        }

        composable(route = TasksGraph.TASK_COMPLETED_LIST) {
            ListTaskScreenScaffold(
                padding = padding,
                status = TaskStatus.COMPLETED,
                viewingOption = TaskViewOption.EXECUTOR,
                { navController.navigate(TasksGraph.TASK_INFO) },
                userViewModel = userViewModel,
                tasksViewModel = tasksViewModel,
            )
        }

        composable(route = TasksGraph.TASK_AUTHOR_LIST) {
            ListTaskScreenScaffold(
                padding = padding,
                status = TaskStatus.COMPLETED,
                viewingOption = TaskViewOption.AUTHOR,
                { navController.navigate(TasksGraph.TASK_INFO) },
                userViewModel = userViewModel,
                tasksViewModel = tasksViewModel,
            )
        }

        composable(route = TasksGraph.TASK_ARCHIVED_LIST) {
            ListTaskScreenScaffold(
                padding = padding,
                status = TaskStatus.ARHIVED,
                viewingOption = TaskViewOption.EXECUTOR,
                { navController.navigate(TasksGraph.TASK_INFO) },
                userViewModel = userViewModel,
                tasksViewModel = tasksViewModel,
            )
        }

        composable(route = TasksGraph.TASK_INFO) {
            InfoTaskScreenScaffold(
                padding = padding,
                userViewModel = userViewModel,
                tasksViewModel = tasksViewModel,
                returnToTaskList = { navController.popBackStack() }
            )
        }
    }
}
