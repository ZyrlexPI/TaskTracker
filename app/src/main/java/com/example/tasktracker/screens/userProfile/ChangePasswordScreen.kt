package com.example.tasktracker.screens.userProfile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import com.example.tasktracker.services.firebase.updatePasswordUser
import com.example.tasktracker.services.showError
import com.example.tasktracker.services.showSuccess
import com.ravenzip.workshop.components.InfoCard
import com.ravenzip.workshop.components.SimpleButton
import com.ravenzip.workshop.components.SinglenessTextField
import com.ravenzip.workshop.components.SnackBar
import com.ravenzip.workshop.data.IconParameters
import com.ravenzip.workshop.data.TextParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ChangePasswordScreen(padding: PaddingValues) {
    val oldPassword = remember { mutableStateOf("") }
    val newPassword = remember { mutableStateOf("") }
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Смена пароля",
            modifier = Modifier.fillMaxWidth(0.9f),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(20.dp))
        SinglenessTextField(text = oldPassword, label = "Текущий пароль", isHiddenText = true)
        Spacer(modifier = Modifier.height(10.dp))
        SinglenessTextField(text = newPassword, label = "Новый пароль", isHiddenText = true)
        Spacer(modifier = Modifier.height(20.dp))
        InfoCard(
            icon = IconParameters(value = Icons.Outlined.Info),
            title = TextParameters(value = "Информация о смене пароля", size = 18),
            text =
                TextParameters(
                    value =
                        "Укажите текущий и желаемый пароль в соответсвующих полях, а затем нажмите на кнопку \"Обновить пароль\".",
                    size = 15
                ),
            isTitleUnderIcon = false
        )
        Spacer(modifier = Modifier.height(20.dp))
        SimpleButton(text = TextParameters("Обновить пароль", size = 19)) {
            scope.launch(Dispatchers.Main) {
                if (updatePasswordUser(oldPassword.value, newPassword.value)) {
                    snackBarHostState.showSuccess(message = "Пароль успешно обновлен")
                } else {
                    snackBarHostState.showError(message = "Ошибка при обновлении пароля")
                }
            }
        }
    }
    SnackBar(snackBarHostState = snackBarHostState)
}
