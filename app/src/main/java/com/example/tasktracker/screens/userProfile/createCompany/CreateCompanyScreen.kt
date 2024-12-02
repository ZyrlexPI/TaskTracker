package com.example.tasktracker.screens.userProfile.createCompany

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
import androidx.compose.ui.unit.sp
import com.example.tasktracker.services.firebase.CompanyViewModel
import com.example.tasktracker.services.firebase.UserViewModel
import com.example.tasktracker.services.showError
import com.example.tasktracker.services.showSuccess
import com.ravenzip.workshop.components.SimpleButton
import com.ravenzip.workshop.components.SinglenessOutlinedTextField
import com.ravenzip.workshop.data.TextConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CreateCompanyScreen(
    padding: PaddingValues,
    snackBarHostState: SnackbarHostState,
    vararg onClick: () -> Unit,
    userViewModel: UserViewModel,
    companyViewModel: CompanyViewModel,
) {
    val nameCompany = remember { mutableStateOf("") }
    val userData = userViewModel.dataUser.collectAsState().value
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.fillMaxSize().padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        SinglenessOutlinedTextField(text = nameCompany, label = "Название компании")
        Spacer(modifier = Modifier.height(40.dp))
        SimpleButton(
            text = "Создать",
            textConfig = TextConfig(size = 19.sp, align = TextAlign.Center),
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
                    companyViewModel.add(nameCompany = nameCompany.value, userData = userData)
                    //                    userViewModel.get(getUser())
                    //                    companyViewModel.getCurrentCompany(userData)
                    snackBarHostState.showSuccess(message = "Организация успешно создана")

                    onClick[0]()
                } else {
                    snackBarHostState.showError(message = "Проверьте правильность заполнения полей")
                }
            }
        }
    }
}
