package com.example.tasktracker.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Task
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.tasktracker.navigation.graphs.MainNavGraph
import com.example.tasktracker.navigation.models.BottomBarGraph
import com.example.tasktracker.viewModels.MainScaffoldViewModel
import com.ravenzip.workshop.components.BottomNavigationBar
import com.ravenzip.workshop.data.appbar.BottomNavigationItem
import com.ravenzip.workshop.data.icon.Icon
import com.ravenzip.workshop.data.icon.IconConfig

@Composable
fun ScaffoldScreen(
    navController: NavHostController = rememberNavController(),
    mainScaffoldViewModel: MainScaffoldViewModel = hiltViewModel(),
    prefs: DataStore<Preferences>,
    returnInAuth: () -> Unit
) {

    val countNotifications =
        mainScaffoldViewModel.countNotifications.collectAsStateWithLifecycle().value

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                buttonsList = generateMenuItems(countNotifications),
                showLabelOnlyOnSelected = false
            )
        },
    ) {
        MainNavGraph(
            navController = navController,
            padding = it,
            prefs = prefs,
            returnInAuth = returnInAuth,
        )
    }
}

@Composable
private fun generateMenuItems(countNotifications: Int): List<BottomNavigationItem> {
    val homeButton =
        BottomNavigationItem(
            label = "Главная",
            route = BottomBarGraph.HOME,
            icon = Icon.ImageVectorIcon(Icons.Outlined.Home),
            iconConfig = IconConfig.Primary,
            hasNews = false,
        )

    val tasksButton =
        BottomNavigationItem(
            label = "Задачи",
            route = BottomBarGraph.TASKS,
            icon = Icon.ImageVectorIcon(Icons.Outlined.Task),
            iconConfig = IconConfig.Primary,
            hasNews = false,
        )

    val notificationsButton =
        BottomNavigationItem(
            label = "Уведомления",
            route = BottomBarGraph.NOTIFICATIONS,
            icon = Icon.ImageVectorIcon(Icons.Outlined.Notifications),
            iconConfig = IconConfig.Primary,
            hasNews = false,
            badgeCount = if (countNotifications > 0) countNotifications else null
        )

    val userProfileButton =
        BottomNavigationItem(
            label = "Профиль",
            route = BottomBarGraph.USER_PROFILE,
            icon = Icon.ImageVectorIcon(Icons.Outlined.AccountCircle),
            iconConfig = IconConfig.Primary,
            hasNews = false
        )

    return listOf(homeButton, tasksButton, notificationsButton, userProfileButton)
}
