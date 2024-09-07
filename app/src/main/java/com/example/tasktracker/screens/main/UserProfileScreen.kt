package com.example.tasktracker.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasktracker.services.firebase.getUser
import com.example.tasktracker.services.firebase.logout
import com.ravenzip.workshop.components.RowIconButton
import com.ravenzip.workshop.data.TextConfig
import com.ravenzip.workshop.data.icon.Icon
import com.ravenzip.workshop.data.icon.IconConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun UserProfileScreen(
    padding: PaddingValues,
    vararg onClick: () -> Unit,
) {
    val emailUser = getUser()?.email.toString()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Icon(
                Icons.Outlined.AccountCircle,
                contentDescription = "",
                modifier = Modifier.size(50.dp)
            )
            Text(
                text =
                    if (emailUser == "null") {
                        "Выполняется выход..."
                    } else {
                        emailUser
                    },
                // modifier = Modifier.padding(start = 17.dp),
                fontSize = 20.sp,
            )
            IconButton(
                onClick = { onClick[3]() },
                modifier = Modifier.size(50.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Apartment,
                    contentDescription = "",
                    modifier = Modifier.size(30.dp),
                )
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
        RowIconButton(
            text = "Личные данные",
            textConfig = TextConfig(size = 19.sp),
            icon = Icon.ImageVectorIcon(Icons.Filled.Person),
            iconConfig = IconConfig.Default,
        ) {
            onClick[0]()
        }
        Spacer(modifier = Modifier.height(20.dp))
        RowIconButton(
            text = "Безопасность",
            textConfig = TextConfig(size = 19.sp),
            icon = Icon.ImageVectorIcon(Icons.Outlined.Lock),
            iconConfig = IconConfig.Default,
        ) {
            onClick[1]()
        }
        Spacer(modifier = Modifier.height(20.dp))
        RowIconButton(
            text = "Настройки",
            textConfig = TextConfig(size = 19.sp),
            icon = Icon.ImageVectorIcon(Icons.Outlined.Settings),
            iconConfig = IconConfig.Default,
        ) {
            onClick[2]()
        }
        Spacer(modifier = Modifier.height(20.dp))
        RowIconButton(
            text = "Выход",
            textConfig = TextConfig(size = 19.sp),
            icon = Icon.ImageVectorIcon(Icons.AutoMirrored.TwoTone.Logout),
            iconConfig = IconConfig.Default,
        ) {
            scope.launch(Dispatchers.Main) {
                logout()
                onClick[4]()
            }
        }
    }
}
