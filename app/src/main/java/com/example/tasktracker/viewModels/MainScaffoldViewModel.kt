package com.example.tasktracker.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktracker.repositories.SharedRepository
import com.example.tasktracker.repositories.UserRepository
import com.example.tasktracker.services.viewModels.getUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MainScaffoldViewModel
@Inject
constructor(
    private val userRepository: UserRepository,
    private val sharedRepository: SharedRepository,
) : ViewModel() {
    private val _countNotifications = MutableStateFlow(0)
    val countNotifications = _countNotifications.asStateFlow()

    init {

        viewModelScope.launch {
            sharedRepository.updateNotifications.collect {
                sharedRepository.updateCountNotifications(userRepository.get(getUser()))
            }
        }

        viewModelScope.launch {
            sharedRepository.countNotifications.collect { _countNotifications.value = it }
        }
    }
}
