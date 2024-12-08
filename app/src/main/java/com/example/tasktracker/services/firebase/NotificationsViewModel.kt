package com.example.tasktracker.services.firebase

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktracker.data.Notification
import com.example.tasktracker.repositories.NotificationsRepository
import com.example.tasktracker.repositories.SharedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationsViewModel
@Inject
constructor(
    private val notificationsRepository: NotificationsRepository,
    private val sharedRepository: SharedRepository,
) : ViewModel() {
    private val _notificationsList = MutableStateFlow(listOf<Notification>())
    val notificationsList = _notificationsList.asStateFlow()

    /** Создание нового уведомления */
    suspend fun add(date: String, event: String, userId: String) {
        notificationsRepository.add(date, event, userId)
        val randomNumber = (0..1000).random()
        sharedRepository.setUpdateNotifications("$date $randomNumber")
    }

    /** Удаление уведомления */
    suspend fun delete(notificationId: String) {
        notificationsRepository.delete(notificationId)
        sharedRepository.setUpdateNotifications(notificationId)
    }

    /** Получение списка уведомлений пользователя */
    suspend fun getListNotifications() {
        _notificationsList.update {
            notificationsRepository.getListNotifications(sharedRepository.userData.value.id)
        }
        updateNotificationCount()
    }

    private suspend fun updateNotificationCount() {
        sharedRepository.setCountNotifications(notificationsList.map { it.count() }.first())
    }

    init {

        viewModelScope.launch {
            sharedRepository.updateNotifications.collect {
                Log.d("NotificationsViewModel", "СПИСОК ОБНОВЛЕН")
                getListNotifications()
            }
        }

        viewModelScope.launch {
            sharedRepository.userData.collect {
                Log.d("NotificationsViewModel", "СПИСОК ОБНОВЛЕН")
                getListNotifications()
            }
        }
    }
}
