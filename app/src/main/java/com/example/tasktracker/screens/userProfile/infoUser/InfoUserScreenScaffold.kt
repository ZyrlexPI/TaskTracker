package com.example.tasktracker.screens.userProfile.infoUser

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tasktracker.viewModels.InfoUserViewModel
import com.ravenzip.workshop.components.TopAppBar

@Composable
fun InfoUserScreenScaffold(
    padding: PaddingValues,
    infoUserViewModel: InfoUserViewModel = hiltViewModel<InfoUserViewModel>(),
) {

    val dataUser = infoUserViewModel.dataCurrentUser.collectAsState().value

    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = { TopAppBar(title = "Информация", backArrow = null, items = listOf()) },
    ) { innerPadding ->
        InfoUserScreen(padding = innerPadding, infoUserViewModel = infoUserViewModel)
    }
}
