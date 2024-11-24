package com.example.tasktracker.screens.tasks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tasktracker.data.Task
import com.example.tasktracker.enums.TaskStatus

@Composable
fun ListTaskScreen(padding: PaddingValues, status: TaskStatus) {
    val listTask =
        listOf(
            Task(
                id = "123",
                name = "Задача 1",
                status = TaskStatus.NEW_TASK,
                author = "Алексей",
                author_id = "321",
                executor = "Николай",
                executor_id = "213",
                companyId = "4321"
            ),
            Task(
                id = "124",
                name = "Задача 2",
                status = TaskStatus.NEW_TASK,
                author = "Александр",
                author_id = "322",
                executor = "Евгений",
                executor_id = "214",
                companyId = "4321"
            ),
            Task(
                id = "125",
                name = "Задача 3",
                status = TaskStatus.NEW_TASK,
                author = "Роман",
                author_id = "323",
                executor = "Николай",
                executor_id = "213",
                companyId = "4321"
            )
        )
    val listTaskFiltered =
        listTask.filter { task ->
            task.status == status && task.companyId == "4321" && task.executor_id == "213"
        }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(listTaskFiltered) { task ->
            Card(
                modifier =
                    Modifier.clip(shape = RoundedCornerShape(8.dp)).clickable {
                        // Todo
                    },
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = Color.Black
                    ),
            ) {
                Row(modifier = Modifier.fillMaxSize(0.92f).padding(10.dp)) {
                    Icon(
                        imageVector = Icons.Filled.TaskAlt,
                        contentDescription = null,
                        modifier =
                            Modifier.size(37.dp)
                                .align(Alignment.CenterVertically)
                                .padding(end = 10.dp)
                    )
                    Column {
                        Text(text = "#" + task.id + " - " + task.name)
                        //      Text(text = "Статус: " + task.status.value)
                        Text(text = "Автор: " + task.author)
                        Text(text = "Исполнитель: " + task.executor)
                    }
                }
            }

            //            RowIconButton(
            //                text = task.name,
            //                textConfig = TextConfig(size = 19.sp),
            //                icon = Icon.ImageVectorIcon(Icons.Filled.TaskAlt),
            //                iconConfig = IconConfig.Default
            //            )
        }
    }
}
