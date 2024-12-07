package com.example.tasktracker.screens.main.notifications

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasktracker.data.Notification
import com.example.tasktracker.services.firebase.NotificationsViewModel
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(padding: PaddingValues, notificationsViewModel: NotificationsViewModel) {
    val scope = rememberCoroutineScope()
    val notifications = notificationsViewModel.notificationsList.collectAsStateWithLifecycle().value

    //    val notifications = remember {
    //        mutableListOf(
    //            Notification("", "2024-11-29 14:30", "Событие 1: Задача завершена", ""),
    //            Notification("", "2024-11-28 10:15", "Событие 2: Новая задача добавлена", ""),
    //            Notification("", "2024-11-27 09:00", "Событие 3: Напоминание о задаче", ""),
    //            Notification("", "2024-11-26 12:45", "Событие 4: Изменен статус задачи", ""),
    //            Notification("", "2024-11-25 08:30", "Событие 5: Новый комментарий", "")
    //        )
    //    }
    val refreshState = rememberPullToRefreshState()
    val isRefreshing = remember { mutableStateOf(false) }

    PullToRefreshBox(
        isRefreshing = isRefreshing.value,
        onRefresh = {
            scope.launch {
                isRefreshing.value = true
                notificationsViewModel.getListNotifications()
                delay(1.seconds)
                isRefreshing.value = false
            }
        },
        modifier = Modifier.padding(padding),
        state = refreshState,
    ) {
        if (notifications.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "У вас нет уведомлений", fontWeight = FontWeight.Bold)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications, key = { it.id }) { notification ->
                    var isRemoved by remember { mutableStateOf(false) }

                    if (!isRemoved) {
                        NotificationCard(
                            onRemove = {
                                scope.launch {
                                    notificationsViewModel.delete(notification.id)
                                    isRemoved = true
                                }
                            },
                            notification
                        )
                    }
                }
            }
        }
    }
}

// Карточка уведомления
@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun NotificationCard(onRemove: () -> Unit, notification: Notification) {

    val scope = rememberCoroutineScope()
    var offsetX by remember { mutableStateOf(0f) }

    // Анимация для горизонтального смещения
    val animatedOffsetX by animateDpAsState(targetValue = offsetX.dp)

    Card(
        shape = MaterialTheme.shapes.medium,
        colors =
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier =
            Modifier.fillMaxWidth().offset(x = animatedOffsetX).pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (offsetX > 300 || offsetX < -300) {
                            onRemove()
                        } else {
                            offsetX = 0f // Возвращаем карточку обратно
                        }
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        offsetX += dragAmount // Обновляем смещение
                    }
                )
            }
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            // Отображение даты и времени
            Text(
                text = notification.dateTime,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Отображение события
            Text(
                text = notification.event,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
