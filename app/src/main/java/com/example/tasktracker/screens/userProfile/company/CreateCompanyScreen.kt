package com.example.tasktracker.screens.userProfile.company

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.tasktracker.services.firebase.CompanyService
import com.example.tasktracker.services.firebase.UserService
import com.example.tasktracker.services.firebase.getUser
import com.example.tasktracker.services.showError
import com.example.tasktracker.services.showSuccess
import com.ravenzip.workshop.components.SimpleButton
import com.ravenzip.workshop.components.SinglenessTextField
import com.ravenzip.workshop.components.SnackBar
import com.ravenzip.workshop.data.TextParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CreateCompanyScreen(
    padding: PaddingValues,
    vararg onClick: () -> Unit,
    userService: MutableState<UserService>
) {
    val companyService = CompanyService()
    val nameCompany = remember { mutableStateOf("") }
    val userData = userService.value.dataUser.collectAsState().value
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Создание организации",
            modifier = Modifier.fillMaxWidth(0.9f),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(30.dp))
        SinglenessTextField(text = nameCompany, label = "Название компании")
        Spacer(modifier = Modifier.height(40.dp))
        SimpleButton(
            text = TextParameters(value = "Создать", size = 19),
        ) {
            scope.launch(Dispatchers.Main) {
                if (userData.companyId != "") {
                    snackBarHostState.showError(
                        message =
                            "Ошибка выполнения запроса. Возможно вы уже состоите в организации"
                    )
                    onClick[0]()
                }
                if (nameCompany.value != "") {
                    companyService.add(nameCompany = nameCompany.value, userData = userData)
                    userService.value.get(getUser())
                    snackBarHostState.showSuccess(message = "Организация успешно создана")

                    onClick[0]()
                } else {
                    snackBarHostState.showError(message = "Проверьте правильность заполнения полей")
                }
            }
        }
    }
    SnackBar(snackBarHostState = snackBarHostState)
}
