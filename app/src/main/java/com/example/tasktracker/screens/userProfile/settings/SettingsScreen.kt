package com.example.tasktracker.screens.userProfile.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.ravenzip.workshop.components.Switch
import com.ravenzip.workshop.data.TextConfig
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    padding: PaddingValues,
    prefs: DataStore<Preferences>,
) {
    val scope = rememberCoroutineScope()

    val darkTheme by
        prefs.data
            .map {
                val counterKey = booleanPreferencesKey("darkTheme")
                it[counterKey] ?: false
            }
            .collectAsState(false)
    val darkThemeSwitch = remember(darkTheme) { mutableStateOf(darkTheme) }
    Column(
        modifier = Modifier.fillMaxSize().padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Switch(
            isChecked = darkThemeSwitch,
            title = "Темная тема",
            titleConfig = TextConfig.Normal,
            text = "Включение/выключение\nтемной темы",
            textConfig = TextConfig.Small,
            onCheckedChanged = {
                scope.launch {
                    prefs.edit { dataStore ->
                        val counterKey = booleanPreferencesKey("darkTheme")
                        dataStore[counterKey] = !darkTheme
                    }
                }
            },
        )
    }
}
