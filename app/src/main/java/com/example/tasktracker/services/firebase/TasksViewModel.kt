package com.example.tasktracker.services.firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktracker.data.Task
import com.example.tasktracker.enums.TaskStatus
import com.example.tasktracker.repositories.SharedRepository
import com.example.tasktracker.repositories.TasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TasksViewModel
@Inject
constructor(
    private val tasksRepository: TasksRepository,
    private val sharedRepository: SharedRepository,
) : ViewModel() {

    private val _dataCurrentTask = MutableStateFlow(Task())
    val dataCurrentTask = _dataCurrentTask.asStateFlow()

    private val _listTasks = MutableStateFlow(listOf<Task>())
    val listTasks = _listTasks.asStateFlow()

    val filteredTaskCount =
        listTasks.map { list ->
            TaskByType(
                new = list.count { task -> task.status == TaskStatus.NEW_TASK },
                inProgress = list.count { task -> task.status == TaskStatus.IN_PROGRESS },
                complete = list.count { task -> task.status == TaskStatus.COMPLETED }
            )
        }

    private val updateListTask = MutableSharedFlow<Unit>()

    init {
        viewModelScope.launch {
            updateListTask.collect { _listTasks.update { tasksRepository.getListTasks() } }
        }

        viewModelScope.launch { updateListTask.emit(Unit) }
    }

    suspend fun add(
        nameTask: String,
        statusTask: TaskStatus,
        author: String,
        authorId: String,
        executor: String,
        executorId: String,
        companyId: String
    ) {

        /** Создание индефикатора задачи */
        val pushKey = (tasksRepository.getListTasks().count() + 1).toString()

        /** Формирование объекта задачи */
        val dataTask =
            Task(
                id = pushKey,
                name = nameTask,
                status = statusTask,
                author = author,
                author_id = authorId,
                executor = executor,
                executor_id = executorId,
                companyId = companyId
            )

        tasksRepository.add(dataTask)
    }

    suspend fun getListTasks() = tasksRepository.getListTasks()

    fun setCurrentTask(task: Task) = sharedRepository.setCurrentTask(task)

    fun updateListTask() {

        viewModelScope.launch { updateListTask.emit(Unit) }
    }

    suspend fun getCurrentTask(taskId: String) {
        _dataCurrentTask.update { tasksRepository.getCurrentTask(taskId)!! }
    }
}

class TaskByType(val new: Int = 0, val inProgress: Int = 0, val complete: Int = 0)
