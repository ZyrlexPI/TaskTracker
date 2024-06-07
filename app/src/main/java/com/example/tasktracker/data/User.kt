package com.example.tasktracker.data

data class User(val name: String, val surname: String) {
    constructor() : this(name = "", surname = "")
}
