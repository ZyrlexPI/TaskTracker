package com.example.tasktracker.services.firebase

import androidx.lifecycle.ViewModel
import com.example.tasktracker.data.Company
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TasksViewModel : ViewModel() {
    private val databaseCompaniesRef = FirebaseDatabase.getInstance().getReference("Сompanies")
    private val databaseUsersRef = FirebaseDatabase.getInstance().getReference("Users")
    private val databaseTasksRef = FirebaseDatabase.getInstance().getReference("Tasks")
    private val _dataTask = MutableStateFlow(Company())
    val dataTask = _dataTask.asStateFlow()
}
