package com.example.tasktracker.screens.userProfile.userData

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.tasktracker.viewModels.UserViewModel
import com.ravenzip.workshop.components.SnackBar
import com.ravenzip.workshop.components.TopAppBar

@Composable
fun UserDataScreenScaffold(padding: PaddingValues, userViewModel: UserViewModel) {
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = { TopAppBar(title = "Личные данные", backArrow = null, items = listOf()) },
    ) { innerPadding ->
        UserDataScreen(
            padding = innerPadding,
            snackBarHostState = snackBarHostState,
            userViewModel = userViewModel
        )
    }

    SnackBar(snackBarHostState)
}
