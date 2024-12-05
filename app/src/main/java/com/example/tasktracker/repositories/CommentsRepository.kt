package com.example.tasktracker.repositories

import com.example.tasktracker.data.Comment
import com.example.tasktracker.sources.CommentsSources
import com.google.firebase.database.getValue
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class CommentsRepository @Inject constructor(private val commentsSources: CommentsSources) {

    /** Создание нового комментария */
    suspend fun add(userName: String, text: String, userId: String, taskId: String) {
        /** Создание индефикатора комментария */
        val pushKey = commentsSources.commentsSource.push().key.toString()
        /** Создание объекта комментария */
        val commentData = Comment(pushKey, userName, text, userId, taskId)
        /** Добавление комментария в БД */
        commentsSources.commentsSource.child(pushKey).setValue(commentData).await()
    }

    /** Получение списка комментариев задачи */
    suspend fun getListComments(taskId: String): MutableList<Comment> {
        val response = commentsSources.commentsSource.get().await().children
        val listComments = mutableListOf<Comment>()
        response.forEach { data ->
            data
                .getValue<Comment>()
                ?.takeIf { comment -> comment.taskId == taskId }
                .let { it?.let { it1 -> listComments.add(it1) } }
        }
        return listComments
    }
}
