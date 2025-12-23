package com.example.foodya.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.foodya.ui.components.CustomBottomBar
import com.example.foodya.ui.navigation.Screen
import com.example.foodya.ui.navigation.SetupNavGraph

@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val userRole by viewModel.userRole.collectAsState()

    val showBottomBar = currentRoute in listOf(
        Screen.CustomerHome.route,
        Screen.Cart.route,
        Screen.CustomerProfile.route,
        Screen.MerchantDashboard.route,
        Screen.ManageMenu.route,
        Screen.MerchantProfile.route
    )

    Scaffold(
        bottomBar = {
            // Chỉ hiện nếu showBottomBar = true VÀ đã có Role
            if (showBottomBar && userRole != null) {
                CustomBottomBar(
                    navController = navController,
                    userRole = userRole.toString()
                )
            }
        }
    ) { innerPadding ->
        // Truyền innerPadding vào NavGraph để nội dung không bị BottomBar che khuất
        Box(modifier = Modifier.padding(innerPadding)) {
            SetupNavGraph(
                navController = navController,
                mainViewModel = viewModel
            )
        }
    }
}