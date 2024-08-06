package com.example.tasktracker.screens.userProfile.company

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasktracker.data.Company
import com.example.tasktracker.data.User
import com.example.tasktracker.services.firebase.CompanyService
import com.example.tasktracker.services.firebase.UserService
import com.example.tasktracker.services.showError
import com.example.tasktracker.services.showSuccess
import com.ravenzip.workshop.components.InfoCard
import com.ravenzip.workshop.data.IconParameters
import com.ravenzip.workshop.data.TextParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun JoinCompanyScreen(
    padding: PaddingValues,
    snackBarHostState: SnackbarHostState,
    vararg onClick: () -> Unit,
    userService: MutableState<UserService>,
    companyService: MutableState<CompanyService>,
) {
    val userData = remember { mutableStateOf(User()) }
    userData.value = userService.value.dataUser.collectAsState().value
    val scope = rememberCoroutineScope()

    val listCompanies = remember { mutableListOf<Company>() }
    val isLoadingList = remember { mutableStateOf(true) }
    LaunchedEffect(isLoadingList.value) {
        if (isLoadingList.value) {
            listCompanies.addAll(companyService.value.getListCompany())
            isLoadingList.value = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        InfoCard(
            icon = IconParameters(value = Icons.Outlined.Info),
            title = TextParameters(value = "Информация", size = 19),
            text =
                TextParameters(
                    value =
                        "Выберите организацию из списка ниже, к которой желаете присоединиться.",
                    size = 15
                ),
            isTitleUnderIcon = false
        )
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn {
            item {
                Text(
                    text = "Доступные организации:",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            items(listCompanies) { company ->
                Card(
                    onClick = {
                        scope.launch(Dispatchers.Main) {
                            if (userData.value.companyId == "") {
                                companyService.value.joinСompany(
                                    company.id,
                                    userData = userData.value
                                )
                                snackBarHostState.showSuccess(
                                    message = "Вы успешно присоединились к организации"
                                )
                                onClick[0]()
                            } else {
                                snackBarHostState.showError(
                                    message =
                                        "Ошибка выполнения запроса. Возможно вы уже состоите в организации"
                                )
                                onClick[0]()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Text(
                        company.name,
                        fontSize = 19.sp,
                        modifier = Modifier.padding(start = 30.dp, top = 10.dp, bottom = 10.dp)
                    )
                }
            }
        }
    }
}
