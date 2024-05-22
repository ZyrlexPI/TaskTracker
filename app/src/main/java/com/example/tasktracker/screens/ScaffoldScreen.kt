package com.example.tasktracker.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.tasktracker.navigation.graphs.HomeScreenNavGraph
import com.example.tasktracker.navigation.models.BottomBar_Graph
import com.ravenzip.workshop.components.BottomNavigationBar
import com.ravenzip.workshop.data.BottomNavigationItem
import com.ravenzip.workshop.data.IconParameters

@Composable
fun ScaffoldScreen(navController: NavHostController = rememberNavController()) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                buttonsList = generateMenuItems(),
                showLabelOnlyOnSelected = true
            )
        }
    ) {
        HomeScreenNavGraph(
            navController = navController,
            padding = it,
        )
    }
}

@Composable
private fun generateMenuItems(): List<BottomNavigationItem> {
    val homeButton =
        BottomNavigationItem(
            label = "Главная",
            route = BottomBar_Graph.HOME,
            icon = IconParameters(value = Icons.Outlined.Home),
            hasNews = false
        )

    val tasksButton =
        BottomNavigationItem(
            label = "Задачи",
            route = BottomBar_Graph.TASKS,
            icon = IconParameters(value = Icons.Outlined.TaskAlt),
            hasNews = false
        )

    val notificationsButton =
        BottomNavigationItem(
            label = "Уведомления",
            route = BottomBar_Graph.NOTIFICATIONS,
            icon = IconParameters(value = Icons.Outlined.Notifications),
            hasNews = false
        )

    val userProfileButton =
        BottomNavigationItem(
            label = "Профиль",
            route = BottomBar_Graph.USER_PROFILE,
            icon = IconParameters(value = Icons.Outlined.Person),
            hasNews = false
        )

    return listOf(homeButton, tasksButton, notificationsButton, userProfileButton)
}
