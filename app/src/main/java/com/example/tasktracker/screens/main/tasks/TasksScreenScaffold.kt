package com.example.tasktracker.screens.main.tasks

import android.text.TextUtils.isEmpty
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasktracker.data.User
import com.example.tasktracker.enums.TaskStatus
import com.example.tasktracker.screens.main.noAccess.NoAccessScreen
import com.example.tasktracker.services.showError
import com.example.tasktracker.services.showSuccess
import com.example.tasktracker.viewModels.CompanyViewModel
import com.example.tasktracker.viewModels.TasksViewModel
import com.example.tasktracker.viewModels.UserViewModel
import com.ravenzip.workshop.components.DropDownTextField
import com.ravenzip.workshop.components.SimpleButton
import com.ravenzip.workshop.components.SinglenessOutlinedTextField
import com.ravenzip.workshop.components.SnackBar
import com.ravenzip.workshop.components.Spinner
import com.ravenzip.workshop.components.TopAppBar
import com.ravenzip.workshop.data.TextConfig
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreenScaffold(
    padding: PaddingValues,
    vararg onClick: () -> Unit,
    userViewModel: UserViewModel,
    companyViewModel: CompanyViewModel,
    tasksViewModel: TasksViewModel,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val isLoading = remember { mutableStateOf(false) }
    val spinnerText = remember { mutableStateOf("Создание задачи...") }

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val taskName = remember { mutableStateOf("") }
    val taskStatus = remember { mutableStateOf<TaskStatus?>(null) }
    val taskUser = remember { mutableStateOf<User?>(null) }

    val userData = userViewModel.dataUser.collectAsState().value
    val companyData = companyViewModel.dataCompany.collectAsState().value
    val idNewTask = tasksViewModel.idNewTask.collectAsState(0).value

    val listUsers = remember { mutableListOf<User>() }
    val isLoadingList = remember { mutableStateOf(true) }

    LaunchedEffect(isLoadingList.value) {
        if (isLoadingList.value) {
            listUsers.addAll(userViewModel.getListUsers())
            isLoadingList.value = false
        }
    }

    val listUsersFiltered =
        listUsers.filter { user -> !isEmpty(user.name) && user.companyId == companyData.id }

    Log.d("TasksScreenScaffold", "LIST USERS - $listUsers")
    Log.d("TasksScreenScaffold", "LIST USERS FILTERED - $listUsersFiltered")

    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = { TopAppBar(title = "Задачи", backArrow = null, items = listOf()) },
        floatingActionButtonPosition = FabPosition.EndOverlay,
        floatingActionButton = {
            if (userData.companyId != "") {
                FloatingActionButton(onClick = { showBottomSheet = true }) {
                    Icon(Icons.Filled.Add, "")
                }
            }
        }
    ) { innerPadding ->
        if (userData.companyId != "") {
            TasksScreen(
                padding = innerPadding,
                onClick = onClick,
            )

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState,
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Создание задачи",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        SinglenessOutlinedTextField(text = taskName, label = "Наименование задачи")

                        Spacer(modifier = Modifier.height(5.dp))

                        DropDownTextField(
                            state = taskStatus,
                            menuItems = TaskStatus.entries,
                            view = { it.value },
                            label = "Статус задачи"
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        DropDownTextField(
                            state = taskUser,
                            menuItems = listUsersFiltered,
                            view = { it.name + " " + it.surname },
                            label = "Исполнитель"
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        SimpleButton(
                            text = "Создать",
                            textConfig = TextConfig(size = 19.sp, align = TextAlign.Center)
                        ) {
                            scope.launch {
                                if (
                                    taskName.value.isNotEmpty() &&
                                        taskUser.value != null &&
                                        taskStatus.value != null &&
                                        taskUser.value != null
                                ) {
                                    /** Отключение видимости контейнера добавления задачи */
                                    showBottomSheet = false
                                    /** Включение спинера */
                                    isLoading.value = true
                                    /** Запрос на создание задачи */
                                    tasksViewModel.add(
                                        nameTask = taskName.value,
                                        statusTask = taskStatus.value!!,
                                        userData.name + " " + userData.surname,
                                        userData.id,
                                        taskUser.value!!.name + " " + taskUser.value!!.surname,
                                        taskUser.value!!.id,
                                        userData.companyId
                                    )

                                    /** Запрос на создание уведомления */
                                    tasksViewModel.addNotification(
                                        date = getCurrentDateTime(),
                                        event = "Была создана новая задача №${idNewTask} ",
                                        userId = taskUser.value!!.id
                                    )
                                    /** Обновление данных текущего пользователя */
                                    userViewModel.setUserData()
                                    /** Обновление индефикатора задач для уведомления */
                                    tasksViewModel.updateIdNewTask()
                                    /** Обнуление вводимых данных */
                                    taskName.value = ""
                                    taskStatus.value = null
                                    taskUser.value = null
                                    /** Отключение спинера */
                                    isLoading.value = false
                                    snackBarHostState.showSuccess(
                                        message = "Задача успешно создана"
                                    )
                                } else {
                                    snackBarHostState.showError(
                                        message =
                                            "Ошибка выполнения запроса. Проверьте введенные данные"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            NoAccessScreen(padding = innerPadding)
        }
    }
    if (isLoading.value) {
        Spinner(
            text = spinnerText.value,
        )
    }
    SnackBar(snackBarHostState)
}

fun getCurrentDateTime(): String {
    val currentDateTime = LocalDateTime.now() // Получаем текущую дату и время
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm") // Задаем формат
    return currentDateTime.format(formatter) // Применяем форматирование
}
