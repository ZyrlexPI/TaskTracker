package com.example.tasktracker.screens.userProfile.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.ravenzip.workshop.components.TopAppBar

@Composable
fun SettingsScreenScaffold(
    padding: PaddingValues,
    prefs: DataStore<Preferences>,
) {

    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = { TopAppBar(title = "Настройки", backArrow = null, items = listOf()) },
    ) { innerPadding ->
        SettingsScreen(padding = innerPadding, prefs = prefs)
    }
}
