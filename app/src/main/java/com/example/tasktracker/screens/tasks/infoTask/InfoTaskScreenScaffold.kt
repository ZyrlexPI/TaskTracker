package com.example.tasktracker.screens.tasks.infoTask

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.tasktracker.data.Task
import com.example.tasktracker.enums.TaskStatus
import com.ravenzip.workshop.components.TopAppBar

@Composable
fun InfoTaskScreenScaffold(
    padding: PaddingValues,
) {
    val taskView =
        Task(
            id = "1",
            name = "Тестовое задание",
            status = TaskStatus.NEW_TASK,
            author = "Алексей",
            author_id = "",
            executor = "Александр",
            executor_id = "",
            companyId = ""
        )

    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = {
            TopAppBar(title = "Задача №" + taskView.id, backArrow = null, items = listOf())
        },
    ) { innerPadding ->
        InfoTaskScreen(
            padding = innerPadding,
            taskView = taskView,
        )
    }
}
