package com.example.tasktracker.services.firebase

import androidx.lifecycle.ViewModel
import com.example.tasktracker.data.Company
import com.example.tasktracker.data.Task
import com.example.tasktracker.enums.TaskStatus
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class TasksViewModel : ViewModel() {
    private val databaseCompaniesRef = FirebaseDatabase.getInstance().getReference("Сompanies")
    private val databaseUsersRef = FirebaseDatabase.getInstance().getReference("Users")
    private val databaseTasksRef = FirebaseDatabase.getInstance().getReference("Tasks")
    private val _dataTask = MutableStateFlow(Company())
    val dataTask = _dataTask.asStateFlow()

    /** Создание новой задачи */
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
        val pushKey = databaseTasksRef.push().key.toString()
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
        /** Добавление индефикатора задачи в компанию */
        val responseCompany = databaseCompaniesRef.child(companyId).child("tasks").get().await()
        val dataTasks = mutableListOf<String>()
        val valueTasks = responseCompany.getValue<List<String>>()
        if (valueTasks != null) {
            dataTasks.addAll(valueTasks)
        }
        dataTasks.add(pushKey)
        databaseCompaniesRef.child(companyId).child("members").setValue(dataTasks)
        /** Добавление индефикатора задачи автору */
        val responseUser = databaseUsersRef.child(authorId).child("tasks").get().await()
        val dataUsersTasks = mutableListOf<String>()
        val valueUsersTasks = responseUser.getValue<List<String>>()
        if (valueUsersTasks != null) {
            dataUsersTasks.addAll(valueUsersTasks)
        }
        dataUsersTasks.add(pushKey)
        databaseUsersRef.child(authorId).child("tasks").setValue(dataUsersTasks)
        /** Добавление индефикатора задачи исполнителю, если он не автор */
        if (authorId != executorId) {
            val responseExecutor = databaseUsersRef.child(executorId).child("tasks").get().await()
            val dataExecutorTasks = mutableListOf<String>()
            val valueExecutorTasks = responseExecutor.getValue<List<String>>()
            if (valueExecutorTasks != null) {
                dataExecutorTasks.addAll(valueExecutorTasks)
            }
            dataExecutorTasks.add(pushKey)
            databaseUsersRef.child(executorId).child("tasks").setValue(dataExecutorTasks)
        }
        /** Добавление задачи в БД */
        databaseTasksRef.child(pushKey).setValue(dataTask).await()
    }
}
