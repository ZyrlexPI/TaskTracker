package com.example.tasktracker.repositories

import android.util.Log
import com.example.tasktracker.data.Result
import com.example.tasktracker.enums.AuthErrorsEnum
import com.example.tasktracker.sources.AuthSources
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@Singleton
class AuthRepository @Inject constructor(private val authSources: AuthSources) {
    private val firebaseAuth: FirebaseAuth
        get() = authSources.firebaseAuth

    val firebaseUser: FirebaseUser?
        get() = authSources.firebaseUser

    /** Регистрация пользователя с указанной электронной почтой и паролем */
    suspend fun createUserWithEmail(email: String, password: String): Result<AuthResult> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            Result(value = result, error = null)
        } catch (e: FirebaseAuthException) {
            val error = AuthErrorsEnum.getErrorMessage(e)
            withContext(Dispatchers.Main) {
                Log.d("Method", "CreateUserWithEmail")
                Log.d("FirebaseAuthException", error)
            }
            Result(value = null, error = error)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.d("Method", "CreateUserWithEmail")
                Log.d("Exception", "${e.message}")
            }
            Result(value = null, error = AuthErrorsEnum.ERROR_DEFAULT.value)
        }
    }

    /** Выполнение входа с указанной электронной почтой и паролем */
    suspend fun logInUserWithEmail(email: String, password: String): Result<AuthResult> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result(value = result, error = null)
        } catch (e: FirebaseTooManyRequestsException) {
            withContext(Dispatchers.Main) {
                Log.d("Method", "LoginUserWithEmail")
                Log.d(
                    "FirebaseTooManyRequestsException",
                    AuthErrorsEnum.ERROR_TOO_MANY_REQUESTS.value
                )
            }

            Result(value = null, error = AuthErrorsEnum.ERROR_TOO_MANY_REQUESTS.value)
        } catch (e: FirebaseAuthException) {
            val error = AuthErrorsEnum.getErrorMessage(e)
            withContext(Dispatchers.Main) {
                Log.d("Method", "LoginUserWithEmail")
                Log.d("FirebaseAuthException", error)
            }

            Result(value = null, error = error)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.d("Method", "LoginUserWithEmail")
                Log.d("Exception", "${e.message}")
            }

            Result(value = null, error = AuthErrorsEnum.ERROR_DEFAULT.value)
        }
    }

    /** Отправка ссылки на указанную почту для сброса пароля */
    suspend fun sendPasswordResetEmail(email: String): Boolean {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.d("Method", "SendPasswordResetEmail")
                Log.d("Exception", "${e.message}")
            }
            false
        }
    }

    /** Обновление пароля у пользователе */
    suspend fun updatePasswordUser(old_password: String, new_password: String): Boolean {
        return try {
            val credential =
                EmailAuthProvider.getCredential(firebaseUser?.email.toString(), old_password)
            firebaseUser?.reauthenticate(credential)?.await()
            firebaseUser?.updatePassword(new_password)?.await()
            true
        } catch (e: Exception) {
            Log.d("UpPass", e.message.toString())
            false
        }
    }

    /** Обновление данных о пользователе */
    suspend fun reloadUser(): Result<Boolean> {
        return try {
            firebaseUser?.reload()?.await()
            Result(value = true, error = null)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) { Log.d("ReloadResult", "${e.message}") }
            Result(value = false, error = "Не удалось обновить данные о пользователе")
        }
    }

    /** Выполнение выхода из аккаунта */
    suspend fun logout() {
        firebaseAuth.signOut()
        reloadUser()
    }
}
