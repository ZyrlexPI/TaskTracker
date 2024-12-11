package com.example.tasktracker.screens.userProfile.company

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.tasktracker.viewModels.CompanyViewModel
import com.example.tasktracker.viewModels.UserViewModel
import com.ravenzip.workshop.components.AlertDialog
import com.ravenzip.workshop.components.SnackBar
import com.ravenzip.workshop.components.Spinner
import com.ravenzip.workshop.components.TopAppBar
import com.ravenzip.workshop.data.appbar.AppBarItem
import com.ravenzip.workshop.data.icon.Icon
import com.ravenzip.workshop.data.icon.IconConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CompanyScreenScaffold(
    padding: PaddingValues,
    vararg onClick: () -> Unit,
    companyViewModel: CompanyViewModel,
    userViewModel: UserViewModel,
) {
    val scope = rememberCoroutineScope()

    val userData = userViewModel.dataUser.collectAsState().value
    val companyData = companyViewModel.dataCompany.collectAsState().value

    val snackBarHostState = remember { SnackbarHostState() }

    val editState = remember { mutableStateOf(false) }

    val isLoading = remember { mutableStateOf(false) }
    val spinnerText = remember { mutableStateOf("Удаление организации...") }

    val alertState = remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = {
            TopAppBar(
                title = "Организация",
                backArrow = null,
                items =
                    if (userData.id == companyData.creatorId) {
                        generateTopAppBarItems(scope, alertState, editState, isLoading)
                    } else {
                        emptyList()
                    },
            )
        },
    ) { innerPadding ->
        CompanyScreen(
            padding = innerPadding,
            snackBarHostState = snackBarHostState,
            onClick = onClick,
            companyViewModel = companyViewModel,
            userViewModel = userViewModel,
            editState = editState
        )
    }

    if (alertState.value) {

        AlertDialog(
            title = "Удалить организацию?",
            text =
                "Вы уверены, что хотите удалить организацию?\nПри удалении организации будут удалены все задачи и данные связанные с ними. Также все пользователи покинут организацию.",
            onDismissText = "Отмена",
            onConfirmationText = "Удалить",
            onDismiss = { alertState.value = false },
            onConfirmation = {
                scope.launch {
                    isLoading.value = true
                    spinnerText.value = "Удаление организации..."
                    alertState.value = false
                    companyViewModel.deleteCompany(companyData.id)
                    userViewModel.setUserData()
                    isLoading.value = false
                }
            }
        )
    }

    if (isLoading.value) {
        Spinner(
            text = spinnerText.value,
        )
    }

    SnackBar(snackBarHostState)
}

fun generateTopAppBarItems(
    scope: CoroutineScope,
    alertState: MutableState<Boolean>,
    editState: MutableState<Boolean>,
    isLoading: MutableState<Boolean>,
): List<AppBarItem> {

    val editButton =
        AppBarItem(
            icon = Icon.ImageVectorIcon(Icons.Filled.Edit),
            iconConfig = IconConfig.Small,
            onClick = { editState.value = !editState.value },
        )

    val deleteButton =
        AppBarItem(
            icon = Icon.ImageVectorIcon(Icons.Filled.Delete),
            iconConfig = IconConfig.Small,
            onClick = { alertState.value = true },
        )

    return listOf(editButton, deleteButton)
}
