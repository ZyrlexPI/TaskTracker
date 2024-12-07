package com.example.tasktracker.sources

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationsSource @Inject constructor() {
    private val notificationsReference =
        FirebaseDatabase.getInstance().getReference("Notifications")

    val notificationsSource
        get() = notificationsReference

    fun currentNotification(commentId: String): DatabaseReference {
        return notificationsReference.child(commentId)
    }
}
