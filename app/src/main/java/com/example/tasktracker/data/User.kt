package com.example.tasktracker.data

data class User(
    val id: String,
    val name: String,
    val surname: String,
    val companyId: String,
    val lastTaskViewId: String,
    val onEdit: Boolean,
    val onDelete: Boolean,
    val tasks: List<String>,
) {
    constructor() :
        this(
            id = "",
            name = "",
            surname = "",
            companyId = "",
            lastTaskViewId = "",
            onEdit = true,
            onDelete = true,
            tasks = listOf()
        )
}
