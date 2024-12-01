package com.example.tasktracker.repositories

import android.util.Log
import com.example.tasktracker.data.Task
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SharedRepository @Inject constructor(private val tasksRepository: TasksRepository) {

    /** Задача, которая отображается на экране */
    private val _currentTask = MutableStateFlow<Task?>(null)
    val currentTask = _currentTask.asStateFlow()

    fun setCurrentTask(task: Task) {
        _currentTask.update { task }
        Log.d("SharedRepository", currentTask.value.toString())
    }
}
