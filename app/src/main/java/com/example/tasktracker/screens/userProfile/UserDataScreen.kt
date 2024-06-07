package com.example.tasktracker.screens.userProfile

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasktracker.services.firebase.UserService
import com.example.tasktracker.services.firebase.getUser
import com.google.firebase.database.getValue
import com.ravenzip.workshop.components.SimpleButton
import com.ravenzip.workshop.components.SinglenessTextField
import com.ravenzip.workshop.data.TextParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun UserDataScreen(padding: PaddingValues) {
    val userService = remember { mutableStateOf(UserService()) }
    val isLoadingUser = remember { mutableStateOf(true) }
    val currentUser = userService.value.dataUser.collectAsState().value
    val name = remember { mutableStateOf("") }
    name.value = currentUser.name
    val surname = remember { mutableStateOf(currentUser.surname) }
    Log.d("SUrName", surname.value + " " + currentUser.surname)
    LaunchedEffect(isLoadingUser.value) {
        if (isLoadingUser.value) {
            userService.value.get(getUser())
            isLoadingUser.value = false
        }
    }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Личные данные",
            modifier = Modifier.fillMaxWidth(0.9f),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(20.dp))
        SinglenessTextField(text = name, label = "Имя")
        Spacer(modifier = Modifier.height(20.dp))
        SinglenessTextField(text = surname, label = "Фамилия")
        Spacer(modifier = Modifier.height(40.dp))
        SimpleButton(
            text = TextParameters(value = "Сохранить", size = 19),
        ) {
            scope.launch(Dispatchers.Main) {
                userService.value.update(currentUser = getUser(), name.value, surname.value)
            }
        }
    }
}
