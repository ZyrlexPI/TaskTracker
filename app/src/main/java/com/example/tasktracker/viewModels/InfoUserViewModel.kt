package com.example.tasktracker.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktracker.data.User
import com.example.tasktracker.repositories.SharedRepository
import com.example.tasktracker.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class InfoUserViewModel
@Inject
constructor(
    private val userRepository: UserRepository,
    private val sharedRepository: SharedRepository,
) : ViewModel() {

    private val _dataCurrentUser = MutableStateFlow(User())
    val dataCurrentUser = _dataCurrentUser.asStateFlow()

    val dataUser = sharedRepository.userData
    val dataCompany = sharedRepository.companyData

    suspend fun updateCurrentUser(user: User) {
        _dataCurrentUser.update { user }
        val randomNumber = (0..1000).random()
        sharedRepository.setUpdateUsers("$randomNumber")
    }

    /** Обновление правила на редактирование задач */
    suspend fun updateOnEdit(user: User, onEdit: Boolean) {
        userRepository.updateOnEdit(user, onEdit)
    }

    /** Обновление правила на удаление задач */
    suspend fun updateOnDelete(user: User, onDelete: Boolean) {
        userRepository.updateOnDelete(user, onDelete)
    }

    init {
        viewModelScope.launch {
            sharedRepository.currentUser
                .filter { user -> user != null }
                .take(1)
                .collect { user ->
                    _dataCurrentUser.update { user }
                    Log.d("InfoUserViewModel", user.toString())
                }
        }
    }
}
