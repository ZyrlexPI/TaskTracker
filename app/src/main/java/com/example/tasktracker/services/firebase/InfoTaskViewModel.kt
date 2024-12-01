package com.example.tasktracker.services.firebase

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktracker.data.Task
import com.example.tasktracker.repositories.SharedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class InfoTasksViewModel
@Inject
constructor(
    private val sharedRepository: SharedRepository,
) : ViewModel() {

    private val _task = MutableStateFlow(Task())
    val task = _task.asStateFlow()

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
