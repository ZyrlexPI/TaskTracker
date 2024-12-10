package com.example.tasktracker.repositories

import android.util.Log
import com.example.tasktracker.data.User
import com.example.tasktracker.sources.CommentsSources
import com.example.tasktracker.sources.CompanySources
import com.example.tasktracker.sources.TasksSources
import com.example.tasktracker.sources.UserSources
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.getValue
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class UserRepository
@Inject
constructor(
    private val userSources: UserSources,
    private val companySources: CompanySources,
    private val commentsSources: CommentsSources,
    private val tasksSources: TasksSources
) {

    /** Добавить нового пользователя в БД */
    suspend fun add(currentUser: FirebaseUser?) {
        if (currentUser !== null) {
            userSources.userSource
                .child(currentUser.uid)
                .setValue(User(currentUser.uid, "", "", "", "", false, false, listOf("")))
                .await()
        } else {
            Log.d("Exception", "currentUser is null")
        }
    }

    /** Получить список существующих пользователей в БД */
    suspend fun getListUsers(): MutableList<User> {
        val response = userSources.userSource.get().await().children
        val listUsers = mutableListOf<User>()
        response.forEach { data -> data.getValue<User>()?.let { listUsers.add(it) } }
        return listUsers
    }

    /** Получить данные о текущем пользователе из БД */
    suspend fun get(currentUser: FirebaseUser?): User {
        if (currentUser !== null) {
            val response = userSources.currentUser(currentUser.uid).get().await()
            val dataUser = response.getValue<User>()
            if (dataUser != null) {
                Log.d("UserData_get", dataUser.toString())
                return dataUser
            }
        } else {
            Log.d("Exception", "currentUser is null")
        }
        return User()
    }

    /** Получить данные о пользователе по id */
    suspend fun getUserById(id: String): User {
        val response = userSources.userSource.child(id).get().await()
        val dataUser = response.getValue<User>()
        if (dataUser != null) {
            Log.d("UserData_get", dataUser.toString())
            return dataUser
        }
        return User()
    }

    /** Обновление данных о пользователе. Личной информации. */
    suspend fun update(userData: User, name: String, surname: String): Boolean {
        val dataUserComment = mapOf("userName" to "$name $surname")
        /** Обновление данных у комментариев об авторе */
        try {
            val responseComments =
                commentsSources.commentsSource
                    .orderByChild("userId")
                    .equalTo(userData.id)
                    .get()
                    .await()
            if (responseComments.exists()) {
                responseComments.children.forEach {
                    commentsSources.commentsSource.child(it.key!!).updateChildren(dataUserComment)
                }
            }
        } catch (e: Exception) {
            Log.d("CommentsRepository", "Ошибка обновления данных об пользователе: ${e.message}")
        }

        val dataAuthor = mapOf("author" to "$name $surname")
        /** Обновление данных у задач об авторе */
        try {
            val responseAuthors =
                tasksSources.taskSource.orderByChild("author_id").equalTo(userData.id).get().await()
            if (responseAuthors.exists()) {
                responseAuthors.children.forEach {
                    tasksSources.taskSource.child(it.key!!).updateChildren(dataAuthor)
                }
            }
        } catch (e: Exception) {
            Log.d("UsersRepository", "Ошибка обновления данных об авторах: ${e.message}")
        }

        val dataExecutor = mapOf("executor" to "$name $surname")
        /** Обновление данных у задач об исполнителе */
        try {
            val responseAuthors =
                tasksSources.taskSource
                    .orderByChild("executor_id")
                    .equalTo(userData.id)
                    .get()
                    .await()
            if (responseAuthors.exists()) {
                responseAuthors.children.forEach {
                    tasksSources.taskSource.child(it.key!!).updateChildren(dataExecutor)
                }
            }
        } catch (e: Exception) {
            Log.d("UsersRepository", "Ошибка обновления данных об исполнителях: ${e.message}")
        }

        val dataUser = mapOf("name" to name, "surname" to surname)
        userSources.userSource.child(userData.id).updateChildren(dataUser)
        if (userData.companyId != "") {
            val response =
                companySources.companySource
                    .child(userData.companyId)
                    .child("members")
                    .get()
                    .await()
            val dataUser1 =
                User(
                    userData.id,
                    name,
                    surname,
                    userData.companyId,
                    userData.lastTaskViewId,
                    userData.onEdit,
                    userData.onDelete,
                    userData.tasks
                )
            val dataMembers = mutableListOf<User>()
            val valueMembers = response.getValue<List<User>>()
            valueMembers?.forEach { member ->
                if (member.id != userData.id) {
                    dataMembers.add(member)
                }
            }
            dataMembers.add(dataUser1)
            companySources.companySource
                .child(userData.companyId)
                .child("members")
                .setValue(dataMembers)
        }
        return true
    }

    /** Обновление правила на редактирование задач */
    suspend fun updateOnEdit(user: User, onEdit: Boolean) {
        val dataUser = mapOf("onEdit" to onEdit)
        userSources.userSource.child(user.id).updateChildren(dataUser)

        val response =
            companySources.companySource.child(user.companyId).child("members").get().await()
        val dataUser1 =
            User(
                user.id,
                user.name,
                user.surname,
                user.companyId,
                user.lastTaskViewId,
                onEdit,
                user.onDelete,
                user.tasks
            )
        val dataMembers = mutableListOf<User>()
        val valueMembers = response.getValue<List<User>>()
        valueMembers?.forEach { member ->
            if (member.id != user.id) {
                dataMembers.add(member)
            }
        }
        dataMembers.add(dataUser1)
        companySources.companySource.child(user.companyId).child("members").setValue(dataMembers)
    }

    /** Обновление правила на удаление задач */
    suspend fun updateOnDelete(user: User, onDelete: Boolean) {
        val dataUser = mapOf("onDelete" to onDelete)
        userSources.userSource.child(user.id).updateChildren(dataUser)

        val response =
            companySources.companySource.child(user.companyId).child("members").get().await()
        val dataUser1 =
            User(
                user.id,
                user.name,
                user.surname,
                user.companyId,
                user.lastTaskViewId,
                user.onEdit,
                onDelete,
                user.tasks
            )
        val dataMembers = mutableListOf<User>()
        val valueMembers = response.getValue<List<User>>()
        valueMembers?.forEach { member ->
            if (member.id != user.id) {
                dataMembers.add(member)
            }
        }
        dataMembers.add(dataUser1)
        companySources.companySource.child(user.companyId).child("members").setValue(dataMembers)
    }

    suspend fun updateLastTaskViewId(userData: User, lastTaskViewId: String) {
        val dataUser = mapOf("lastTaskViewId" to lastTaskViewId)
        userSources.currentUser(userData.id).updateChildren(dataUser)
    }
}
