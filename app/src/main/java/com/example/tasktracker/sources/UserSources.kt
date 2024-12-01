package com.example.tasktracker.sources

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSources @Inject constructor() {
    private val usersReference = FirebaseDatabase.getInstance().getReference("Users")

    val userSource
        get(): DatabaseReference = usersReference

    fun currentUser(userUid: String): DatabaseReference {
        return usersReference.child(userUid)
    }
}
