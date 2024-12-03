package com.example.tasktracker.services.firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktracker.data.User
import com.example.tasktracker.repositories.SharedRepository
import com.example.tasktracker.repositories.UserRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.getValue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class UserViewModel
@Inject
constructor(
    private val sharedRepository: SharedRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    val dataUser = sharedRepository.userData

    init {
        viewModelScope.launch { sharedRepository.setUserData(userRepository.get(getUser())) }
    }

    /** Добавить нового пользователя в БД */
    suspend fun add(currentUser: FirebaseUser?) {
        userRepository.add(currentUser)
    }

    /** Получить список существующих пользователей в БД */
    suspend fun getListUsers(): MutableList<User> {
        return userRepository.getListUsers()
    }

    /** Получить данные о текущем пользователе из БД */
    suspend fun setUserData(currentUser: FirebaseUser?) {
        sharedRepository.setUserData(userRepository.get(getUser()))
    }

    /** Обновление данных о пользователе. Личной информации. */
    suspend fun update(userData: User, name: String, surname: String): Boolean {
        return userRepository.update(userData, name, surname)
    }

    suspend fun updateLastTaskViewId(userData: User, lastTaskViewId: String) {
        userRepository.updateLastTaskViewId(userData, lastTaskViewId)
    }
}
