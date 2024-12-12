package com.example.tasktracker.repositories

import android.util.Log
import com.example.tasktracker.data.Company
import com.example.tasktracker.data.User
import com.example.tasktracker.sources.CompanySources
import com.example.tasktracker.sources.UserSources
import com.google.firebase.database.getValue
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class CompanyRepository
@Inject
constructor(
    private val companySources: CompanySources,
    private val userSources: UserSources,
    private val tasksRepository: TasksRepository
) {

    /** Создание новой организации */
    suspend fun add(nameCompany: String, userData: User) {
        val pushKey = companySources.companySource.push().key.toString()
        val data = mapOf("companyId" to pushKey)
        userSources.userSource.child(userData.id).updateChildren(data)
        val dataUser =
            User(
                userData.id,
                userData.name,
                userData.surname,
                pushKey,
                userData.lastTaskViewId,
                userData.onEdit,
                userData.onDelete,
                userData.tasks
            )
        companySources.companySource
            .child(pushKey)
            .setValue(Company(pushKey, nameCompany, userData.id, listOf(dataUser), listOf()))
            .await()
    }

    /** Получение информации о компании текущего пользователя, если он в ней состоит */
    suspend fun getCurrentCompany(userData: User): Company {
        if (userData.companyId != "") {
            val response = companySources.companySource.child(userData.companyId).get().await()
            val dataCompanyCurrent = response.getValue<Company>()
            if (dataCompanyCurrent != null) {
                return dataCompanyCurrent
            }
        } else {
            Log.d("CurrentCompany", "Company id is empty")
        }
        return Company()
    }

    /** Получить список компаний существующих в БД */
    suspend fun getListCompany(): MutableList<Company> {
        val response = companySources.companySource.get().await().children
        val listCompanies = mutableListOf<Company>()
        response.forEach { data -> data.getValue<Company>()?.let { listCompanies.add(it) } }
        return listCompanies
    }

    /** Присоединение пользователя к существующей компании */
    suspend fun joinCompany(targetCompany: String, userData: User) {
        val data = mapOf("companyId" to targetCompany)
        userSources.userSource.child(userData.id).updateChildren(data)
        val dataUser =
            User(
                userData.id,
                userData.name,
                userData.surname,
                targetCompany,
                userData.lastTaskViewId,
                userData.onEdit,
                userData.onDelete,
                userData.tasks
            )

        val response =
            companySources.companySource.child(targetCompany).child("members").get().await()
        val dataMembers = mutableListOf<User>()
        val valueMembers = response.getValue<List<User>>()
        if (valueMembers != null) {
            dataMembers.addAll(valueMembers)
        }
        dataMembers.add(dataUser)
        companySources.companySource.child(targetCompany).child("members").setValue(dataMembers)
    }

    /** Удаление текущего пользователя из организации в которой состоит */
    suspend fun deleteCurrentUser(userData: User) {
        val dataUser =
            mapOf("companyId" to "", "lastTaskViewId" to "", "onEdit" to true, "onDelete" to true)
        userSources.userSource.child(userData.id).updateChildren(dataUser)
        val response =
            companySources.companySource.child(userData.companyId).child("members").get().await()
        val dataMembers = mutableListOf<User>()
        val valueMembers = response.getValue<List<User>>()
        if (valueMembers != null) {
            valueMembers.forEach { data ->
                if (data.id != userData.id) {
                    dataMembers.add(data)
                }
            }
            companySources.companySource
                .child(userData.companyId)
                .child("members")
                .setValue(dataMembers)
        }
    }

    /** Получение списка пользователей в компании */
    suspend fun getMembersCompany(targetCompany: String): MutableList<User> {
        val response =
            companySources.companySource.child(targetCompany).child("members").get().await()
        val dataMembers = mutableListOf<User>()
        val valueMembers = response.getValue<List<User>>()
        if (response.exists()) {
            if (valueMembers != null) {
                dataMembers.addAll(valueMembers)
            }
        }
        return dataMembers
    }

    /** Обновление имени компании */
    suspend fun updateNameCompany(targetCompany: String, nameCompany: String) {
        val data = mapOf("name" to nameCompany)
        companySources.companySource.child(targetCompany).updateChildren(data)
    }

    /** Изменение создателя компании */
    suspend fun changeCreatorCompany(targetCompany: String, userId: String) {
        /** Устанавливаем все права новому создателю */
        val dataUser = mapOf("onEdit" to true, "onDelete" to true)
        userSources.userSource.child(userId).updateChildren(dataUser)
        /** Обновление данных об новом создателе */
        val response =
            companySources.companySource.child(targetCompany).child("members").get().await()
        var dataUser1 = User()
        val dataMembers = mutableListOf<User>()
        val valueMembers = response.getValue<List<User>>()
        valueMembers?.forEach { member ->
            if (member.id != userId) {
                dataMembers.add(member)
            } else {
                dataUser1 = member
            }
        }
        val dataUser2 = dataUser1.copy(onEdit = true, onDelete = true)
        dataMembers.add(dataUser2)
        companySources.companySource.child(targetCompany).child("members").setValue(dataMembers)
        /** Устанавливаем нового создателя */
        val dataCompany = mapOf("creatorId" to userId)
        companySources.companySource.child(targetCompany).updateChildren(dataCompany)
    }

    /** Удаление компании */
    suspend fun deleteCompany(targetCompany: String) {
        /** Удаляем все задачи в компании */
        val responseTasks =
            companySources.companySource.child(targetCompany).child("tasks").get().await()
        val valueTasks = responseTasks.getValue<List<String>>()
        if (responseTasks.exists()) {
            if (valueTasks != null) {
                valueTasks.forEach { taskId ->
                    tasksRepository.delete(tasksRepository.getCurrentTask(taskId))
                }
            }
        }
        /**
         * Удаляем всем пользователям компании индентификатор организации и устанавливаем права
         * поумолчанию
         */
        val responseUsers =
            companySources.companySource.child(targetCompany).child("members").get().await()
        val valueUsers = responseUsers.getValue<List<User>>()
        val dataUser = mapOf("companyId" to "", "onEdit" to true, "onDelete" to true)
        if (responseUsers.exists()) {
            if (valueUsers != null) {
                valueUsers.forEach { user ->
                    userSources.userSource.child(user.id).updateChildren(dataUser)
                }
            }
        }
        /** Удаляем организацию */
        companySources.companySource.child(targetCompany).removeValue()
    }
}
