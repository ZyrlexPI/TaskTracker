package com.example.tasktracker.screens.userProfile.createCompany

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
fun CreateCompanyScreenScaffold(
    padding: PaddingValues,
    vararg onClick: () -> Unit,
    userViewModel: UserViewModel,
    companyViewModel: CompanyViewModel,
) {
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = { TopAppBar(title = "Добавить организацию", backArrow = null, items = listOf()) },
    ) { innerPadding ->
        CreateCompanyScreen(
            padding = innerPadding,
            snackBarHostState = snackBarHostState,
            onClick = onClick,
            userViewModel = userViewModel,
            companyViewModel = companyViewModel,
        )
    }

    SnackBar(snackBarHostState)
}
