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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasktracker.services.ValidationService
import com.ravenzip.workshop.components.SimpleButton
import com.ravenzip.workshop.components.SinglenessTextField
import com.ravenzip.workshop.components.SnackBar
import com.ravenzip.workshop.components.Spinner
import com.ravenzip.workshop.data.TextParameters

@Composable
fun AuthorizationScreen(
    navigateToHomeScreen: () -> Unit,
    navigateToRegistrationScreen: () -> Unit,
    navigateToRecoveryScreen: () -> Unit
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val isEmailValid = remember { mutableStateOf(true) }
    val isPasswordValid = remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val isLoading = remember { mutableStateOf(false) }
    val spinnerText = remember { mutableStateOf("Вход в аккаунт...") }
    val validationService = ValidationService()
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Добро пожаловать в",
            modifier = Modifier.fillMaxWidth(0.9f),
            fontSize = 25.sp,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Task Tracker",
            modifier = Modifier.fillMaxWidth(0.9f),
            fontSize = 25.sp,
        )
        Spacer(modifier = Modifier.height(20.dp))
        SinglenessTextField(text = email, label = "Электронная почта")
        Spacer(modifier = Modifier.height(20.dp))
        SinglenessTextField(text = password, label = "Пароль", isHiddenText = true)
        BottomContainer {
            Spacer(modifier = Modifier.height(20.dp))
            SimpleButton(
                text = TextParameters(value = "Войти", size = 16),
            ) {}
            Spacer(modifier = Modifier.height(20.dp))
            SimpleButton(
                text = TextParameters(value = "Восстановить аккаунт", size = 16),
            ) {}
            Spacer(modifier = Modifier.height(20.dp))
            SimpleButton(
                text = TextParameters(value = "Регистрация", size = 16),
            ) {}
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (isLoading.value) {
        Spinner(text = TextParameters(value = spinnerText.value, size = 16))
    }

    SnackBar(snackBarHostState = snackBarHostState)
}
