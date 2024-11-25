package com.example.tasktracker.screens.main.tasks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ControlPoint
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.PublishedWithChanges
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ravenzip.workshop.components.RowIconButton
import com.ravenzip.workshop.data.TextConfig
import com.ravenzip.workshop.data.icon.Icon
import com.ravenzip.workshop.data.icon.IconConfig

@Composable
fun TasksScreen(padding: PaddingValues, vararg onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RowIconButton(
            text = "Новые",
            textConfig = TextConfig(size = 19.sp),
            icon = Icon.ImageVectorIcon(Icons.Filled.ControlPoint),
            iconConfig = IconConfig.Default,
        ) {
            onClick[0]()
        }
        Spacer(modifier = Modifier.height(20.dp))
        RowIconButton(
            text = "В процессе",
            textConfig = TextConfig(size = 19.sp),
            icon = Icon.ImageVectorIcon(Icons.Outlined.PublishedWithChanges),
            iconConfig = IconConfig.Default,
        ) {
            onClick[1]()
        }
        Spacer(modifier = Modifier.height(20.dp))
        RowIconButton(
            text = "Завершенные",
            textConfig = TextConfig(size = 19.sp),
            icon = Icon.ImageVectorIcon(Icons.Outlined.Done),
            iconConfig = IconConfig.Default,
        ) {
            onClick[2]()
        }
    }
}
