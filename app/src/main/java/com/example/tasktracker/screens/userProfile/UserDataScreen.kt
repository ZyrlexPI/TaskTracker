package com.example.tasktracker.screens.userProfile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tasktracker.services.firebase.UserViewModel
import com.example.tasktracker.services.firebase.getUser
import com.example.tasktracker.services.showError
import com.example.tasktracker.services.showSuccess
import com.ravenzip.workshop.components.SimpleButton
import com.ravenzip.workshop.components.SinglenessTextField
import com.ravenzip.workshop.data.TextConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun UserDataScreen(
    padding: PaddingValues,
    snackBarHostState: SnackbarHostState,
    userViewModel: UserViewModel
) {
    val userData = userViewModel.dataUser.collectAsState().value
    val name = remember { mutableStateOf(userData.name) }
    val surname = remember { mutableStateOf(userData.surname) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        SinglenessTextField(text = name, label = "Имя")
        Spacer(modifier = Modifier.height(20.dp))
        SinglenessTextField(text = surname, label = "Фамилия")
        Spacer(modifier = Modifier.height(40.dp))
        SimpleButton(
            text = "Сохранить",
            textConfig = TextConfig(size = 19, align = TextAlign.Center)
        ) {
            scope.launch(Dispatchers.Main) {
                if (name.value == "" || surname.value == "") {
                    snackBarHostState.showError("Проверьте правильность заполнения полей")
                    return@launch
                }

                if (
                    userViewModel.update(
                        userData = userData,
                        name = name.value,
                        surname = surname.value
                    )
                ) {
                    userViewModel.get(getUser())
                    snackBarHostState.showSuccess(message = "Данные пользователя успешно обновлены")
                } else {
                    snackBarHostState.showError(message = "Ошибка при обновлении данных")
                }
            }
        }
    }
}
