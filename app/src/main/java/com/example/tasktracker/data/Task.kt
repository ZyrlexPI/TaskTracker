package com.example.tasktracker.data

import com.example.tasktracker.enums.TaskStatus

data class Task(
    val id: String,
    val name: String,
    val status: TaskStatus,
    val author: String,
    val author_id: String,
    val executor: String,
    val executor_id: String,
    val companyId: String,
    val observers: List<User>,
) {
    constructor() :
        this(
            id = "",
            name = "",
            status = TaskStatus.NEW_TASK,
            author = "",
            author_id = "",
            executor = "",
            executor_id = "",
            companyId = "",
            observers = listOf()
        )
}
