package com.example.tasktracker.screens.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasktracker.services.ValidationService
import com.example.tasktracker.services.firebase.reloadUser
import com.example.tasktracker.services.firebase.sendPasswordResetEmail
import com.example.tasktracker.services.showError
import com.example.tasktracker.services.showSuccess
import com.ravenzip.workshop.components.InfoCard
import com.ravenzip.workshop.components.SimpleButton
import com.ravenzip.workshop.components.SinglenessTextField
import com.ravenzip.workshop.components.SnackBar
import com.ravenzip.workshop.components.Spinner
import com.ravenzip.workshop.data.IconParameters
import com.ravenzip.workshop.data.TextParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RecoveryScreen(navigateToAuthScreen: () -> Unit) {
    val email = remember { mutableStateOf("") }
    val isEmailValid = remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val validationService = ValidationService()
    val isLoading = remember { mutableStateOf(false) }
    val spinnerText = remember { mutableStateOf("Обработка запроса") }
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Восстановление",
            modifier = Modifier.fillMaxWidth(0.9f),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(20.dp))
        SinglenessTextField(text = email, label = "Электронная почта")
        Spacer(modifier = Modifier.height(10.dp))
        InfoCard(
            icon = IconParameters(value = Icons.Outlined.Info),
            title = TextParameters(value = "Информация о восстановлении", size = 18),
            text =
                TextParameters(
                    value =
                        "Укажите электронную почту привязанную к аккаунту, на него придет письмо для сброса пароля.",
                    size = 15
                ),
            isTitleUnderIcon = false,
        )
        BottomContainer {
            Spacer(modifier = Modifier.height(30.dp))
            SimpleButton(
                text = TextParameters(value = "Восстановить", size = 19),
            ) {
                scope.launch(Dispatchers.Main) {
                    isEmailValid.value = validationService.isEmailValid(email.value)

                    if (!isEmailValid.value) {
                        snackBarHostState.showError("Проверьте правильность заполнения поля")
                        return@launch
                    }
                    isLoading.value = true

                    val isReloadSuccess = reloadUser()
                    if (isReloadSuccess.value != true) {
                        isLoading.value = false
                        snackBarHostState.showError(isReloadSuccess.error!!)
                        return@launch
                    }

                    val resetResult = sendPasswordResetEmail(email.value)
                    isLoading.value = false
                    if (resetResult) {
                        snackBarHostState.showSuccess(
                            "Письмо со ссылкой для сброса было успешно отправлено на почту"
                        )
                    } else {
                        snackBarHostState.showError("Ошибка сброса пароля")
                    }
                }
            }
            Spacer(modifier = Modifier.height(50.dp))
        }
    }

    if (isLoading.value) {
        Spinner(text = TextParameters(value = spinnerText.value, size = 16))
    }

    SnackBar(snackBarHostState = snackBarHostState)
}
