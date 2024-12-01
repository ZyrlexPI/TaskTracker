package com.example.tasktracker.screens.main.tasks

import android.text.TextUtils.isEmpty
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
import com.example.tasktracker.services.firebase.TasksViewModel
import com.example.tasktracker.services.firebase.UserViewModel
import com.example.tasktracker.services.showError
import com.example.tasktracker.services.showSuccess
import com.ravenzip.workshop.components.DropDownTextField
import com.ravenzip.workshop.components.SimpleButton
import com.ravenzip.workshop.components.SinglenessOutlinedTextField
import com.ravenzip.workshop.components.SnackBar
import com.ravenzip.workshop.components.Spinner
import com.ravenzip.workshop.components.TopAppBar
import com.ravenzip.workshop.data.TextConfig
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreenScaffold(
    padding: PaddingValues,
    vararg onClick: () -> Unit,
    userViewModel: UserViewModel,
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

    val listUsers = remember { mutableListOf<User>() }
    val isLoadingList = remember { mutableStateOf(true) }

    LaunchedEffect(isLoadingList.value) {
        if (isLoadingList.value) {
            listUsers.addAll(userViewModel.getListUsers())
            isLoadingList.value = false
        }
    }

    val listUsersFiltered = listUsers.filter { user -> !isEmpty(user.name) }

    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = { TopAppBar(title = "Задачи", backArrow = null, items = listOf()) },
        floatingActionButtonPosition = FabPosition.EndOverlay,
        floatingActionButton = {
            FloatingActionButton(onClick = { showBottomSheet = true }) {
                Icon(Icons.Filled.Add, "")
            }
        }
    ) { innerPadding ->
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
                        menuItems = TaskStatus.values().toList(),
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
                                    taskName.value,
                                    taskStatus.value!!,
                                    taskUser.value!!.name + " " + taskUser.value!!.surname,
                                    userData.id,
                                    userData.name + " " + userData.surname,
                                    taskUser.value!!.id,
                                    userData.companyId
                                )
                                /** Обновление списка задач */
                                tasksViewModel.updateListTask()
                                /** Обнуление вводимых данных */
                                taskName.value = ""
                                taskStatus.value = null
                                taskUser.value = null
                                /** Отключение спинера */
                                isLoading.value = false
                                snackBarHostState.showSuccess(message = "Задача успешно создана")
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
    }
    if (isLoading.value) {
        Spinner(
            text = spinnerText.value,
        )
    }
    SnackBar(snackBarHostState)
}
