package com.example.tasktracker.services.firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktracker.data.Comment
import com.example.tasktracker.data.Task
import com.example.tasktracker.enums.TaskStatus
import com.example.tasktracker.repositories.CommentsRepository
import com.example.tasktracker.repositories.NotificationsRepository
import com.example.tasktracker.repositories.SharedRepository
import com.example.tasktracker.repositories.TasksRepository
import com.example.tasktracker.repositories.UserRepository
import com.ravenzip.kotlinflowextended.functions.forkJoin
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TasksViewModel
@Inject
constructor(
    private val tasksRepository: TasksRepository,
    private val notificationsRepository: NotificationsRepository,
    private val userRepository: UserRepository,
    private val commentsRepository: CommentsRepository,
    private val sharedRepository: SharedRepository,
) : ViewModel() {

    private val _dataCurrentTask = MutableStateFlow(Task())
    val dataCurrentTask = _dataCurrentTask.asStateFlow()

    //    @OptIn(ExperimentalCoroutinesApi::class)
    //    val userNameAuthor =
    //        dataCurrentTask
    //            .map { task -> task.author_id }
    //            .flatMapLatest { flowOf(getUserNameById(it)) }
    //            .stateIn(scope = viewModelScope, SharingStarted.Lazily, initialValue = "")
    //    @OptIn(ExperimentalCoroutinesApi::class)
    //    val userNameExecutor =
    //        dataCurrentTask
    //            .map { task -> task.executor_id }
    //            .flatMapLatest { flowOf(getUserNameById(it)) }
    //            .stateIn(scope = viewModelScope, SharingStarted.Lazily, initialValue = "")

    private val _listTasks = MutableStateFlow(listOf<Task>())
    val listTasks = _listTasks.asStateFlow()

    private val _listComments = MutableStateFlow(listOf<Comment>())
    val listComments = _listComments.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val namesComments =
        listComments
            .filter { it.isNotEmpty() }
            .flatMapLatest { comments ->
                forkJoin(comments.map { flowOf(getFilteredComment(it.text, it.userId)) })
            }

    val countTask = listTasks.map { task -> task.count() }

    /** Подсчёт айди нового задания для уведомления */
    val idNewTask = listTasks.map { task -> task.last().id.toInt() + 1 }

    @OptIn(ExperimentalCoroutinesApi::class)
    val listActualTasks =
        listTasks
            .filter { it.isNotEmpty() }
            .flatMapLatest { tasks -> forkJoin(tasks.map { flowOf(getActualTask(it)) }) }

    val filteredTaskCount =
        listTasks.map { list ->
            TaskByType(
                new = list.count { task -> task.status == TaskStatus.NEW_TASK },
                inProgress = list.count { task -> task.status == TaskStatus.IN_PROGRESS },
                complete = list.count { task -> task.status == TaskStatus.COMPLETED }
            )
        }

    val filteredTaskUserCount =
        listTasks.map { list ->
            TaskByType(
                new =
                    list.count { task ->
                        task.status == TaskStatus.NEW_TASK &&
                            task.executor_id == sharedRepository.userData.value.id
                    },
                inProgress =
                    list.count { task ->
                        task.status == TaskStatus.IN_PROGRESS &&
                            task.executor_id == sharedRepository.userData.value.id
                    },
                complete =
                    list.count { task ->
                        task.status == TaskStatus.COMPLETED &&
                            task.executor_id == sharedRepository.userData.value.id
                    }
            )
        }

    private val updateListTask = MutableSharedFlow<Unit>()

    init {

        viewModelScope.launch {
            sharedRepository.userData.collect { getCurrentTask(it.lastTaskViewId) }
        }

        viewModelScope.launch {
            sharedRepository.currentTask.collect {
                if (it != null) {
                    getCurrentTask(it.id)
                }
            }
        }

        viewModelScope.launch {
            sharedRepository.currentTask.collect {
                if (it != null) {
                    getListComments(it.id)
                }
            }
        }

        viewModelScope.launch {
            sharedRepository.companyData.collect {
                _listTasks.update {
                    tasksRepository.getListTasks(sharedRepository.companyData.value.id)
                }
            }
        }
    }

    /** Добавление задачи */
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
        val pushKey = (tasksRepository.getListAllTasks().last().id.toInt() + 1).toString()

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

    /** Обновление задачи */
    suspend fun update(taskData: Task) {
        tasksRepository.update(taskData)
    }

    /** Удаление задачи */
    suspend fun delete(taskData: Task) = tasksRepository.delete(taskData)

    suspend fun getListTasks() = tasksRepository.getListTasks(sharedRepository.companyData.value.id)

    fun setCurrentTask(task: Task) = sharedRepository.setCurrentTask(task)

    suspend fun updateListTask() {
        _listTasks.update { tasksRepository.getListTasks(sharedRepository.companyData.value.id) }
    }

    suspend fun getCurrentTask(taskId: String) {
        _dataCurrentTask.update { tasksRepository.getCurrentTask(taskId) }
    }

    suspend fun addComment(userName: String, text: String, userId: String, taskId: String) {
        commentsRepository.add(userName, text, userId, taskId)
    }

    suspend fun getListComments(taskId: String) {
        _listComments.update { commentsRepository.getListComments(taskId) }
    }

    suspend fun getUserNameById(userId: String): String {
        val userData = userRepository.getUserById(userId)
        return userData.name + " " + userData.surname
    }

    suspend fun getFilteredComment(text: String, userId: String): CommentFiltered {
        val userData = userRepository.getUserById(userId)
        val dataFiltered =
            CommentFiltered(userName = userData.name + " " + userData.surname, text = text)
        return dataFiltered
    }

    suspend fun getActualTask(taskData: Task): Task {

        val authorData = userRepository.getUserById(taskData.author_id)
        if (taskData.author_id != taskData.executor_id) {
            val executorData = userRepository.getUserById(taskData.executor_id)
            val dataTask =
                Task(
                    id = taskData.id,
                    name = taskData.name,
                    status = taskData.status,
                    author = authorData.name + " " + authorData.surname,
                    author_id = taskData.author_id,
                    executor = executorData.name + " " + executorData.surname,
                    executor_id = taskData.executor_id,
                    companyId = taskData.companyId
                )
            return dataTask
        } else {
            val dataTask =
                Task(
                    id = taskData.id,
                    name = taskData.name,
                    status = taskData.status,
                    author = authorData.name + " " + authorData.surname,
                    author_id = taskData.author_id,
                    executor = authorData.name + " " + authorData.surname,
                    executor_id = taskData.executor_id,
                    companyId = taskData.companyId
                )
            return dataTask
        }
    }

    /** Добавление уведомления */
    suspend fun addNotification(date: String, event: String, userId: String) {
        notificationsRepository.add(date, event, userId)
        val randomNumber = (0..1000).random()
        sharedRepository.setUpdateNotifications("$date $randomNumber")
    }
}

class TaskByType(val new: Int = 0, val inProgress: Int = 0, val complete: Int = 0)

data class CommentFiltered(val userName: String = "", val text: String = "")
