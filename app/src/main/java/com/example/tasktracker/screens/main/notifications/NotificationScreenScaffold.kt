package com.example.tasktracker.screens.main.notifications

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ravenzip.workshop.components.TopAppBar

@Composable
fun NotificationsScreenScaffold(
    padding: PaddingValues,
) {
    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = { TopAppBar(title = "Уведомления", backArrow = null, items = listOf()) },
    ) { innerPadding ->
        NotificationsScreen(
            padding = innerPadding,
        )
    }
}