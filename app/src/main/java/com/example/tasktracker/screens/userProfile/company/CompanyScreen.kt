package com.example.tasktracker.screens.userProfile.company

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Output
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasktracker.data.Company
import com.example.tasktracker.data.User
import com.example.tasktracker.services.firebase.CompanyService
import com.example.tasktracker.services.firebase.UserService
import com.example.tasktracker.services.firebase.getUser
import com.example.tasktracker.services.showSuccess
import com.ravenzip.workshop.components.RowIconButton
import com.ravenzip.workshop.components.SnackBar
import com.ravenzip.workshop.data.IconParameters
import com.ravenzip.workshop.data.TextParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CompanyScreen(
    padding: PaddingValues,
    vararg onClick: () -> Unit,
    companyService: MutableState<CompanyService>,
    userService: MutableState<UserService>
) {
    val companyData = remember { mutableStateOf(Company()) }
    companyData.value = companyService.value.dataCompany.collectAsState().value
    val userData = remember { mutableStateOf(User()) }
    userData.value = userService.value.dataUser.collectAsState().value
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val isLoadingUser = remember { mutableStateOf(true) }
    val isLoadingCompany = remember { mutableStateOf(false) }
    LaunchedEffect(isLoadingUser.value) {
        if (isLoadingUser.value) {
            userService.value.get(getUser())
            isLoadingCompany.value = true
            isLoadingUser.value = false
        }
    }
    LaunchedEffect(isLoadingCompany.value) {
        if (isLoadingCompany.value) {
            companyService.value.getCurrentCompany(userData.value)
            isLoadingCompany.value = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Организация",
            modifier = Modifier.fillMaxWidth(0.9f),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text =
                if (companyData.value.name != "") {
                    "Вы состоите в организации \"" + companyData.value.name + "\""
                } else {
                    "Вы не состоите в организации"
                },
            fontSize = 20.sp,
            modifier = Modifier.fillMaxWidth(0.85f)
        )
        Spacer(modifier = Modifier.height(30.dp))
        // TODO Добавить условие того есть ли организация, тогда отобразить кнопку выйти из
        // организации, иначе отображать создание и присоединение
        if (companyData.value.id == "") {
            RowIconButton(
                text = TextParameters("Присоединиться", 19),
                icon = IconParameters(Icons.Outlined.Add)
            ) {
                onClick[0]()
            }
            Spacer(modifier = Modifier.height(20.dp))
            RowIconButton(
                text = TextParameters("Создать", 19),
                icon = IconParameters(Icons.Outlined.Create)
            ) {
                onClick[1]()
            }
        } else {
            RowIconButton(
                text = TextParameters("Выйти из организации", 19),
                icon = IconParameters(Icons.Outlined.Output)
            ) {
                scope.launch(Dispatchers.Main) {
                    companyService.value.deleteCurrentUser(userData.value)
                    snackBarHostState.showSuccess(message = "Вы успешно вышли из организации")
                    onClick[2]()
                }
            }
        }
    }
    SnackBar(snackBarHostState = snackBarHostState)
}
