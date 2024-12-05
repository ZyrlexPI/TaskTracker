package com.example.tasktracker.screens.tasks.infoTask

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasktracker.services.firebase.InfoTasksViewModel
import com.example.tasktracker.services.firebase.TasksViewModel
import com.example.tasktracker.services.firebase.UserViewModel
import com.example.tasktracker.services.showError
import com.example.tasktracker.services.showSuccess
import com.ravenzip.workshop.components.SimpleButton
import com.ravenzip.workshop.components.SinglenessOutlinedTextField
import com.ravenzip.workshop.components.SnackBar
import com.ravenzip.workshop.components.Spinner
import com.ravenzip.workshop.components.TopAppBar
import com.ravenzip.workshop.data.TextConfig
import com.ravenzip.workshop.data.appbar.AppBarItem
import com.ravenzip.workshop.data.icon.Icon
import com.ravenzip.workshop.data.icon.IconConfig
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoTaskScreenScaffold(
    padding: PaddingValues,
    userViewModel: UserViewModel,
    tasksViewModel: TasksViewModel,
    infoTasksViewModel: InfoTasksViewModel = hiltViewModel(),
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val isLoading = remember { mutableStateOf(false) }
    val spinnerText = remember { mutableStateOf("Отправка комментария...") }

    val taskInfo = infoTasksViewModel.task.collectAsStateWithLifecycle().value

    val userData = userViewModel.dataUser.collectAsStateWithLifecycle().value

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val textComment = remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = {
            TopAppBar(
                title = "Задача №" + taskInfo.id,
                backArrow = null,
                items = generateTopAppBarItems()
            )
        },
        floatingActionButtonPosition = FabPosition.EndOverlay,
        floatingActionButton = {
            FloatingActionButton(onClick = { showBottomSheet = true }) {
                Icon(Icons.Filled.AddComment, "")
            }
        }
    ) { innerPadding ->
        InfoTaskScreen(
            padding = innerPadding,
            infoTasksViewModel = infoTasksViewModel,
            tasksViewModel = tasksViewModel,
            taskView = taskInfo,
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
                        text = "Создание комментария",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    SinglenessOutlinedTextField(text = textComment, label = "Текст комментария")

                    Spacer(modifier = Modifier.height(20.dp))

                    SimpleButton(
                        text = "Создать",
                        textConfig = TextConfig(size = 19.sp, align = TextAlign.Center)
                    ) {
                        scope.launch {
                            if (textComment.value.isNotEmpty()) {
                                /** Отключение видимости контейнера добавления комментария */
                                showBottomSheet = false
                                /** Включение спинера */
                                isLoading.value = true
                                /** Запрос на создание комментария */
                                tasksViewModel.addComment(
                                    userData.name + " " + userData.surname,
                                    textComment.value,
                                    userData.id,
                                    taskInfo.id
                                )
                                /** Обновление списка комментариев */
                                tasksViewModel.getListComments(taskInfo.id)
                                /** Обнуление вводимых данных */
                                textComment.value = ""
                                /** Отключение спинера */
                                isLoading.value = false
                                snackBarHostState.showSuccess(
                                    message = "Комментарий успешно отправлен"
                                )
                            } else {
                                snackBarHostState.showError(
                                    message =
                                        "Ошибка выполнения запроса. Проверьте введенные данные"
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
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

fun generateTopAppBarItems(): List<AppBarItem> {
    val editButton =
        AppBarItem(
            icon = Icon.ImageVectorIcon(Icons.Filled.Edit),
            iconConfig = IconConfig.Small,
            onClick = {},
        )

    val deleteButton =
        AppBarItem(
            icon = Icon.ImageVectorIcon(Icons.Filled.Delete),
            iconConfig = IconConfig.Small,
            onClick = {},
        )

    return listOf(editButton, deleteButton)
}
