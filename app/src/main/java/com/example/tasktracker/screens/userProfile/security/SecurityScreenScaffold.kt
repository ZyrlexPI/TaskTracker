package com.example.tasktracker.screens.userProfile.security

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ravenzip.workshop.components.TopAppBar

@Composable
fun SecurityScreenScaffold(
    padding: PaddingValues,
    vararg onClick: () -> Unit,
) {

    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = { TopAppBar(title = "Безопасность аккаунта", backArrow = null, items = listOf()) },
    ) { innerPadding ->
        SecurityScreen(
            padding = innerPadding,
            onClick = onClick,
        )
    }
}
