package com.example.tasktracker.screens.userProfile.company

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.tasktracker.services.firebase.CompanyViewModel
import com.example.tasktracker.services.firebase.UserViewModel
import com.ravenzip.workshop.components.SnackBar
import com.ravenzip.workshop.components.TopAppBar

@Composable
fun CompanyScreenScaffold(
    padding: PaddingValues,
    vararg onClick: () -> Unit,
    companyViewModel: CompanyViewModel,
    userViewModel: UserViewModel
) {
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = { TopAppBar(title = "Организация", backArrow = null, items = listOf()) },
    ) { innerPadding ->
        CompanyScreen(
            padding = innerPadding,
            snackBarHostState = snackBarHostState,
            onClick = onClick,
            companyViewModel = companyViewModel,
            userViewModel = userViewModel
        )
    }

    SnackBar(snackBarHostState)
}
