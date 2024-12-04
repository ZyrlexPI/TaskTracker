package com.example.tasktracker.screens.tasks.infoTask

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasktracker.services.firebase.InfoTasksViewModel
import com.ravenzip.workshop.components.TopAppBar
import com.ravenzip.workshop.data.appbar.AppBarItem
import com.ravenzip.workshop.data.icon.Icon
import com.ravenzip.workshop.data.icon.IconConfig

@Composable
fun InfoTaskScreenScaffold(
    padding: PaddingValues,
    infoTasksViewModel: InfoTasksViewModel = hiltViewModel(),
) {
    val taskInfo = infoTasksViewModel.task.collectAsStateWithLifecycle().value

    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = {
            TopAppBar(
                title = "Задача №" + taskInfo.id,
                backArrow = null,
                items = generateTopAppBarItems()
            )
        },
    ) { innerPadding ->
        InfoTaskScreen(
            padding = innerPadding,
            taskView = taskInfo,
        )
    }
}

fun generateTopAppBarItems(): List<AppBarItem> {
    val editButton =
        AppBarItem(
            icon = Icon.ImageVectorIcon(Icons.Filled.Edit),
            iconConfig = IconConfig.Small,
            onClick = {},
        )

    val deleteButton =
        AppBarItem(
            icon = Icon.ImageVectorIcon(Icons.Filled.Delete),
            iconConfig = IconConfig.Small,
            onClick = {},
        )

    return listOf(editButton, deleteButton)
}
