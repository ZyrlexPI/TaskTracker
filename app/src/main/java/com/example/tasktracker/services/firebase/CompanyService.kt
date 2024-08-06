package com.example.tasktracker.services.firebase

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tasktracker.data.Company
import com.example.tasktracker.data.User
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class CompanyService : ViewModel() {
    private val databaseCompaniesRef = FirebaseDatabase.getInstance().getReference("Сompanies")
    private val databaseUsersRef = FirebaseDatabase.getInstance().getReference("Users")
    private val _dataCompany = MutableStateFlow(Company())
    val dataCompany = _dataCompany.asStateFlow()

    /** Создание новой организации */
    suspend fun add(nameCompany: String, userData: User) {
        val pushKey = databaseCompaniesRef.push().key.toString()
        val data = mapOf("companyId" to pushKey)
        databaseUsersRef.child(userData.id).updateChildren(data)
        val dataUser = User(userData.id, userData.name, userData.surname, pushKey)
        databaseCompaniesRef
            .child(pushKey)
            .setValue(Company(pushKey, nameCompany, listOf(dataUser)))
            .await()
    }

    /** Получение информации о компании текущего пользователя, если он в ней состоит */
    suspend fun getCurrentCompany(userData: User) {
        if (userData.companyId != "") {
            val response = databaseCompaniesRef.child(userData.companyId).get().await()
            val data = response.getValue<Company>()
            if (data != null) {
                _dataCompany.value = data
            }
        } else {
            _dataCompany.value = Company()
            Log.d("CurrentCompany", "Company id is empty")
        }
    }

    /** Получить список компаний существующих в БД */
    suspend fun getListCompany(): MutableList<Company> {
        val response = databaseCompaniesRef.get().await().children
        val listCompanies = mutableListOf<Company>()
        response.forEach { data -> data.getValue<Company>()?.let { listCompanies.add(it) } }
        return listCompanies
    }

    /** Присоединение пользователя к существующей компании */
    suspend fun joinСompany(targetCompany: String, userData: User) {
        val data = mapOf("companyId" to targetCompany)
        databaseUsersRef.child(userData.id).updateChildren(data)
        val dataUser = User(userData.id, userData.name, userData.surname, targetCompany)

        val response = databaseCompaniesRef.child(targetCompany).child("members").get().await()
        val dataMembers = mutableListOf<User>()
        val valueMembers = response.getValue<List<User>>()
        if (valueMembers != null) {
            dataMembers.addAll(valueMembers)
        }
        dataMembers.add(dataUser)
        databaseCompaniesRef.child(targetCompany).child("members").setValue(dataMembers)
    }

    /** Удаление текущего пользователя из организации в которой состоит */
    suspend fun deleteCurrentUser(userData: User) {
        databaseUsersRef.child(userData.id).child("companyId").setValue("")
        val response = databaseCompaniesRef.child(userData.companyId).child("members").get().await()
        val dataMembers = mutableListOf<User>()
        val valueMembers = response.getValue<List<User>>()
        if (valueMembers != null) {
            valueMembers.forEach { data ->
                if (data.id != userData.id) {
                    dataMembers.add(data)
                }
            }
            databaseCompaniesRef.child(userData.companyId).child("members").setValue(dataMembers)
        }
    }
}
