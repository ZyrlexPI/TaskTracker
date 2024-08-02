package com.example.tasktracker.screens.userProfile.security

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Key
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ravenzip.workshop.components.RowIconButton
import com.ravenzip.workshop.data.IconParameters
import com.ravenzip.workshop.data.TextParameters

@Composable
fun SecurityScreen(padding: PaddingValues, vararg onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        RowIconButton(
            text = TextParameters("Смена пароля", 19),
            icon = IconParameters(Icons.Outlined.Key)
        ) {
            onClick[0]()
        }
        Spacer(modifier = Modifier.height(20.dp))
        //        InfoCard(
        //            icon = IconParameters(value = Icons.Outlined.Warning),
        //            title = TextParameters(value = "ВАЖНО", size = 20),
        //            text = TextParameters(value = "Данный экран находится в разработке.", size =
        // 20),
        //            isTitleUnderIcon = false
        //        )
    }
}
