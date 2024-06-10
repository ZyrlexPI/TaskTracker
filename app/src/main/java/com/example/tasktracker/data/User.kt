package com.example.tasktracker.data

data class User(val id: String, val name: String, val surname: String, val companyId: String) {
    constructor() : this(id = "", name = "", surname = "", companyId = "")
}
