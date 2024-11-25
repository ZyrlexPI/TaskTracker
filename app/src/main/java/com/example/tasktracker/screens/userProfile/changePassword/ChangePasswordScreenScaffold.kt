package com.example.tasktracker.screens.userProfile.changePassword

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ravenzip.workshop.components.TopAppBar

@Composable
fun ChangePasswordScreenScaffold(
    padding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    onClick: () -> Unit,
) {

    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = { TopAppBar(title = "Смена пароля", backArrow = null, items = listOf()) },
    ) { innerPadding ->
        ChangePasswordScreen(
            padding = innerPadding,
            snackBarHostState = snackbarHostState,
            onClick = onClick,
        )
    }
}
