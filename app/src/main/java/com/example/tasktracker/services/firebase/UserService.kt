package com.example.tasktracker.services.firebase

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tasktracker.data.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class UserService : ViewModel() {
    private val databaseRef = FirebaseDatabase.getInstance().getReference("Users")
    private val databaseCompaniesRef = FirebaseDatabase.getInstance().getReference("Сompanies")
    private val _dataUser = MutableStateFlow(User())
    val dataUser = _dataUser.asStateFlow()

    /** Добавить нового пользователя в БД */
    suspend fun add(currentUser: FirebaseUser?) {
        if (currentUser !== null) {
            databaseRef.child(currentUser.uid).setValue(User(currentUser.uid, "", "", "")).await()
        } else {
            Log.d("Exception", "currentUser is null")
        }
    }

    /** Получить данные о текущем пользователе из БД */
    suspend fun get(currentUser: FirebaseUser?) {
        if (currentUser !== null) {
            val response = databaseRef.child(currentUser.uid).get().await()
            val data = response.getValue<User>()
            if (data != null) {
                _dataUser.value = data
            }
            Log.d("UserData", dataUser.value.toString())
        } else {
            Log.d("Exception", "currentUser is null")
        }
    }

    /** Обновление данных о пользователе. Личной информации. */
    suspend fun update(userData: User, name: String, surname: String): Boolean {
        if (userData !== null) {
            val data = mapOf("name" to name, "surname" to surname)
            databaseRef.child(userData.id).updateChildren(data)
            if (userData.companyId != "") {
                val response =
                    databaseCompaniesRef.child(userData.companyId).child("members").get().await()
                val dataUser = User(userData.id, name, surname, userData.companyId)
                val dataMembers = mutableListOf<User>()
                val valueMembers = response.getValue<List<User>>()
                valueMembers?.forEach { data ->
                    if (data.id != userData.id) {
                        dataMembers.add(data)
                    }
                }
                dataMembers.add(dataUser)
                databaseCompaniesRef
                    .child(userData.companyId)
                    .child("members")
                    .setValue(dataMembers)
            }
            return true
        } else {
            Log.d("Exception", "dataUser is null")
            return false
        }
    }
}
