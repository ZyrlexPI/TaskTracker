package com.example.tasktracker.screens.main.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ravenzip.workshop.components.TopAppBar

@Composable
fun HomeScreenScaffold(
    padding: PaddingValues,
    navigationToLastViewTask: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = { TopAppBar(title = "Главный экран", backArrow = null, items = listOf()) },
    ) { innerPadding ->
        HomeScreen(
            padding = innerPadding,
            navigationToLastViewTask = navigationToLastViewTask,
        )
    }
}
