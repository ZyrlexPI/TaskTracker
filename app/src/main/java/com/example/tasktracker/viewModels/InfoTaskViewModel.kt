package com.example.tasktracker.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktracker.data.Task
import com.example.tasktracker.data.User
import com.example.tasktracker.repositories.CompanyRepository
import com.example.tasktracker.repositories.SharedRepository
import com.example.tasktracker.repositories.TasksRepository
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
    private val tasksRepository: TasksRepository,
    private val companyRepository: CompanyRepository,
    private val sharedRepository: SharedRepository,
) : ViewModel() {

    val dataCompany = sharedRepository.companyData

    private val _listMembers = MutableStateFlow(listOf<User>())
    val listMembers = _listMembers.asStateFlow()

    /** Получить список участников компании */
    suspend fun getMembersCompany() {
        _listMembers.update {
            companyRepository.getMembersCompany(sharedRepository.companyData.value!!.id)
        }
    }

    private val _task = MutableStateFlow(Task())
    val task = _task.asStateFlow()

    //    @OptIn(ExperimentalCoroutinesApi::class)
    //    val userNameAuthor =
    //        task
    //            .map { task -> task.author_id }
    //            .flatMapLatest { flowOf(getUserNameById(it)) }
    //            .stateIn(scope = viewModelScope, SharingStarted.Lazily, initialValue = "")
    //    @OptIn(ExperimentalCoroutinesApi::class)
    //    val userNameExecutor =
    //        task
    //            .map { task -> task.executor_id }
    //            .flatMapLatest { flowOf(getUserNameById(it)) }
    //            .stateIn(scope = viewModelScope, SharingStarted.Lazily, initialValue = "")
    //
    //    private suspend fun getUserNameById(userId: String): String {
    //        val userData = userRepository.getUserById(userId)
    //        return userData.name + " " + userData.surname
    //    }

    suspend fun updateTask(task: Task) {
        _task.update { task }
    }

    /** Добавить пользователя в список наблюдателя */
    suspend fun addObserver(taskId: String, user: User) {
        tasksRepository.addObserver(taskId, user)
    }

    /** Удалить пользователя из списка наблюдателя */
    suspend fun deleteObserver(taskId: String, user: User) {
        tasksRepository.deleteObserver(taskId, user)
    }

    suspend fun getCurrentTask(taskId: String) {
        _task.update { tasksRepository.getCurrentTask(taskId) }
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

        viewModelScope.launch { task.collect { getMembersCompany() } }
    }
}
