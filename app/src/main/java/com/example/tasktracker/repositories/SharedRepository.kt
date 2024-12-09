package com.example.tasktracker.repositories

import android.util.Log
import com.example.tasktracker.data.Company
import com.example.tasktracker.data.Task
import com.example.tasktracker.data.User
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SharedRepository
@Inject
constructor(private val notificationsRepository: NotificationsRepository) {

    /** Задача, которая отображается на экране */
    private val _currentTask = MutableStateFlow<Task?>(null)
    val currentTask = _currentTask.asStateFlow()

    fun setCurrentTask(task: Task) {
        _currentTask.update { task }
        Log.d("SharedRepository", currentTask.value.toString())
    }

    /** Пользователь, который отображается на экране */
    private val _currentUser = MutableStateFlow(User())
    val currentUser = _currentUser.asStateFlow()

    fun setCurrentUser(user: User) {
        _currentUser.update { user }
    }

    /** Текущий пользователь */
    private val _userData = MutableStateFlow<User>(User())
    val userData = _userData.asStateFlow()

    fun setUserData(user: User) {
        _userData.update { user }
    }

    /** Текущая Организация */
    private val _companyData = MutableStateFlow(Company())
    val companyData = _companyData.asStateFlow()

    fun setCompanyData(company: Company) {
        _companyData.update { company }
    }

    /** Статус обновления Уведомлений */
    private val _updateNotifications = MutableStateFlow("")
    val updateNotifications = _updateNotifications.asStateFlow()

    fun setUpdateNotifications(update: String) {
        _updateNotifications.update {
            Log.d("SharedRepository", "ШАРЕД ОБНОВЛЕН $update")
            update
        }
    }

    /** Количество уведомлений */
    private val _countNotifications = MutableStateFlow(0)
    val countNotifications = _countNotifications.asStateFlow()

    fun setCountNotifications(count: Int) {
        _countNotifications.update { count }
    }

    suspend fun updateCountNotifications(user: User) {
        _countNotifications.update { notificationsRepository.getListNotifications(user.id).count() }
    }
}
