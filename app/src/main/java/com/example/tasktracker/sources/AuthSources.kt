package com.example.tasktracker.sources

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthSources @Inject constructor() {
    private val firebaseAuthInstance = FirebaseAuth.getInstance()

    val firebaseAuth: FirebaseAuth
        get() = firebaseAuthInstance

    val firebaseUser: FirebaseUser?
        get() = firebaseAuthInstance.currentUser
}
