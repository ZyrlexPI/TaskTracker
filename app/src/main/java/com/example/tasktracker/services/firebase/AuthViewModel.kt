package com.example.tasktracker.services.firebase

import androidx.lifecycle.ViewModel
import com.example.tasktracker.data.Result
import com.example.tasktracker.repositories.AuthRepository
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel
@Inject
constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    /** Регистрация пользователя с указанной электронной почтой и паролем */
    suspend fun createUserWithEmail(email: String, password: String): Result<AuthResult> {
        return authRepository.createUserWithEmail(email, password)
    }

    /** Выполнение входа с указанной электронной почтой и паролем */
    suspend fun logInUserWithEmail(email: String, password: String): Result<AuthResult> {
        return authRepository.logInUserWithEmail(email, password)
    }

    /** Отправка ссылки на указанную почту для сброса пароля */
    suspend fun sendPasswordResetEmail(email: String): Boolean {
        return authRepository.sendPasswordResetEmail(email)
    }

    /** Обновление пароля у пользователе */
    suspend fun updatePasswordUser(old_password: String, new_password: String): Boolean {
        return authRepository.updatePasswordUser(old_password, new_password)
    }

    /** Обновление данных о пользователе */
    suspend fun reloadUser(): Result<Boolean> {
        return authRepository.reloadUser()
    }

    /** Получение текущего пользователя */
    fun getUser(): FirebaseUser? {
        return authRepository.getUser()
    }

    /** Выполнение выхода из аккаунта */
    suspend fun logout() {
        authRepository.logout()
    }
}
