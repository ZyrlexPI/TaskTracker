package com.example.tasktracker.screens.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.example.tasktracker.services.firebase.logInUserWithEmail
import com.example.tasktracker.services.firebase.reloadUser
import com.example.tasktracker.services.showError
import com.ravenzip.workshop.components.SimpleButton
import com.ravenzip.workshop.components.SinglenessOutlinedTextField
import com.ravenzip.workshop.components.SnackBar
import com.ravenzip.workshop.components.Spinner
import com.ravenzip.workshop.data.TextConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AuthorizationScreen(
    navigateToHomeScreen: () -> Unit,
    navigateToRegistrationScreen: () -> Unit,
    navigateToRecoveryScreen: () -> Unit,
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val isEmailValid = remember { mutableStateOf(true) }
    val isPasswordValid = remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val isLoading = remember { mutableStateOf(false) }
    val spinnerText = remember { mutableStateOf("Авторизация") }
    val validationService = ValidationService()

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Добро пожаловать в",
            modifier = Modifier.fillMaxWidth(0.9f),
            fontSize = 31.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Task Tracker",
            modifier = Modifier.fillMaxWidth(0.9f),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(20.dp))
        SinglenessOutlinedTextField(text = email, label = "Электронная почта")
        Spacer(modifier = Modifier.height(20.dp))
        SinglenessOutlinedTextField(text = password, label = "Пароль", isHiddenText = true)

        BottomContainer {
            Spacer(modifier = Modifier.height(30.dp))
            SimpleButton(
                text = "Вход",
                textConfig = TextConfig(size = 19.sp, align = TextAlign.Center),
            ) {
                scope.launch(Dispatchers.Main) {
                    isEmailValid.value = validationService.isEmailValid(email.value)
                    isPasswordValid.value = validationService.isPasswordValid(password.value)

                    if (!isEmailValid.value || !isPasswordValid.value) {
                        snackBarHostState.showError("Проверьте правильность заполнения полей")
                        return@launch
                    }
                    isLoading.value = true

                    val isReloadSuccess = reloadUser()
                    if (isReloadSuccess.value != true) {
                        isLoading.value = false
                        snackBarHostState.showError(isReloadSuccess.error!!)
                        return@launch
                    }

                    val authResult = logInUserWithEmail(email.value, password.value)

                    if (authResult.value == null) {
                        isLoading.value = false
                        snackBarHostState.showError(authResult.error!!)
                        return@launch
                    }

                    isLoading.value = false
                    navigateToHomeScreen()
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            SimpleButton(
                text = "Восстановить аккаунт",
                textConfig = TextConfig(size = 19.sp, align = TextAlign.Center),
            ) {
                navigateToRecoveryScreen()
            }
            Spacer(modifier = Modifier.height(20.dp))
            SimpleButton(
                text = "Регистрация",
                textConfig = TextConfig(size = 19.sp, align = TextAlign.Center),
            ) {
                navigateToRegistrationScreen()
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
