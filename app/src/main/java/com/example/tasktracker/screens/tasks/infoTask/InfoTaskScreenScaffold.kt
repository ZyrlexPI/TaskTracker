package com.example.tasktracker.screens.tasks.infoTask

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasktracker.services.firebase.InfoTasksViewModel
import com.ravenzip.workshop.components.TopAppBar

@Composable
fun InfoTaskScreenScaffold(
    padding: PaddingValues,
    infoTasksViewModel: InfoTasksViewModel = hiltViewModel(),
) {
    val taskInfo = infoTasksViewModel.task.collectAsStateWithLifecycle().value

    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = {
            TopAppBar(title = "Задача №" + taskInfo.id, backArrow = null, items = listOf())
        },
    ) { innerPadding ->
        InfoTaskScreen(
            padding = innerPadding,
            taskView = taskInfo,
        )
    }
}
