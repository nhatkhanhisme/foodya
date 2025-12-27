package com.example.foodya

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.foodya.ui.MainScreen
import com.example.foodya.ui.MainViewModel
import com.example.foodya.ui.theme.FoodyaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            mainViewModel.isLoading.value
        }

        enableEdgeToEdge()
        setContent {
            FoodyaTheme {
                val navController = rememberNavController()
                val startDestination by mainViewModel.startDestination.collectAsState()
                val userRole by mainViewModel.currentUserRole.collectAsState()

                if (!mainViewModel.isLoading.collectAsState().value) {
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