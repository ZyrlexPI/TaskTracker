package com.example.tasktracker.screens.tasks.infoTask

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasktracker.data.Comment
import com.example.tasktracker.data.Task
import com.example.tasktracker.enums.TaskStatus
import com.example.tasktracker.services.firebase.InfoTasksViewModel
import com.example.tasktracker.services.firebase.TasksViewModel
import com.example.tasktracker.services.showError
import com.example.tasktracker.services.showSuccess
import com.ravenzip.workshop.components.DropDownTextField
import com.ravenzip.workshop.components.SimpleButton
import com.ravenzip.workshop.components.SinglenessOutlinedTextField
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
                snackBarHostState
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
                        fontSize = 24.sp,
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
}

@Composable
fun EditTaskScreen(
    task: Task,
    infoTasksViewModel: InfoTasksViewModel,
    tasksViewModel: TasksViewModel,
    editState: MutableState<Boolean>,
    snackBarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    val newNameTask = remember(task) { mutableStateOf(task.name) }

    val newStatusTask = remember(task) { mutableStateOf(task.status) }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        SinglenessOutlinedTextField(text = newNameTask, label = "Имя задачи")
        Spacer(modifier = Modifier.height(8.dp))
        DropDownTextField(
            state = newStatusTask,
            menuItems = TaskStatus.values().toList(),
            view = { it.value },
            label = "Статус задачи"
        )
        Spacer(modifier = Modifier.height(10.dp))
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
    }
}

// Карточка для отображения задачи
@Composable
fun TaskCard(task: Task) {
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
