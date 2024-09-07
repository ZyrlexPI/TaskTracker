package com.example.tasktracker.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Task
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tasktracker.navigation.graphs.HomeScreenNavGraph
import com.example.tasktracker.navigation.models.BottomBar_Graph
import com.example.tasktracker.navigation.models.UserProfileGraph
import com.ravenzip.workshop.components.BottomNavigationBar
import com.ravenzip.workshop.components.SnackBar
import com.ravenzip.workshop.components.TopAppBar
import com.ravenzip.workshop.data.appbar.BottomNavigationItem
import com.ravenzip.workshop.data.icon.Icon
import com.ravenzip.workshop.data.icon.IconConfig

@Composable
fun ScaffoldScreen(
    navController: NavHostController = rememberNavController(),
    returnInAuth: () -> Unit
) {
    val titleTopAppBar = remember { mutableStateOf("") }
    val snackBarHostState = remember { SnackbarHostState() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    ChangeTopAppBarState(
        currentRoute = navBackStackEntry?.destination?.route,
        titleTopAppBar = titleTopAppBar
    )

    Scaffold(
        topBar = { TopAppBar(titleTopAppBar.value) },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                buttonsList = generateMenuItems(),
                showLabelOnlyOnSelected = false
            )
        },
    ) {
        HomeScreenNavGraph(
            navController = navController,
            padding = it,
            snackBarHostState = snackBarHostState,
            returnInAuth = returnInAuth,
        )
    }
    SnackBar(snackBarHostState)
}

@Composable
private fun generateMenuItems(): List<BottomNavigationItem> {
    val homeButton =
        BottomNavigationItem(
            label = "Главная",
            route = BottomBar_Graph.HOME,
            icon = Icon.ImageVectorIcon(Icons.Outlined.Home),
            iconConfig = IconConfig.Primary,
            hasNews = false,
        )

    val tasksButton =
        BottomNavigationItem(
            label = "Задачи",
            route = BottomBar_Graph.TASKS,
            icon = Icon.ImageVectorIcon(Icons.Outlined.Task),
            iconConfig = IconConfig.Primary,
            hasNews = false,
        )

    val notificationsButton =
        BottomNavigationItem(
            label = "Уведомления",
            route = BottomBar_Graph.NOTIFICATIONS,
            icon = Icon.ImageVectorIcon(Icons.Outlined.Notifications),
            iconConfig = IconConfig.Primary,
            hasNews = false
        )

    val userProfileButton =
        BottomNavigationItem(
            label = "Профиль",
            route = BottomBar_Graph.USER_PROFILE,
            icon = Icon.ImageVectorIcon(Icons.Outlined.AccountCircle),
            iconConfig = IconConfig.Primary,
            hasNews = false
        )

    return listOf(homeButton, tasksButton, notificationsButton, userProfileButton)
}

@Composable
private fun ChangeTopAppBarState(currentRoute: String?, titleTopAppBar: MutableState<String>) {
    when (currentRoute) {
        BottomBar_Graph.HOME -> titleTopAppBar.value = "Главный экран"
        BottomBar_Graph.TASKS -> titleTopAppBar.value = "Задачи"
        BottomBar_Graph.NOTIFICATIONS -> titleTopAppBar.value = "Уведомления"
        BottomBar_Graph.USER_PROFILE -> titleTopAppBar.value = "Профиль"
        UserProfileGraph.USER_DATA -> titleTopAppBar.value = "Личные данные"
        UserProfileGraph.SECURITY -> titleTopAppBar.value = "Безопасность аккаунта"
        UserProfileGraph.SETTINGS -> titleTopAppBar.value = "Настройки"
        UserProfileGraph.CHANGE_PASSWORD -> titleTopAppBar.value = "Смена пароля"
        UserProfileGraph.COMPANY -> titleTopAppBar.value = "Организация"
        UserProfileGraph.COMPANY_JOIN -> titleTopAppBar.value = "Присоединиться к организации"
        UserProfileGraph.COMPANY_ADD -> titleTopAppBar.value = "Добавить организацию"
    }
}
