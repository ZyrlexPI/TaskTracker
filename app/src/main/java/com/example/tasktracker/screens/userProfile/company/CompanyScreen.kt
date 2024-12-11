package com.example.tasktracker.screens.userProfile.company

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Output
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasktracker.data.Company
import com.example.tasktracker.data.User
import com.example.tasktracker.services.showError
import com.example.tasktracker.services.showSuccess
import com.example.tasktracker.viewModels.CompanyViewModel
import com.example.tasktracker.viewModels.UserViewModel
import com.ravenzip.workshop.components.DropDownTextField
import com.ravenzip.workshop.components.Icon
import com.ravenzip.workshop.components.RowIconButton
import com.ravenzip.workshop.components.SimpleButton
import com.ravenzip.workshop.components.SinglenessOutlinedTextField
import com.ravenzip.workshop.components.Spinner
import com.ravenzip.workshop.data.TextConfig
import com.ravenzip.workshop.data.icon.Icon
import com.ravenzip.workshop.data.icon.IconConfig
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyScreen(
    padding: PaddingValues,
    snackBarHostState: SnackbarHostState,
    vararg onClick: () -> Unit,
    companyViewModel: CompanyViewModel,
    userViewModel: UserViewModel,
    editState: MutableState<Boolean>,
) {
    val scope = rememberCoroutineScope()

    val companyData = companyViewModel.dataCompany.collectAsStateWithLifecycle().value
    val userData = userViewModel.dataUser.collectAsState().value

    val listMembersCompany = companyViewModel.listMembersCompany.collectAsStateWithLifecycle().value

    val selectUser = remember { mutableStateOf<User?>(null) }
    val listForNewCreator =
        listMembersCompany.filter { member -> member.id != companyData.creatorId }
    Log.d("CompanyScreen", listForNewCreator.toString())

    Log.d("CompanyScreen_CD", companyData.toString())
    Log.d("CompanyScreen_UD", userData.toString())
    val refreshState = rememberPullToRefreshState()
    val isRefreshing = remember { mutableStateOf(false) }

    val alertState = remember { mutableStateOf(false) }
    val alertText = remember { mutableStateOf("") }
    val alertConfirmationText = remember { mutableStateOf("Сохранить") }

    val isLoading = remember { mutableStateOf(false) }
    val spinnerText = remember { mutableStateOf("Смена владельца...") }

    PullToRefreshBox(
        isRefreshing = isRefreshing.value,
        onRefresh = {
            scope.launch {
                isRefreshing.value = true
                companyViewModel.getMembersCompany(companyData.id)
                userViewModel.setUserData()
                delay(1.seconds)
                isRefreshing.value = false
            }
        },
        modifier = Modifier.padding(padding),
        state = refreshState,
    ) {
        if (userData.name != "" && userData.surname != "") {

            if (companyData.id == "") {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Вы не состоите в организации",
                        fontSize = 20.sp,
                        modifier = Modifier.fillMaxWidth(0.85f)
                    )
                    Spacer(modifier = Modifier.height(30.dp))
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
                }
            } else {
                if (editState.value) {

                    EditCompanyScreen(
                        companyData,
                        companyViewModel,
                        editState,
                        alertState,
                        alertText,
                        alertConfirmationText,
                        snackBarHostState,
                    )
                } else {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        item {
                            Text(
                                text = "Вы состоите в организации \"" + companyData.name + "\"",
                                fontSize = 20.sp,
                                modifier = Modifier.fillMaxWidth(0.85f)
                            )
                        }

                        item {
                            RowIconButton(
                                text = "Выйти из организации",
                                textConfig = TextConfig(size = 19.sp),
                                icon = Icon.ImageVectorIcon(Icons.Outlined.Output),
                            ) {
                                if (companyData.creatorId == userData.id) {
                                    if (companyData.members.count() > 1) {
                                        alertConfirmationText.value = "Выйти"
                                        alertText.value =
                                            "Чтобы выйти из организации, выберите нового владельца из списка ниже."
                                        alertState.value = true
                                    } else {
                                        scope.launch {
                                            snackBarHostState.showError(
                                                message =
                                                    "Нельзя выйти из организации, так как вы владелец. Удалите организацию."
                                            )
                                        }
                                    }
                                } else {
                                    scope.launch(Dispatchers.Main) {
                                        companyViewModel.deleteCurrentUser(userData)
                                        userViewModel.setUserData()
                                        Log.d("ExitInCompany", userData.toString())
                                        snackBarHostState.showSuccess(
                                            message = "Вы успешно вышли из организации"
                                        )
                                        onClick[2]()
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                text = "Список сотрудников",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }

                        items(listMembersCompany) { member ->
                            UserCard(
                                user = member,
                                member.id == companyData.creatorId,
                                onClick = {
                                    scope.launch {
                                        companyViewModel.setCurrentUser(member)
                                        onClick[4]()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text =
                        "Ваши личные данные не настроены.\nНастройте их в профиле или перейдите по кнопке ниже",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.fillMaxWidth(0.85f)
                )
                Spacer(modifier = Modifier.height(30.dp))

                RowIconButton(
                    text = "Личные данные",
                    textConfig = TextConfig(size = 19.sp),
                    icon = Icon.ImageVectorIcon(Icons.Outlined.Person),
                ) {
                    onClick[3]()
                }
            }
        }
    }
    if (alertState.value) {
        AlertDialogEditCreator(
            title = "Смена владельца",
            text = alertText.value,
            onDismissText = "Отмена",
            onConfirmationText = alertConfirmationText.value,
            onDismiss = { alertState.value = false },
            onConfirmation = {
                scope.launch {
                    alertState.value = false
                    if (selectUser.value == null) {
                        snackBarHostState.showError(
                            message = "Ошибка выбора пользователя. Повторите попытку"
                        )
                    } else {
                        companyViewModel.changeCreatorCompany(companyData.id, selectUser.value!!.id)
                        companyViewModel.updateCurrentCompany()
                        editState.value = false
                        snackBarHostState.showSuccess(message = "Владелец успешно изменен")
                    }
                }
            },
            selectUser = selectUser,
            listForNewCreator = listForNewCreator,
        )
    }

    if (isLoading.value) {
        Spinner(
            text = spinnerText.value,
        )
    }
}

@Composable
fun AlertDialogEditCreator(
    icon: Icon? = null,
    iconConfig: IconConfig = IconConfig.Default,
    title: String,
    titleConfig: TextConfig = TextConfig.H1,
    text: String,
    textConfig: TextConfig = TextConfig.Small,
    onDismissText: String,
    onDismissTextConfig: TextConfig = TextConfig.SmallCenteredMedium,
    onConfirmationText: String,
    onConfirmationTextConfig: TextConfig = TextConfig.SmallCenteredMedium,
    containerColors: CardColors = CardDefaults.cardColors(),
    onDismiss: () -> Unit,
    onConfirmation: () -> Unit,
    selectUser: MutableState<User?>,
    listForNewCreator: List<User>,
) {
    val titleColor = remember { titleConfig.color ?: Color.Unspecified }
    val textColor = remember { textConfig.color ?: Color.Unspecified }

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(shape = RoundedCornerShape(10.dp), colors = containerColors) {
            Column(
                modifier =
                    Modifier.padding(start = 20.dp, end = 20.dp, top = 25.dp, bottom = 25.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                if (icon !== null && iconConfig.size > 0) {
                    Icon(
                        icon = icon,
                        iconConfig = iconConfig,
                        defaultColor = containerColors.contentColor,
                    )
                    Spacer(modifier = Modifier.padding(top = 20.dp))
                }

                Text(
                    text = title,
                    color = titleColor,
                    fontSize = titleConfig.size,
                    fontWeight = titleConfig.weight,
                    letterSpacing = titleConfig.letterSpacing,
                )

                Spacer(modifier = Modifier.padding(top = 20.dp))
                Text(
                    text = text,
                    color = textColor,
                    fontSize = textConfig.size,
                    fontWeight = textConfig.weight,
                    letterSpacing = textConfig.letterSpacing,
                )

                Spacer(modifier = Modifier.padding(top = 20.dp))
                DropDownTextField(
                    state = selectUser,
                    menuItems = listForNewCreator,
                    view = { it.name + " " + it.surname },
                    label = "Новый владелец",
                )

                Spacer(modifier = Modifier.padding(top = 20.dp))
                Row {
                    SimpleButton(
                        width = 0.5f,
                        text = onDismissText,
                        textConfig = onDismissTextConfig,
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = containerColors.containerColor,
                                contentColor = MaterialTheme.colorScheme.primary,
                            ),
                        contentPadding = PaddingValues(0.dp),
                    ) {
                        onDismiss()
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    SimpleButton(
                        text = onConfirmationText,
                        textConfig = onConfirmationTextConfig,
                        contentPadding = PaddingValues(0.dp),
                    ) {
                        onConfirmation()
                    }
                }
            }
        }
    }
}

@Composable
fun EditCompanyScreen(
    companyData: Company,
    companyViewModel: CompanyViewModel,
    editState: MutableState<Boolean>,
    alertState: MutableState<Boolean>,
    alertText: MutableState<String>,
    alertConfirmText: MutableState<String>,
    snackBarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    val newNameCompany = remember(companyData) { mutableStateOf(companyData.name) }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        SinglenessOutlinedTextField(text = newNameCompany, label = "Название организации")
        Spacer(modifier = Modifier.height(10.dp))
        SimpleButton(text = "Изменить название") {
            scope.launch {
                if (newNameCompany.value.isNotBlank()) {
                    val newCompany = companyData.copy(name = newNameCompany.value)
                    companyViewModel.updateNameCompany(companyData.id, newNameCompany.value)
                    companyViewModel.setCurrentCompany(newCompany)
                    snackBarHostState.showSuccess(message = "Имя организации успешно изменено")
                    editState.value = false
                } else {
                    snackBarHostState.showError(
                        message = "Ошибка выполнения запроса. Проверьте введенные данные"
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Сменить владельца\nорганизации",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        RowIconButton(
            text = "Сменить владельца",
            textConfig = TextConfig(size = 19.sp),
            icon = Icon.ImageVectorIcon(Icons.Outlined.PersonAdd),
        ) {
            if (companyData.members.count() > 1) {
                alertText.value = "Для смены владельца организации, выберить нового из списка ниже."
                alertConfirmText.value = "Сменить"
                alertState.value = true
            } else {
                scope.launch {
                    snackBarHostState.showError(
                        message =
                            "Нельзя менять владельца организации. В организации должно быть хотя бы 2 человека"
                    )
                }
            }
        }
    }
}

// Карточка пользователя
@Composable
fun UserCard(user: User, creator: Boolean, onClick: () -> Unit) {
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        modifier =
            Modifier.fillMaxWidth(0.9f).clickable(onClick = onClick) // Делает карточку кликабельной
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            // Иконка
            Icon(
                imageVector = Icons.Default.Person, // Используем иконку "человечек"
                contentDescription = "User Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier =
                    Modifier.size(40.dp) // Размер иконки
                        .align(Alignment.CenterVertically) // Выравнивание по вертикали
                        .padding(end = 16.dp) // Отступ от текста
            )

            // Текстовая часть
            Column {
                // Имя пользователя
                Text(
                    text = user.name + " " + user.surname,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(2.dp))
                // Роль пользователя
                Row {
                    Text(
                        text = "Должность: ",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = if (creator) "Директор" else "Сотрудник",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
                Row {
                    Text(
                        "Права:",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    // Права пользователя
                    Text(
                        text =
                            " Edit: " +
                                (if (user.onEdit) "Да" else "Нет") +
                                ", Delete: " +
                                (if (user.onDelete) "Да" else "Нет"),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}
