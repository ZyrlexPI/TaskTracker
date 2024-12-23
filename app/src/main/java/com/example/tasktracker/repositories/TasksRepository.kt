package com.example.tasktracker.repositories

import android.util.Log
import com.example.tasktracker.data.Task
import com.example.tasktracker.data.User
import com.example.tasktracker.sources.CommentsSources
import com.example.tasktracker.sources.CompanySources
import com.example.tasktracker.sources.TasksSources
import com.example.tasktracker.sources.UserSources
import com.google.firebase.database.getValue
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class TasksRepository
@Inject
constructor(
    private val tasksSources: TasksSources,
    private val companySources: CompanySources,
    private val userSources: UserSources,
    private val commentsSources: CommentsSources,
) {

    /** Создание новой задачи */
    suspend fun add(dataTask: Task) {

        /** Добавление индефикатора задачи в компанию */
        val responseCompany =
            companySources.companySource.child(dataTask.companyId).child("tasks").get().await()
        val dataTasks = mutableListOf<String>()
        val valueTasks = responseCompany.getValue<List<String>>()

        if (valueTasks != null) {
            dataTasks.addAll(valueTasks)
        }

        dataTasks.add(dataTask.id)
        companySources.companySource.child(dataTask.companyId).child("tasks").setValue(dataTasks)
        /** Добавление индефикатора задачи автору */
        val responseUser =
            userSources.userSource.child(dataTask.author_id).child("tasks").get().await()
        val dataUsersTasks = mutableListOf<String>()
        val valueUsersTasks = responseUser.getValue<List<String>>()

        if (valueUsersTasks != null) {
            dataUsersTasks.addAll(valueUsersTasks)
        }

        dataUsersTasks.add(dataTask.id)
        userSources.userSource.child(dataTask.author_id).child("tasks").setValue(dataUsersTasks)
        /** Добавление индефикатора задачи исполнителю, если он не автор */
        if (dataTask.author_id != dataTask.executor_id) {
            val responseExecutor =
                userSources.userSource.child(dataTask.executor_id).child("tasks").get().await()
            val dataExecutorTasks = mutableListOf<String>()
            val valueExecutorTasks = responseExecutor.getValue<List<String>>()
            if (valueExecutorTasks != null) {
                dataExecutorTasks.addAll(valueExecutorTasks)
            }
            dataExecutorTasks.add(dataTask.id)
            userSources.userSource
                .child(dataTask.executor_id)
                .child("tasks")
                .setValue(dataExecutorTasks)
        }
        /** Добавление задачи в БД */
        tasksSources.taskSource.child(dataTask.id).setValue(dataTask).await()
    }

    /** Обновление данных задачи */
    suspend fun update(dataTask: Task) {
        tasksSources.taskSource.child(dataTask.id).setValue(dataTask).await()
    }

    /** Удаление задачи */
    suspend fun delete(taskData: Task) {
        /** Удаление комментарие задачи */
        try {
            val responseComments =
                commentsSources.commentsSource
                    .orderByChild("taskId")
                    .equalTo(taskData.id)
                    .get()
                    .await()
            if (responseComments.exists()) {
                responseComments.children.forEach {
                    commentsSources.commentsSource.child(it.key!!).removeValue().await()
                }
            }
        } catch (e: Exception) {
            Log.d("CommentsRepository", "delete: ${e.message}")
        }
        /** Удаление задачи из компании */
        val responseCompany =
            companySources.companySource.child(taskData.companyId).child("tasks").get().await()
        val dataTaskInCompany = mutableListOf<String>()
        val valueTaskInCompany = responseCompany.getValue<List<String>>()
        if (valueTaskInCompany != null) {
            valueTaskInCompany.forEach { data ->
                if (data != taskData.id) {
                    dataTaskInCompany.add(data)
                }
            }
            companySources.companySource
                .child(taskData.companyId)
                .child("tasks")
                .setValue(dataTaskInCompany)
        }
        /** Удаление задачи из автора */
        val responseAuthor =
            userSources.userSource.child(taskData.author_id).child("tasks").get().await()
        val dataTaskInAuthor = mutableListOf<String>()
        val valueTaskInAuthor = responseAuthor.getValue<List<String>>()
        if (valueTaskInAuthor != null) {
            valueTaskInAuthor.forEach { data ->
                if (data != taskData.id) {
                    dataTaskInAuthor.add(data)
                }
            }
            userSources.userSource
                .child(taskData.author_id)
                .child("tasks")
                .setValue(dataTaskInAuthor)
        }
        /** Удаление задачи из исполнителя */
        if (taskData.author_id != taskData.executor_id) {
            val responseExecutor =
                userSources.userSource.child(taskData.executor_id).child("tasks").get().await()
            val dataTaskInExecutor = mutableListOf<String>()
            val valueTaskInExecutor = responseExecutor.getValue<List<String>>()
            if (valueTaskInExecutor != null) {
                valueTaskInExecutor.forEach { data ->
                    if (data != taskData.id) {
                        dataTaskInExecutor.add(data)
                    }
                }
                userSources.userSource
                    .child(taskData.executor_id)
                    .child("tasks")
                    .setValue(dataTaskInExecutor)
            }
        }
        /** Удаление задачи из БД */
        tasksSources.taskSource.child(taskData.id).removeValue().await()
    }

    /** Получить список заданий существующих в БД */
    suspend fun getListAllTasks(): List<Task> {
        val response = tasksSources.taskSource.get().await().children
        val listTasks = mutableListOf<Task>()
        response.forEach { data -> data.getValue<Task>()?.let { listTasks.add(it) } }
        return listTasks
    }

    suspend fun getListTasks(companyId: String): List<Task> {
        val response =
            tasksSources.taskSource
                .orderByChild("companyId")
                .equalTo(companyId)
                .get()
                .await()
                .children
        val listTasks = mutableListOf<Task>()
        response.forEach { data -> data.getValue<Task>()?.let { listTasks.add(it) } }
        return listTasks
    }

    /** Получить задачу из БД */
    suspend fun getCurrentTask(taskId: String): Task {
        if (taskId != "") {
            val response = tasksSources.currentTask(taskId).get().await()
            val dataTaskCurrent = response.getValue<Task>()
            if (dataTaskCurrent != null) {
                Log.d("taskId_get", dataTaskCurrent.toString())
                return dataTaskCurrent
            }
        } else {
            Log.d("Exception", "taskId is null")
        }
        return Task()
    }

    /** Добавить пользователя в список наблюдателя */
    suspend fun addObserver(taskId: String, user: User) {

        val response = tasksSources.taskSource.child(taskId).child("observers").get().await()
        val dataObservers = mutableListOf<User>()
        val valueObservers = response.getValue<List<User>>()
        if (valueObservers != null) {
            dataObservers.addAll(valueObservers)
        }
        dataObservers.add(user)
        tasksSources.taskSource.child(taskId).child("observers").setValue(dataObservers)
    }

    /** Удалить пользователя из списка наблюдателя */
    suspend fun deleteObserver(taskId: String, user: User) {

        val response = tasksSources.taskSource.child(taskId).child("observers").get().await()
        val dataObservers = mutableListOf<User>()
        val valueObservers = response.getValue<List<User>>()
        if (valueObservers != null) {
            dataObservers.addAll(valueObservers)
        }
        dataObservers.remove(user)
        tasksSources.taskSource.child(taskId).child("observers").setValue(dataObservers)
    }
}
