package com.example.tasktracker.repositories

import com.example.tasktracker.data.Task
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
    private val userSources: UserSources
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
        companySources.companySource.child(dataTask.companyId).child("members").setValue(dataTasks)
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

    /** Получить список заданий существующих в БД */
    suspend fun getListTasks(): List<Task> {
        val response = tasksSources.taskSource.get().await().children
        val listTasks = mutableListOf<Task>()
        response.forEach { data -> data.getValue<Task>()?.let { listTasks.add(it) } }
        return listTasks
    }
}
