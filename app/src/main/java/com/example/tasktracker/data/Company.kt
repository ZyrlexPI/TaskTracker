package com.example.tasktracker.data

data class Company(
    val id: String,
    val name: String,
    val members: List<User>,
    val tasks: List<String>
) {
    constructor() : this(id = "", name = "", members = listOf(), tasks = listOf())
}
