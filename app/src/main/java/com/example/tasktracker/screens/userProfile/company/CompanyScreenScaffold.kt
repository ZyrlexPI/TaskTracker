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
import androidx.compose.ui.Modifier
import com.example.tasktracker.viewModels.CompanyViewModel
import com.example.tasktracker.viewModels.UserViewModel
import com.ravenzip.workshop.components.SnackBar
import com.ravenzip.workshop.components.TopAppBar
import com.ravenzip.workshop.data.appbar.AppBarItem
import com.ravenzip.workshop.data.icon.Icon
import com.ravenzip.workshop.data.icon.IconConfig

@Composable
fun CompanyScreenScaffold(
    padding: PaddingValues,
    vararg onClick: () -> Unit,
    companyViewModel: CompanyViewModel,
    userViewModel: UserViewModel
) {
    val userData = userViewModel.dataUser.collectAsState().value
    val companyData = companyViewModel.dataCompany.collectAsState().value

    val snackBarHostState = remember { SnackbarHostState() }

    val editState = remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = {
            TopAppBar(
                title = "Организация",
                backArrow = null,
                items =
                    if (userData.id == companyData.creatorId) {
                        generateTopAppBarItems(editState)
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

    SnackBar(snackBarHostState)
}

fun generateTopAppBarItems(
    editState: MutableState<Boolean>,
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
            onClick = {
                //                scope.launch {
                //                    tasksViewModel.delete(taskInfo)
                //                    tasksViewModel.updateListTask()
                //                    userViewModel.updateLastTaskViewId(userData, "")
                //                    userViewModel.setUserData(getUser())
                //                    returnToTaskList()
                //                }
            },
        )

    return listOf(editButton, deleteButton)
}
