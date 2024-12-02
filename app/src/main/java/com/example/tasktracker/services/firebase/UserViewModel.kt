package com.example.tasktracker.services.firebase

import androidx.lifecycle.ViewModel
import com.example.tasktracker.data.User
import com.example.tasktracker.repositories.UserRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.getValue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class UserViewModel
@Inject
constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _dataUser = MutableStateFlow(User())
    val dataUser = _dataUser.asStateFlow()

    /** Добавить нового пользователя в БД */
    suspend fun add(currentUser: FirebaseUser?) {
        userRepository.add(currentUser)
    }

    /** Получить список существующих пользователей в БД */
    suspend fun getListUsers(): MutableList<User> {
        return userRepository.getListUsers()
    }

    /** Получить данные о текущем пользователе из БД */
    suspend fun get(currentUser: FirebaseUser?) {
        _dataUser.update { userRepository.get(currentUser) }
    }

    /** Обновление данных о пользователе. Личной информации. */
    suspend fun update(userData: User, name: String, surname: String): Boolean {
        return userRepository.update(userData, name, surname)
    }

    suspend fun updateLastTaskViewId(userData: User, lastTaskViewId: String) {
        userRepository.updateLastTaskViewId(userData, lastTaskViewId)
    }
}
