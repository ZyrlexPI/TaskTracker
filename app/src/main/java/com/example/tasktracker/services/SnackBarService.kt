package com.example.tasktracker.services

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.SnackbarHostState
import com.example.tasktracker.ui.theme.errorColor
import com.example.tasktracker.ui.theme.successColor
import com.example.tasktracker.ui.theme.warningColor
import com.ravenzip.workshop.data.SnackBarVisualsConfig
import com.ravenzip.workshop.data.icon.IconConfig

suspend fun SnackbarHostState.showMessage(message: String) {
    this.showSnackbar(
        SnackBarVisualsConfig(
            message = message,
            icon = Icons.Outlined.Info,
            iconConfig = IconConfig.Default,
        )
    )
}

suspend fun SnackbarHostState.showSuccess(message: String) {
    this.showSnackbar(
        SnackBarVisualsConfig(
            message = message,
            icon = Icons.Outlined.Done,
            iconConfig = IconConfig(color = successColor)
        )
    )
}

suspend fun SnackbarHostState.showWarning(message: String) {
    this.showSnackbar(
        SnackBarVisualsConfig(
            message = message,
            icon = Icons.Outlined.Warning,
            iconConfig = IconConfig(color = warningColor),
        )
    )
}

suspend fun SnackbarHostState.showError(message: String) {
    this.showSnackbar(
        SnackBarVisualsConfig(
            message = message,
            icon = Icons.Outlined.Error,
            iconConfig = IconConfig(color = errorColor),
        )
    )
}
