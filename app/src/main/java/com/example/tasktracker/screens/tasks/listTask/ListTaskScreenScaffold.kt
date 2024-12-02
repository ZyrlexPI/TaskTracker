package com.example.tasktracker.screens.tasks.listTask

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.tasktracker.enums.TaskStatus
import com.example.tasktracker.services.firebase.TasksViewModel
import com.example.tasktracker.services.firebase.UserViewModel
import com.ravenzip.workshop.components.TopAppBar

@Composable
fun ListTaskScreenScaffold(
    padding: PaddingValues,
    status: TaskStatus,
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
            }
        }

    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = { TopAppBar(title = statusTobBar, backArrow = null, items = listOf()) },
    ) { innerPadding ->
        ListTaskScreen(
            padding = innerPadding,
            status = status,
            navigateToInfoTask = navigateToInfoTask,
            userViewModel = userViewModel,
            tasksViewModel = tasksViewModel,
        )
    }
}
