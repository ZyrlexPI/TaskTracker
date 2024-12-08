package com.example.tasktracker.sources

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentsSources @Inject constructor() {
    private val commentsReference = FirebaseDatabase.getInstance().getReference("Comments")

    val commentsSource
        get() = commentsReference

    fun currentComment(commentId: String): DatabaseReference {
        return commentsReference.child(commentId)
    }
}
