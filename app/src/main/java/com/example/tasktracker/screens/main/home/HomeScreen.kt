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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasktracker.data.Task
import com.example.tasktracker.viewModels.TaskByType
import com.example.tasktracker.viewModels.TasksViewModel
import com.example.tasktracker.viewModels.UserViewModel
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun HomeScreen(
    padding: PaddingValues,
    navigationToLastViewTask: () -> Unit,
    userViewModel: UserViewModel,
    tasksViewModel: TasksViewModel
) {
    val userData = userViewModel.dataUser.collectAsStateWithLifecycle().value
    val taskData = tasksViewModel.dataCurrentTask.collectAsStateWithLifecycle().value

    val taskCount = tasksViewModel.filteredTaskCount.collectAsStateWithLifecycle(TaskByType()).value

    val taskUserCount =
        tasksViewModel.filteredTaskUserCount.collectAsStateWithLifecycle(TaskByType()).value

    val totalTasks =
        remember(taskCount) { taskCount.new + taskCount.inProgress + taskCount.complete }

    val totalUserTasks =
        remember(taskUserCount) {
            taskUserCount.new + taskUserCount.inProgress + taskUserCount.complete
        }

    // Основной макет
    Column(
        modifier = Modifier.fillMaxSize().padding(padding),
        verticalArrangement = Arrangement.spacedBy(12.dp),
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
            title = "Мои задачи",
            totalTasks = totalUserTasks,
            newTasksCount = taskUserCount.new,
            inProgressTasksCount = taskUserCount.inProgress,
            completedTasksCount = taskUserCount.complete
        )

        // Карточка общего состояния задач
        StatsCard(
            title = "Всего задач",
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
    title: String,
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
                text = "$title: $totalTasks",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            // Прогресс-бары для каждой категории задач
            ProgressBar(
                label = "Новые",
                count = newTasksCount,
                value =
                    if (totalTasks == 0) 0f
                    else
                        BigDecimal(newTasksCount / totalTasks.toFloat().toDouble())
                            .setScale(2, RoundingMode.HALF_UP)
                            .toFloat()
            )
            ProgressBar(
                label = "В процессе",
                count = inProgressTasksCount,
                value =
                    if (totalTasks == 0) 0f
                    else
                        BigDecimal(inProgressTasksCount / totalTasks.toFloat().toDouble())
                            .setScale(2, RoundingMode.HALF_UP)
                            .toFloat()
            )
            ProgressBar(
                label = "Завершенные",
                count = completedTasksCount,
                value =
                    if (totalTasks == 0) 0f
                    else
                        BigDecimal(completedTasksCount / totalTasks.toFloat().toDouble())
                            .setScale(2, RoundingMode.HALF_UP)
                            .toFloat()
            )
        }
    }
}

// Прогресс-бар с текстом
@Composable
fun ProgressBar(label: String, count: Int, value: Float) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label + ": " + count + " - " + value * 100 + "%",
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
