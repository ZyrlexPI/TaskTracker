package com.example.tasktracker.screens.userProfile.changePassword

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tasktracker.services.showError
import com.example.tasktracker.services.showSuccess
import com.example.tasktracker.viewModels.AuthViewModel
import com.ravenzip.workshop.components.InfoCard
import com.ravenzip.workshop.components.SimpleButton
import com.ravenzip.workshop.components.SinglenessOutlinedTextField
import com.ravenzip.workshop.data.TextConfig
import com.ravenzip.workshop.data.icon.Icon
import com.ravenzip.workshop.data.icon.IconConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ChangePasswordScreen(
    padding: PaddingValues,
    snackBarHostState: SnackbarHostState,
    authViewModel: AuthViewModel = hiltViewModel<AuthViewModel>(),
    onClick: () -> Unit
) {
    val oldPassword = remember { mutableStateOf("") }
    val newPassword = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        SinglenessOutlinedTextField(
            text = oldPassword,
            label = "Текущий пароль",
            isHiddenText = true
        )
        Spacer(modifier = Modifier.height(10.dp))
        SinglenessOutlinedTextField(text = newPassword, label = "Новый пароль", isHiddenText = true)
        Spacer(modifier = Modifier.height(20.dp))
        InfoCard(
            icon = Icon.ImageVectorIcon(Icons.Outlined.Info),
            title = "Информация о смене пароля",
            titleConfig = TextConfig(size = 18.sp),
            text =
                "Укажите текущий и желаемый пароль в соответсвующих полях, а затем нажмите на кнопку \"Обновить пароль\".",
            textConfig = TextConfig(size = 15.sp),
            iconConfig = IconConfig.PrimarySmall,
        )
        Spacer(modifier = Modifier.height(20.dp))
        SimpleButton(
            text = "Обновить пароль",
            textConfig = TextConfig(size = 19.sp, align = TextAlign.Center)
        ) {
            scope.launch(Dispatchers.Main) {
                if (authViewModel.updatePasswordUser(oldPassword.value, newPassword.value)) {
                    snackBarHostState.showSuccess(message = "Пароль успешно обновлен")
                    onClick()
                } else {
                    snackBarHostState.showError(
                        message =
                            "Ошибка при обновлении пароля. Проверьте правильность заполнения полей"
                    )
                }
            }
        }
    }
}
