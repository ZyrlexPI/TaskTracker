package com.example.tasktracker.repositories

import com.example.tasktracker.data.Notification
import com.example.tasktracker.sources.NotificationsSource
import com.google.firebase.database.getValue
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class NotificationsRepository
@Inject
constructor(private val notificationsSource: NotificationsSource) {

    /** Создание нового уведомления */
    suspend fun add(date: String, event: String, userId: String) {
        /** Создание индефикатора уведомления */
        val pushKey = notificationsSource.notificationsSource.push().key.toString()
        /** Создание объекта комментария */
        val notificationData = Notification(pushKey, date, event, userId)
        /** Добавление комментария в БД */
        notificationsSource.notificationsSource.child(pushKey).setValue(notificationData).await()
    }

    /** Получение списка уведомлений пользователя */
    suspend fun getListNotifications(userId: String): List<Notification> {
        val response = notificationsSource.notificationsSource.get().await().children
        val listNotifications = mutableListOf<Notification>()
        response.forEach { data ->
            data
                .getValue<Notification>()
                ?.takeIf { notification -> notification.userId == userId }
                .let { it?.let { it1 -> listNotifications.add(it1) } }
        }
        return listNotifications
    }

    /** Удаление уведомления */
    suspend fun delete(notificationId: String) {
        notificationsSource.notificationsSource.child(notificationId).removeValue().await()
    }
}
