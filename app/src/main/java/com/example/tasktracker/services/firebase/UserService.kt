package com.example.tasktracker.services.firebase

import android.util.Log
import com.example.tasktracker.data.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class UserService() {
    private val databaseRef = FirebaseDatabase.getInstance().getReference("Users")

    private val _dataUser = MutableStateFlow(User())
    val dataUser = _dataUser.asStateFlow()

    suspend fun add(currentUser: FirebaseUser?) {
        if (currentUser !== null) {
            databaseRef.child(currentUser.uid).setValue(User("Name", "Surname")).await()
        } else {
            Log.d("Exception", "currentUser is null")
        }
    }

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

    fun update(currentUser: FirebaseUser?, name: String, surname: String) {
        if (currentUser !== null) {
            databaseRef.child(currentUser.uid).child("name").setValue(name)
            databaseRef.child(currentUser.uid).child("surname").setValue(surname)
        } else {
            Log.d("Exception", "currentUser is null")
        }
    }
}
