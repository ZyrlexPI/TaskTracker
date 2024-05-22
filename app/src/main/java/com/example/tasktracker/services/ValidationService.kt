package com.example.tasktracker.services

import android.util.Patterns

class ValidationService {
    fun isEmailValid(value: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(value).matches()
    }

    fun isPasswordValid(value: String): Boolean {
        return value.length > 5
    }
}
