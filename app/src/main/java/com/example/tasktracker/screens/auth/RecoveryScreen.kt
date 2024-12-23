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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasktracker.services.ValidationService
import com.example.tasktracker.services.showError
import com.example.tasktracker.services.showSuccess
import com.example.tasktracker.services.viewModels.reloadUser
import com.example.tasktracker.services.viewModels.sendPasswordResetEmail
import com.ravenzip.workshop.components.InfoCard
import com.ravenzip.workshop.components.SimpleButton
import com.ravenzip.workshop.components.SinglenessOutlinedTextField
import com.ravenzip.workshop.components.SnackBar
import com.ravenzip.workshop.components.Spinner
import com.ravenzip.workshop.data.TextConfig
import com.ravenzip.workshop.data.icon.Icon
import com.ravenzip.workshop.data.icon.IconConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RecoveryScreen(
    navigateToAuthScreen: () -> Unit,
) {
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
        SinglenessOutlinedTextField(text = email, label = "Электронная почта")
        Spacer(modifier = Modifier.height(10.dp))
        InfoCard(
            icon = Icon.ImageVectorIcon(Icons.Outlined.Info),
            iconConfig = IconConfig.PrimarySmall,
            title = "Информация о восстановлении",
            titleConfig = TextConfig.H2,
            text =
                "Укажите электронную почту привязанную к аккаунту, на него придет письмо для сброса пароля.",
            textConfig = TextConfig(size = 15.sp),
        )
        BottomContainer {
            Spacer(modifier = Modifier.height(30.dp))
            SimpleButton(
                text = "Восстановить",
                textConfig = TextConfig(size = 19.sp, align = TextAlign.Center),
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
        Spinner(
            text = spinnerText.value,
        )
    }

    SnackBar(snackBarHostState = snackBarHostState)
}
