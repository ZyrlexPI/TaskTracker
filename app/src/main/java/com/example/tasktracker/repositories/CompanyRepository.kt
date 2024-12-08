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
constructor(private val companySources: CompanySources, private val userSources: UserSources) {

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
        userSources.userSource.child(userData.id).child("companyId").setValue("")
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
}
