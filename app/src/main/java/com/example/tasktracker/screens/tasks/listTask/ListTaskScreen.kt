package com.example.tasktracker.screens.tasks.listTask

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasktracker.enums.TaskStatus
import com.example.tasktracker.services.firebase.TasksViewModel

@Composable
fun ListTaskScreen(
    padding: PaddingValues,
    status: TaskStatus,
    navigateToInfoTask: () -> Unit,
    tasksViewModel: TasksViewModel,
) {
    val scope = rememberCoroutineScope()
    val listTask = tasksViewModel.listTasks.collectAsStateWithLifecycle().value

    val listTaskFiltered =
        listTask.filter { task ->
            task.status == status
            //                    && task.companyId == "4321" && task.executor_id == "213"
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
                        tasksViewModel.setCurrentTask(task)
                        navigateToInfoTask()
                    },
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
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
