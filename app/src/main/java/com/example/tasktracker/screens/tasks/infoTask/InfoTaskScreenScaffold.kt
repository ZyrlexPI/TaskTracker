package com.example.tasktracker.screens.tasks.infoTask

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ravenzip.workshop.components.TopAppBar

@Composable
fun ListTaskScreenScaffold(
    padding: PaddingValues,
) {

    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = { TopAppBar(title = "Задача №...", backArrow = null, items = listOf()) },
    ) { innerPadding ->
        InfoTaskScreen(
            padding = innerPadding,
        )
    }
}
