package com.example.tasktracker.screens.tasks.listTask

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.tasktracker.enums.TaskStatus
import com.example.tasktracker.enums.TaskViewOption
import com.example.tasktracker.viewModels.TasksViewModel
import com.example.tasktracker.viewModels.UserViewModel
import com.ravenzip.workshop.components.TopAppBar

@Composable
fun ListTaskScreenScaffold(
    padding: PaddingValues,
    status: TaskStatus,
    viewingOption: TaskViewOption,
    navigateToInfoTask: () -> Unit,
    userViewModel: UserViewModel,
    tasksViewModel: TasksViewModel,
) {
    val statusTobBar =
        remember(status) {
            when (status) {
                TaskStatus.NEW_TASK -> "Новые задачи"
                TaskStatus.IN_PROGRESS -> "Задачи в процессе"
                TaskStatus.COMPLETED -> "Завершенные задачи"
                TaskStatus.ARHIVED -> "Архивированные задачи"
            }
        }

    val statusAuthorTopBar =
        remember(viewingOption) {
            when (viewingOption) {
                TaskViewOption.EXECUTOR -> "Задачи"
                TaskViewOption.AUTHOR -> "Отслеживаемые задачи"
            }
        }

    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = {
            TopAppBar(
                title =
                    if (viewingOption == TaskViewOption.AUTHOR) statusAuthorTopBar
                    else statusTobBar,
                backArrow = null,
                items = listOf()
            )
        },
    ) { innerPadding ->
        ListTaskScreen(
            padding = innerPadding,
            status = status,
            viewingOption = viewingOption,
            navigateToInfoTask = navigateToInfoTask,
            userViewModel = userViewModel,
            tasksViewModel = tasksViewModel,
        )
    }
}
