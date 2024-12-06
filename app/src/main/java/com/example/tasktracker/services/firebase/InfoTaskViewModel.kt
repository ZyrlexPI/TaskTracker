package com.example.tasktracker.services.firebase

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktracker.data.Task
import com.example.tasktracker.repositories.SharedRepository
import com.example.tasktracker.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class InfoTasksViewModel
@Inject
constructor(
    private val userRepository: UserRepository,
    private val sharedRepository: SharedRepository,
) : ViewModel() {

    private val _task = MutableStateFlow(Task())
    val task = _task.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val userNameAuthor =
        task
            .map { task -> task.author_id }
            .flatMapLatest { flowOf(getUserNameById(it)) }
            .stateIn(scope = viewModelScope, SharingStarted.Lazily, initialValue = "")
    @OptIn(ExperimentalCoroutinesApi::class)
    val userNameExecutor =
        task
            .map { task -> task.executor_id }
            .flatMapLatest { flowOf(getUserNameById(it)) }
            .stateIn(scope = viewModelScope, SharingStarted.Lazily, initialValue = "")

    private suspend fun getUserNameById(userId: String): String {
        val userData = userRepository.getUserById(userId)
        return userData.name + " " + userData.surname
    }

    suspend fun updateTask(task: Task) {
        _task.update { task }
    }

    init {
        viewModelScope.launch {
            sharedRepository.currentTask
                .filter { task -> task != null }
                .take(1)
                .collect { task ->
                    _task.update { task!! }
                    Log.d("InfoTaskViewModel", task.toString())
                }
        }

        viewModelScope.launch {
            sharedRepository.currentTask.collect { Log.d("emptyCOLLECT", it.toString()) }
        }
    }
}
