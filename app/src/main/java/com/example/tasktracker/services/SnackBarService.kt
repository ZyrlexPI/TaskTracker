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
import com.ravenzip.workshop.data.IconParameters
import com.ravenzip.workshop.data.SnackBarVisualsExtended

suspend fun SnackbarHostState.showMessage(message: String) {
    this.showSnackbar(
        SnackBarVisualsExtended(
            message = message,
            icon = IconParameters(value = Icons.Outlined.Info)
        )
    )
}

suspend fun SnackbarHostState.showSuccess(message: String) {
    this.showSnackbar(
        SnackBarVisualsExtended(
            message = message,
            icon = IconParameters(value = Icons.Outlined.Done, color = successColor)
        )
    )
}

suspend fun SnackbarHostState.showWarning(message: String) {
    this.showSnackbar(
        SnackBarVisualsExtended(
            message = message,
            icon = IconParameters(value = Icons.Outlined.Warning, color = warningColor)
        )
    )
}

suspend fun SnackbarHostState.showError(message: String) {
    this.showSnackbar(
        SnackBarVisualsExtended(
            message = message,
            icon = IconParameters(value = Icons.Outlined.Error, color = errorColor)
        )
    )
}
