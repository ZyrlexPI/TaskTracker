package com.example.tasktracker.screens.main

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasktracker.data.User
import com.example.tasktracker.services.firebase.CompanyService
import com.example.tasktracker.services.firebase.UserService
import com.example.tasktracker.services.firebase.getUser
import com.example.tasktracker.services.firebase.logout
import com.ravenzip.workshop.components.RowIconButton
import com.ravenzip.workshop.components.Spinner
import com.ravenzip.workshop.data.IconParameters
import com.ravenzip.workshop.data.TextParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun UserProfileScreen(
    padding: PaddingValues,
    vararg onClick: () -> Unit,
    companyService: MutableState<CompanyService>,
    userService: MutableState<UserService>
) {
    val emailUser = getUser()?.email.toString()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isLoading = remember { mutableStateOf(false) }
    val spinnerText = remember { mutableStateOf("Выполняется выход...") }

    val userData = remember { mutableStateOf(User()) }
    userData.value = userService.value.dataUser.collectAsState().value
    val isLoadingUser = remember { mutableStateOf(true) }
    val isLoadingCompany = remember { mutableStateOf(false) }
    LaunchedEffect(isLoadingUser.value) {
        if (isLoadingUser.value) {
            userService.value.get(getUser())
            isLoadingCompany.value = true
            isLoadingUser.value = false
        }
    }
    LaunchedEffect(isLoadingCompany.value) {
        if (isLoadingCompany.value) {
            companyService.value.getCurrentCompany(userData.value)
            isLoadingCompany.value = false
        }
    }

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
            text =
                TextParameters(
                    "Личные данные",
                    19,
                ),
            icon = IconParameters(Icons.Filled.Person)
        ) {
            onClick[0]()
        }
        Spacer(modifier = Modifier.height(20.dp))
        RowIconButton(
            text =
                TextParameters(
                    "Безопасность",
                    19,
                ),
            icon = IconParameters(Icons.Outlined.Lock)
        ) {
            onClick[1]()
        }
        Spacer(modifier = Modifier.height(20.dp))
        RowIconButton(
            text =
                TextParameters(
                    "Настройки",
                    19,
                ),
            icon = IconParameters(Icons.Outlined.Settings)
        ) {
            onClick[2]()
        }
        Spacer(modifier = Modifier.height(20.dp))
        RowIconButton(
            text =
                TextParameters(
                    "Выход",
                    19,
                ),
            icon = IconParameters(Icons.AutoMirrored.TwoTone.Logout)
        ) {
            scope.launch(Dispatchers.Main) {
                val packageManager: PackageManager = context.packageManager
                val intent: Intent? = packageManager.getLaunchIntentForPackage(context.packageName)
                val componentName: ComponentName? = intent?.component
                val mainIntent: Intent = Intent.makeRestartActivityTask(componentName)
                logout()
                isLoading.value = true
                var timer = 3
                while (timer != 0) {
                    delay(1000)
                    timer -= 1
                }
                isLoading.value = false
                context.startActivity(mainIntent)
                Runtime.getRuntime().exit(0)
            }
        }
    }
    if (isLoading.value) {
        Spinner(text = TextParameters(value = spinnerText.value, size = 16))
    }
}
