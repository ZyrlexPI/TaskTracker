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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tasktracker.services.ValidationService
import com.example.tasktracker.services.firebase.UserViewModel
import com.example.tasktracker.services.firebase.createUserWithEmail
import com.example.tasktracker.services.firebase.getUser
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
fun RegistrationScreen(
    navigateToHomeScreen: () -> Unit,
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val isEmailValid = remember { mutableStateOf(true) }
    val isPasswordValid = remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val isLoading = remember { mutableStateOf(false) }
    val spinnerText = remember { mutableStateOf("Регистрация") }
    val validationService = ValidationService()
    val userViewModel = hiltViewModel<UserViewModel>()
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Регистрация",
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
                text = "Зарегистрироваться",
                textConfig = TextConfig(size = 19.sp, align = TextAlign.Center),
            ) {
                scope.launch(Dispatchers.Main) {
                    isEmailValid.value = validationService.isEmailValid(email.value)
                    isPasswordValid.value = validationService.isPasswordValid(password.value)

                    if (!isEmailValid.value) {
                        snackBarHostState.showError("Некорректный email")
                        return@launch
                    }

                    if (!isPasswordValid.value) {
                        snackBarHostState.showError("В пароле должно быть минимум 8 символов")
                        return@launch
                    }
                    isLoading.value = true

                    val isReloadSuccess = reloadUser()
                    if (isReloadSuccess.value != true) {
                        isLoading.value = false
                        snackBarHostState.showError(isReloadSuccess.error!!)
                        return@launch
                    }

                    spinnerText.value = "Регистрация..."
                    val authResult = createUserWithEmail(email.value, password.value)

                    if (authResult.value == null) {
                        isLoading.value = false
                        snackBarHostState.showError(authResult.error!!)
                        return@launch
                    }

                    isLoading.value = false
                    userViewModel.add(getUser())
                    navigateToHomeScreen()
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
