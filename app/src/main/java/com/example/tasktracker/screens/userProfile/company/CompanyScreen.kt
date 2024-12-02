package com.example.tasktracker.screens.userProfile.company

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Output
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasktracker.services.firebase.CompanyViewModel
import com.example.tasktracker.services.firebase.UserViewModel
import com.example.tasktracker.services.firebase.getUser
import com.example.tasktracker.services.showSuccess
import com.ravenzip.workshop.components.RowIconButton
import com.ravenzip.workshop.data.TextConfig
import com.ravenzip.workshop.data.icon.Icon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CompanyScreen(
    padding: PaddingValues,
    snackBarHostState: SnackbarHostState,
    vararg onClick: () -> Unit,
    companyViewModel: CompanyViewModel,
    userViewModel: UserViewModel
) {
    val companyData = companyViewModel.dataCompany.collectAsStateWithLifecycle().value
    val userData = userViewModel.dataUser.collectAsState().value
    val scope = rememberCoroutineScope()
    Log.d("CompanyScreen_CD", companyData.toString())
    Log.d("CompanyScreen_UD", userData.toString())

    LaunchedEffect(Unit) { companyViewModel.getCurrentCompany(userData) }

    Column(
        modifier = Modifier.fillMaxSize().padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text =
                if (companyData.name != "") {
                    "Вы состоите в организации \"" + companyData.name + "\""
                } else {
                    "Вы не состоите в организации"
                },
            fontSize = 20.sp,
            modifier = Modifier.fillMaxWidth(0.85f)
        )
        Spacer(modifier = Modifier.height(30.dp))
        // TODO Добавить условие того есть ли организация, тогда отобразить кнопку выйти из
        // организации, иначе отображать создание и присоединение
        if (companyData.id == "") {
            RowIconButton(
                text = "Присоединиться",
                textConfig = TextConfig(size = 19.sp),
                icon = Icon.ImageVectorIcon(Icons.Outlined.Add),
            ) {
                onClick[0]()
            }
            Spacer(modifier = Modifier.height(20.dp))
            RowIconButton(
                text = "Создать",
                textConfig = TextConfig(size = 19.sp),
                icon = Icon.ImageVectorIcon(Icons.Outlined.Create),
            ) {
                onClick[1]()
            }
        } else {
            RowIconButton(
                text = "Выйти из организации",
                textConfig = TextConfig(size = 19.sp),
                icon = Icon.ImageVectorIcon(Icons.Outlined.Output),
            ) {
                scope.launch(Dispatchers.Main) {
                    companyViewModel.deleteCurrentUser(userData)
                    userViewModel.get(getUser())
                    Log.d("ExitInCompany", userData.toString())
                    snackBarHostState.showSuccess(message = "Вы успешно вышли из организации")
                    onClick[2]()
                }
            }
        }
    }
}
