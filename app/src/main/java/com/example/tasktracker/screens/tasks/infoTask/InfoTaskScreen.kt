package com.example.tasktracker.screens.tasks.infoTask

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasktracker.data.Task

@Composable
fun InfoTaskScreen(
    padding: PaddingValues,
    taskView: Task,
) {
    // Весь экран
    Box(modifier = Modifier.fillMaxSize().padding(padding)) { TaskCard(taskView) }
}

// Карточка для отображения задачи
@Composable
fun TaskCard(task: Task) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp, start = 16.dp, end = 16.dp)
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

    // UI для отображения информации о задаче
//    Column(modifier = Modifier.fillMaxSize().padding(padding)) {
//        TaskDetailItem(label = "Имя задачи:", value = task.name)
//        Spacer(modifier = Modifier.height(8.dp))
//        TaskDetailItem(label = "Статус задачи:", value = task.status.value)
//        Spacer(modifier = Modifier.height(8.dp))
//        TaskDetailItem(label = "Автор:", value = task.author)
//        Spacer(modifier = Modifier.height(8.dp))
//        TaskDetailItem(label = "Исполнитель:", value = task.executor)
//    }
// }

// Вспомогательный компонент для строки данных
// @Composable
// fun TaskDetailItem(label: String, value: String) {
//    Column(modifier = Modifier.fillMaxWidth()) {
//        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
//        Text(text = value, fontSize = 16.sp)
//    }
// }
