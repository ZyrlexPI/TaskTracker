package com.example.tasktracker.data

data class Notification(
    val id: String,
    val dateTime: String,
    val event: String,
    val userId: String
) {
    constructor() : this(id = "", dateTime = "", event = "", userId = "")
}
