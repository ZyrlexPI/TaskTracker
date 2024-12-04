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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasktracker.enums.TaskStatus
import com.example.tasktracker.enums.TaskViewOption
import com.example.tasktracker.services.firebase.TasksViewModel
import com.example.tasktracker.services.firebase.UserViewModel
import com.example.tasktracker.services.firebase.getUser
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListTaskScreen(
    padding: PaddingValues,
    status: TaskStatus,
    viewingOption: TaskViewOption,
    navigateToInfoTask: () -> Unit,
    userViewModel: UserViewModel,
    tasksViewModel: TasksViewModel,
) {
    val scope = rememberCoroutineScope()
    val listTask = tasksViewModel.listTasks.collectAsStateWithLifecycle().value
    val userData = userViewModel.dataUser.collectAsState().value

    val listTaskFiltered =
        when (viewingOption) {
            TaskViewOption.EXECUTOR ->
                listTask.filter { task -> task.status == status && task.executor_id == userData.id }
            TaskViewOption.AUTHOR ->
                listTask
                    .sortedBy { task -> task.status }
                    .filter { task -> task.author_id == userData.id }
        }
    val refreshState = rememberPullToRefreshState()
    val isRefreshing = remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    PullToRefreshBox(
        isRefreshing = isRefreshing.value,
        onRefresh = {
            scope.launch {
                isRefreshing.value = true
                tasksViewModel.updateListTask()
                delay(1.seconds)
                isRefreshing.value = false
            }
        },
        modifier = Modifier.padding(padding),
        state = refreshState,
    ) {
        if (listTaskFiltered.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Список задач пуст")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(listTaskFiltered) { task ->
                    Card(
                        modifier =
                            Modifier.clip(shape = RoundedCornerShape(8.dp)).clickable {
                                scope.launch {
                                    userViewModel.updateLastTaskViewId(userData = userData, task.id)
                                    userViewModel.setUserData(getUser())
                                    tasksViewModel.setCurrentTask(task)
                                    navigateToInfoTask()
                                }
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
                                if (viewingOption == TaskViewOption.AUTHOR) {
                                    Text(text = "Статус: " + task.status.value)
                                }
                                Text(text = "Автор: " + task.author)
                                Text(text = "Исполнитель: " + task.executor)
                            }
                        }
                    }
                }
            }
        }
    }
}
