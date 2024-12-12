package com.example.tasktracker.screens.tasks.infoTask

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import com.example.tasktracker.data.Comment
import com.example.tasktracker.data.Task
import com.example.tasktracker.data.User
import com.example.tasktracker.enums.TaskStatus
import com.example.tasktracker.services.showError
import com.example.tasktracker.services.showSuccess
import com.example.tasktracker.viewModels.InfoTasksViewModel
import com.example.tasktracker.viewModels.TasksViewModel
import com.ravenzip.workshop.components.AlertDialog
import com.ravenzip.workshop.components.DropDownTextField
import com.ravenzip.workshop.components.Icon
import com.ravenzip.workshop.components.RowIconButton
import com.ravenzip.workshop.components.SimpleButton
import com.ravenzip.workshop.components.SinglenessOutlinedTextField
import com.ravenzip.workshop.data.TextConfig
import com.ravenzip.workshop.data.icon.Icon
import com.ravenzip.workshop.data.icon.IconConfig
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoTaskScreen(
    padding: PaddingValues,
    infoTasksViewModel: InfoTasksViewModel,
    tasksViewModel: TasksViewModel,
    editState: MutableState<Boolean>,
    snackBarHostState: SnackbarHostState,
) {
    val scope = rememberCoroutineScope()
    val taskView = infoTasksViewModel.task.collectAsStateWithLifecycle().value
    val listComments = tasksViewModel.listComments.collectAsStateWithLifecycle(listOf()).value
    Log.d("InfoTaskScreen_LIST", "$listComments")
    val refreshState = rememberPullToRefreshState()
    val isRefreshing = remember { mutableStateOf(false) }

    val listMembers = infoTasksViewModel.listMembers.collectAsStateWithLifecycle(listOf()).value

    val listFilterMembers =
        listMembers.filter { member ->
            member.id != taskView.author_id && // Исключаем автора задачи
                member.id != taskView.executor_id && // Исключаем исполнителя задачи
                taskView.observers.none { observer ->
                    observer.id == member.id
                } // Исключаем наблюдателей
        }

    val selectUser = remember { mutableStateOf<User?>(null) }

    val alertAddState = remember { mutableStateOf(false) }
    val alertDeleteState = remember { mutableStateOf(false) }
    val currentUserDelete = remember { mutableStateOf<User?>(null) }

    // Весь экран
    PullToRefreshBox(
        isRefreshing = isRefreshing.value,
        onRefresh = {
            scope.launch {
                isRefreshing.value = true
                tasksViewModel.getListComments(taskView.id)
                delay(1.seconds)
                isRefreshing.value = false
            }
        },
        modifier = Modifier.padding(padding),
        state = refreshState,
    ) {
        if (editState.value) {
            EditTaskScreen(
                taskView,
                infoTasksViewModel,
                tasksViewModel,
                editState,
                alertAddState,
                alertDeleteState,
                snackBarHostState,
                listFilterMembers,
                currentUserDelete,
            )
        } else {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item { TaskCard(taskView) }
                item {
                    Text(
                        text = "Комментарии",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
                if (listComments.isEmpty()) {
                    item { Text(text = "Нет комментариев") }
                } else {
                    items(listComments) { comment -> CommentItem(comment) }
                }
            }
        }
    }

    if (alertAddState.value) {
        AlertDialogAddObserver(
            title = "Добавление наблюдателя",
            text = "Выберите пользователя для добавления в наблюдатели",
            onDismissText = "Отмена",
            onConfirmationText = "Добавить",
            onDismiss = { alertAddState.value = false },
            onConfirmation = {
                scope.launch {
                    alertAddState.value = false
                    if (selectUser.value == null) {
                        snackBarHostState.showError(
                            message = "Ошибка выбора пользователя. Повторите попытку"
                        )
                    } else {
                        infoTasksViewModel.addObserver(taskView.id, selectUser.value!!)
                        infoTasksViewModel.getCurrentTask(taskView.id)
                        tasksViewModel.addNotification(
                            getCurrentDateTime(),
                            "Вы были добавлены в наблюдатели в задаче №${taskView.id}",
                            selectUser.value!!.id
                        )
                        editState.value = false
                        snackBarHostState.showSuccess(
                            message = "Пользователь успешно добавлен в наблюдатели"
                        )
                    }
                }
            },
            selectUser = selectUser,
            listForNewCreator = listFilterMembers,
        )
    }

    if (alertDeleteState.value) {
        AlertDialog(
            title = "Удаление наблюдателя",
            text = "Вы действительно хотите удалить пользователя из наблюдателей?",
            onDismissText = "Отмена",
            onConfirmationText = "Удалить",
            onDismiss = { alertDeleteState.value = false },
            onConfirmation = {
                scope.launch {
                    alertDeleteState.value = false
                    if (currentUserDelete.value == null) {
                        snackBarHostState.showError(
                            message = "Ошибка удаления пользователя. Повторите попытку"
                        )
                    } else {
                        infoTasksViewModel.deleteObserver(taskView.id, currentUserDelete.value!!)
                        infoTasksViewModel.getCurrentTask(taskView.id)
                        tasksViewModel.addNotification(
                            getCurrentDateTime(),
                            "Вы были удалены из наблюдателей в задаче №${taskView.id}",
                            currentUserDelete.value!!.id
                        )
                        editState.value = false
                        snackBarHostState.showSuccess(
                            message = "Пользователь успешно удален из наблюдателей"
                        )
                    }
                }
            },
        )
    }
}

@Composable
fun EditTaskScreen(
    task: Task,
    infoTasksViewModel: InfoTasksViewModel,
    tasksViewModel: TasksViewModel,
    editState: MutableState<Boolean>,
    alertAddState: MutableState<Boolean>,
    alertDeleteState: MutableState<Boolean>,
    snackBarHostState: SnackbarHostState,
    listFilterMembers: List<User>,
    currentUserDelete: MutableState<User?>,
) {
    val scope = rememberCoroutineScope()

    val newNameTask = remember(task) { mutableStateOf(task.name) }
    val newStatusTask = remember(task) { mutableStateOf(task.status) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            SinglenessOutlinedTextField(text = newNameTask, label = "Имя задачи")
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            DropDownTextField(
                state = newStatusTask,
                menuItems = TaskStatus.values().toList(),
                view = { it.value },
                label = "Статус задачи"
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            SimpleButton(text = "Изменить задачу") {
                scope.launch {
                    if (newNameTask.value.isNotBlank() && newStatusTask.value != null) {
                        val newTask =
                            Task(
                                id = task.id,
                                name = newNameTask.value,
                                status = newStatusTask.value,
                                author = task.author,
                                author_id = task.author_id,
                                executor = task.executor,
                                executor_id = task.executor_id,
                                companyId = task.companyId,
                                observers = task.observers,
                            )
                        tasksViewModel.update(newTask)
                        infoTasksViewModel.updateTask(newTask)
                        tasksViewModel.setCurrentTask(newTask)
                        tasksViewModel.updateListTask()
                        tasksViewModel.addNotification(
                            getCurrentDateTime(),
                            "Были изменены данные в задачи №${task.id}",
                            task.author_id
                        )
                        if (task.executor_id != task.author_id) {
                            tasksViewModel.addNotification(
                                getCurrentDateTime(),
                                "Были изменены данные в задачи №${task.id}",
                                task.executor_id
                            )
                        }
                        snackBarHostState.showSuccess(message = "Задача успешно изменена")
                        editState.value = false
                    } else {
                        snackBarHostState.showError(
                            message = "Ошибка выполнения запроса. Проверьте введенные данные"
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            Text(
                text = "Наблюдатели",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            RowIconButton(
                text = "Добавить наблюдателя",
                textConfig = TextConfig(size = 19.sp),
                icon = Icon.ImageVectorIcon(Icons.Outlined.PersonAdd),
            ) {
                if (listFilterMembers.isNotEmpty()) {
                    alertAddState.value = true
                } else {
                    scope.launch {
                        snackBarHostState.showError(
                            message =
                                "Нельзя добавить наблюдателя. Нет пользователей подходящих под условие."
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            Text(
                text = "Список наблюдателей:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (task.observers.isEmpty()) {
            item { Text(text = "Нет наблюдателей") }
        } else {
            items(task.observers) { observer ->
                ObserverCard(
                    observer = observer,
                    onRemove = {
                        alertDeleteState.value = true
                        currentUserDelete.value = observer
                    }
                )
            }
        }
    }
}

// Карточка наблюдателя
@Composable
fun ObserverCard(observer: User, onRemove: () -> Unit) {
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        Row(
            modifier =
                Modifier.padding(start = 20.dp, end = 10.dp, top = 8.dp, bottom = 8.dp)
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Имя наблюдателя
            Row(
                modifier = Modifier.align(Alignment.CenterVertically),
            ) {
                Text(
                    text = "Сотрудник: ",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = observer.name + " " + observer.surname,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Кнопка удаления
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Outlined.Close, // Иконка "крест"
                    contentDescription = "Удалить наблюдателя",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun AlertDialogAddObserver(
    icon: Icon? = null,
    iconConfig: IconConfig = IconConfig.Default,
    title: String,
    titleConfig: TextConfig = TextConfig.H2,
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
                    label = "Новый наблюдатель",
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

// Карточка для отображения задачи
@Composable
fun TaskCard(task: Task) {

    val textObservers =
        if (task.observers.isEmpty()) "Нет"
        else task.observers.map { user -> user.name + " " + user.surname }.joinToString(", ")

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Заголовок
            Text(
                text = "Информация о задаче",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary
            )

            // Данные о задаче
            TaskDetailItem(label = "Наименование:", value = task.name)
            TaskDetailItem(label = "Статус:", value = task.status.value)
            TaskDetailItem(label = "Автор:", value = task.author)
            TaskDetailItem(label = "Исполнитель:", value = task.executor)
            TaskDetailItem(label = "Наблюдатели:", value = textObservers)
        }
    }
}

// Вспомогательный компонент для строки данных
@Composable
fun TaskDetailItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

// Элемент комментария
@Composable
fun CommentItem(comment: Comment) {
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text(
                text = comment.userName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = comment.text, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

// Функция для получения текущей даты и времени
fun getCurrentDateTime(): String {
    val currentDateTime = LocalDateTime.now() // Получаем текущую дату и время
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm") // Задаем формат
    return currentDateTime.format(formatter) // Применяем форматирование
}
