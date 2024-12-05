package com.example.tasktracker.screens.tasks.infoTask

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
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.tasktracker.services.firebase.InfoTasksViewModel
import com.example.tasktracker.services.firebase.TasksViewModel
import com.ravenzip.workshop.components.SimpleButton
import com.ravenzip.workshop.components.SinglenessOutlinedTextField
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoTaskScreen(
    padding: PaddingValues,
    infoTasksViewModel: InfoTasksViewModel,
    tasksViewModel: TasksViewModel,
    taskView: Task,
) {
    val scope = rememberCoroutineScope()
    val listComments = tasksViewModel.listComments.collectAsStateWithLifecycle().value

    val refreshState = rememberPullToRefreshState()
    val isRefreshing = remember { mutableStateOf(false) }

    val authorName = infoTasksViewModel.userNameAuthor.collectAsStateWithLifecycle().value
    val executorName = infoTasksViewModel.userNameExecutor.collectAsStateWithLifecycle().value

    val userNameComment = remember { mutableStateOf("") }

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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { TaskCard(authorName, executorName, taskView) }
            item {
                Text(
                    text = "Комментарии",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            items(listComments) { comment ->
                LaunchedEffect(Unit) {
                    scope.launch {
                        userNameComment.value = tasksViewModel.getUserNameById(comment.userId)
                    }
                }
                CommentItem(userNameComment.value, comment)
            }
        }
    }
}

// Карточка для отображения задачи
@Composable
fun TaskCard(authorName: String, executorName: String, task: Task) {
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
            TaskDetailItem(label = "Автор:", value = authorName)
            TaskDetailItem(label = "Исполнитель:", value = executorName)
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

// Добавление комментария
fun addComment(comments: MutableList<Comment>, userName: String, text: String) {
    comments.add(Comment("", userName, text, "", ""))
}

// Элемент комментария
@Composable
fun CommentItem(userName: String, comment: Comment) {
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text(
                text = userName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = comment.text, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

// Кнопка добавления нового комментария
@Composable
fun AddCommentButton(onAddComment: (userName: String, commentText: String) -> Unit) {
    val userName = remember { mutableStateOf("") }
    val commentText = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SinglenessOutlinedTextField(text = userName, label = "Имя пользователя")
        Spacer(modifier = Modifier.height(8.dp))
        SinglenessOutlinedTextField(text = commentText, label = "Текст комментария")
        Spacer(modifier = Modifier.height(10.dp))
        SimpleButton(text = "Добавить комментарий") {
            if (userName.value.isNotBlank() && commentText.value.isNotBlank()) {
                onAddComment(userName.value, commentText.value)
                userName.value = ""
                commentText.value = ""
            }
        }
    }
}
