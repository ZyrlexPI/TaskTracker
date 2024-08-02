package com.example.tasktracker.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ravenzip.workshop.components.InfoCard
import com.ravenzip.workshop.data.IconParameters
import com.ravenzip.workshop.data.TextParameters

@Composable
fun TasksScreen(padding: PaddingValues) {
    Column(
        modifier = Modifier.fillMaxSize().padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        InfoCard(
            icon = IconParameters(value = Icons.Outlined.Warning),
            title = TextParameters(value = "ВАЖНО", size = 20),
            text = TextParameters(value = "Данный экран находится в разработке.", size = 20),
            isTitleUnderIcon = false
        )
    }
}
