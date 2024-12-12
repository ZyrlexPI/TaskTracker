package com.example.tasktracker.screens.userProfile.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.ravenzip.workshop.components.RowIconButton
import com.ravenzip.workshop.components.Switch
import com.ravenzip.workshop.data.TextConfig
import com.ravenzip.workshop.data.icon.Icon
import com.ravenzip.workshop.data.icon.IconConfig
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    padding: PaddingValues,
    prefs: DataStore<Preferences>,
    navigateToChangePassword: () -> Unit,
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
        TextWithLine("Настройки безопасности")
        Spacer(modifier = Modifier.height(10.dp))
        RowIconButton(
            text = "Смена пароля",
            textConfig = TextConfig(size = 19.sp),
            icon = Icon.ImageVectorIcon(Icons.Outlined.Key),
            iconConfig = IconConfig.Default,
        ) {
            navigateToChangePassword()
        }
        Spacer(modifier = Modifier.height(20.dp))
        TextWithLine("Настройки приложения")
        Spacer(modifier = Modifier.height(10.dp))
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

@Composable
fun TextWithLine(text: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(start = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Текст
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )

        // Линия
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(0.95f), // Ширина линии 95%
            thickness = 1.dp, // Толщина линии
            color = MaterialTheme.colorScheme.onPrimaryContainer // Цвет линии
        )
    }
}
