package com.example.tasktracker.screens.main.userProfile

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ravenzip.workshop.components.TopAppBar

@Composable
fun UserProfileScreenScaffold(
    padding: PaddingValues,
    vararg onClick: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = { TopAppBar(title = "Задачи", backArrow = null, items = listOf()) },
    ) { innerPadding ->
        UserProfileScreen(
            padding = innerPadding,
            onClick = onClick,
        )
    }
}
