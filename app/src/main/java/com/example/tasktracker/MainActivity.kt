package com.example.tasktracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.tasktracker.navigation.graphs.RootNavigationGraph
import com.example.tasktracker.services.SplashScreenService
import com.example.tasktracker.ui.theme.TaskTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val splashScreenService: SplashScreenService by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    installSplashScreen().setKeepOnScreenCondition {
                        splashScreenService.isLoading.value
                    }

                    val startDestination =
                        splashScreenService.startDestination.collectAsState().value

                    RootNavigationGraph(
                        navController = rememberNavController(),
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}
