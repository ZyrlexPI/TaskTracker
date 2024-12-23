package com.example.tasktracker.sources

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TasksSources @Inject constructor() {
    private val tasksReference = FirebaseDatabase.getInstance().getReference("Tasks")

    val taskSource
        get() = tasksReference

    fun currentTask(taskId: String): DatabaseReference {
        return tasksReference.child(taskId)
    }
}
