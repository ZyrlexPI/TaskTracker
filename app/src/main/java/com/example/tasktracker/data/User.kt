package com.example.tasktracker.data

data class User(
    val id: String,
    val name: String,
    val surname: String,
    val companyId: String,
    val lastTaskViewId: String,
    val tasks: List<String>,
) {
    constructor() :
        this(
            id = "",
            name = "",
            surname = "",
            companyId = "",
            lastTaskViewId = "",
            tasks = listOf()
        )
}
