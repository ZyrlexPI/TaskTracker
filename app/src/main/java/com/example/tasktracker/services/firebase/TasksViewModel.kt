package com.example.tasktracker.services.firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktracker.data.Comment
import com.example.tasktracker.data.Task
import com.example.tasktracker.enums.TaskStatus
import com.example.tasktracker.repositories.CommentsRepository
import com.example.tasktracker.repositories.SharedRepository
import com.example.tasktracker.repositories.TasksRepository
import com.example.tasktracker.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TasksViewModel
@Inject
constructor(
    private val tasksRepository: TasksRepository,
    private val userRepository: UserRepository,
    private val commentsRepository: CommentsRepository,
    private val sharedRepository: SharedRepository,
) : ViewModel() {

    private val _dataCurrentTask = MutableStateFlow(Task())
    val dataCurrentTask = _dataCurrentTask.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val userNameAuthor =
        dataCurrentTask
            .map { task -> task.author_id }
            .flatMapLatest { flowOf(getUserNameById(it)) }
            .stateIn(scope = viewModelScope, SharingStarted.Lazily, initialValue = "")
    @OptIn(ExperimentalCoroutinesApi::class)
    val userNameExecutor =
        dataCurrentTask
            .map { task -> task.executor_id }
            .flatMapLatest { flowOf(getUserNameById(it)) }
            .stateIn(scope = viewModelScope, SharingStarted.Lazily, initialValue = "")

    private val _listTasks = MutableStateFlow(listOf<Task>())
    val listTasks = _listTasks.asStateFlow()

    private val _listComments = MutableStateFlow(listOf<Comment>())
    val listComments = _listComments.asStateFlow()

    //    @OptIn(ExperimentalCoroutinesApi::class)
    //    val namesComments =
    //        listComments
    //            .onEach { Log.d("NamesComments_get", it.toString()) }
    //            .filter { it.isNotEmpty() }
    //            .flatMapLatest { comments ->
    //                forkJoin(
    //                    comments.map { flowOf(getFilteredComment(it.text, it.userId)) },
    //                    flowLimit = if (comments.count() <= 3) comments.count() else 3
    //                )
    //            }
    //            .onEach { Log.d("NamesComments_get_2", it.toString()) }

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
            updateListTask.collect { _listTasks.update { tasksRepository.getListTasks() } }
        }

        viewModelScope.launch { updateListTask.emit(Unit) }
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
        val pushKey = (tasksRepository.getListTasks().last().id.toInt() + 1).toString()

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

        /** Формирование объекта задачи */
        //        val dataTask =
        //            Task(
        //                id = id,
        //                name = nameTask,
        //                status = statusTask,
        //                author = author,
        //                author_id = authorId,
        //                executor = executor,
        //                executor_id = executorId,
        //                companyId = companyId
        //            )

        tasksRepository.update(taskData)
    }

    /** Удаление задачи */
    suspend fun delete(taskData: Task) = tasksRepository.delete(taskData)

    suspend fun getListTasks() = tasksRepository.getListTasks()

    fun setCurrentTask(task: Task) = sharedRepository.setCurrentTask(task)

    fun updateListTask() {
        viewModelScope.launch { updateListTask.emit(Unit) }
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
}

class TaskByType(val new: Int = 0, val inProgress: Int = 0, val complete: Int = 0)

data class CommentFiltered(val userName: String = "", val text: String = "")
