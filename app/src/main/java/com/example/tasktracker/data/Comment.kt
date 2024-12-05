package com.example.tasktracker.data

data class Comment(
    val id: String,
    val userName: String,
    val text: String,
    val userId: String,
    val taskId: String
) {
    constructor() : this("", "", "", "", "")
}
