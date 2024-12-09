package com.example.tasktracker.screens.main.home

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasktracker.screens.main.noAccess.NoAccessScreen
import com.example.tasktracker.viewModels.TasksViewModel
import com.example.tasktracker.viewModels.UserViewModel
import com.ravenzip.workshop.components.TopAppBar

@Composable
fun HomeScreenScaffold(
    padding: PaddingValues,
    navigationToLastViewTask: () -> Unit,
    userViewModel: UserViewModel,
    tasksViewModel: TasksViewModel,
) {
    val userData = userViewModel.dataUser.collectAsStateWithLifecycle().value
    Log.d("UserDataInHomeScreen", userData.toString())
    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = { TopAppBar(title = "Главный экран", backArrow = null, items = listOf()) },
    ) { innerPadding ->
        if (userData.companyId != "") {
            HomeScreen(
                padding = innerPadding,
                navigationToLastViewTask = navigationToLastViewTask,
                userViewModel = userViewModel,
                tasksViewModel = tasksViewModel,
            )
        } else {
            NoAccessScreen(padding = innerPadding)
        }
    }
}
