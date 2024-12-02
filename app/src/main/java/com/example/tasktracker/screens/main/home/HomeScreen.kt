package com.example.tasktracker.screens.main.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasktracker.data.Task
import com.example.tasktracker.services.firebase.TaskByType
import com.example.tasktracker.services.firebase.TasksViewModel
import com.example.tasktracker.services.firebase.UserViewModel

@Composable
fun HomeScreen(
    padding: PaddingValues,
    navigationToLastViewTask: () -> Unit,
    userViewModel: UserViewModel,
    tasksViewModel: TasksViewModel
) {
    val userData = userViewModel.dataUser.collectAsState().value
    val taskData = tasksViewModel.dataCurrentTask.collectAsState().value

    val isLoadingList = remember(userData.lastTaskViewId) { mutableStateOf(true) }

    LaunchedEffect(isLoadingList.value) {
        if (isLoadingList.value) {
            isLoadingList.value = false
            if (userData.lastTaskViewId != "") {
                tasksViewModel.getCurrentTask(userData.lastTaskViewId)
            }
        }
    }

    val taskCount = tasksViewModel.filteredTaskCount.collectAsStateWithLifecycle(TaskByType()).value

    val totalTasks =
        remember(taskCount) { taskCount.new + taskCount.inProgress + taskCount.complete }

    // Основной макет
    Column(
        modifier = Modifier.fillMaxSize().padding(padding),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (userData.lastTaskViewId != "") {
            // Карточка последней задачи
            TaskCard(
                task = taskData,
                tasksViewModel = tasksViewModel,
                navigationToLastViewTask = navigationToLastViewTask
            )
        } else {
            // Карточка последней задачи если ещё нет
            TaskCardNotFound()
        }

        // Карточка общего состояния задач
        StatsCard(
            totalTasks = totalTasks,
            newTasksCount = taskCount.new,
            inProgressTasksCount = taskCount.inProgress,
            completedTasksCount = taskCount.complete
        )
    }
}

// Карточка последней открытой задачи
@Composable
fun TaskCard(task: Task, tasksViewModel: TasksViewModel, navigationToLastViewTask: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier =
            Modifier.fillMaxWidth().padding(top = 5.dp, start = 16.dp, end = 16.dp).clickable {
                tasksViewModel.setCurrentTask(task)
                navigationToLastViewTask()
            },
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Последняя открытая задача №${task.id}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(text = "Имя задачи: ${task.name}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Статус: ${task.status.value}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Автор: ${task.author}", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Исполнитель: ${task.executor}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun TaskCardNotFound() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier.fillMaxWidth().padding(top = 5.dp, start = 16.dp, end = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Последняя открытая задача",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(text = "Ничего не найдено", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// Карточка общего состояния задач
@Composable
fun StatsCard(
    totalTasks: Int,
    newTasksCount: Int,
    inProgressTasksCount: Int,
    completedTasksCount: Int
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier.fillMaxWidth().padding(top = 5.dp, start = 16.dp, end = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Всего задач: $totalTasks",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            // Прогресс-бары для каждой категории задач
            ProgressBar(
                label = "Новые",
                value = if (totalTasks == 0) 0f else newTasksCount / totalTasks.toFloat()
            )
            ProgressBar(
                label = "В процессе",
                value = if (totalTasks == 0) 0f else inProgressTasksCount / totalTasks.toFloat()
            )
            ProgressBar(
                label = "Завершенные",
                value = if (totalTasks == 0) 0f else completedTasksCount / totalTasks.toFloat()
            )
        }
    }
}

// Прогресс-бар с текстом
@Composable
fun ProgressBar(label: String, value: Float) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label + " - " + value * 100 + "%",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { value },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = Color.LightGray,
        )
    }
}
