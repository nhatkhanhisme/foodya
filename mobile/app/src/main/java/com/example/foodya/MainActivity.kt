package com.example.foodya

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.foodya.data.local.AuthEventManager
import com.example.foodya.ui.MainScreen
import com.example.foodya.ui.MainViewModel
import com.example.foodya.ui.navigation.Graph
import com.example.foodya.ui.theme.FoodyaTheme
import com.example.foodya.ui.theme.ThemeViewModel
import com.example.foodya.ui.theme.rememberThemePreference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private val themeViewModel: ThemeViewModel by viewModels()

    @Inject
    lateinit var authEventManager: AuthEventManager

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            mainViewModel.isLoading.value
        }

        enableEdgeToEdge()
        setContent {
            // Observe theme preference
            val themePreference by themeViewModel.themePreference.collectAsState()
            val systemInDarkTheme = isSystemInDarkTheme()
            val isDarkTheme = rememberThemePreference(themePreference, systemInDarkTheme)

            val navController = rememberNavController()

            // Observe global logout event
            LaunchedEffect(Unit) {
                authEventManager.logoutEvent.collect {
                    navController.navigate(Graph.AUTH) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }

            FoodyaTheme(darkTheme = isDarkTheme) {
                val startDestination by mainViewModel.startDestination.collectAsState()
                val userRole by mainViewModel.currentUserRole.collectAsState()
                val isLoading by mainViewModel.isLoading.collectAsState()

                if (!isLoading) {
                    // Key ensures NavController recreates when role changes
                    androidx.compose.runtime.key(userRole, startDestination) {
                        MainScreen(
                            navController = navController,
                            userRole = userRole,
                            startDestination = startDestination,
                            viewModel = mainViewModel
                        )
                    }
                }
            }
        }
    }
}