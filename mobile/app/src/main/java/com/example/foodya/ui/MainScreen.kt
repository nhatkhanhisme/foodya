package com.example.foodya.ui

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.foodya.data.model.UserRole
import com.example.foodya.ui.navigation.BottomNavItems
import com.example.foodya.ui.navigation.SetupNavGraph

@Composable
fun MainScreen(
    navController: NavHostController,
    startDestination: String,
    userRole: UserRole?,
    viewModel: MainViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val shouldShowBottomBar = userRole != null && (
            BottomNavItems.CustomerItems.any { it.route == currentDestination?.route } ||
                    BottomNavItems.MerchantItems.any { it.route == currentDestination?.route }
            )
    Log.d("UserRole", userRole.toString());

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                // Chọn danh sách menu dựa trên Role
                val items = if (userRole == UserRole.MERCHANT) {
                    BottomNavItems.MerchantItems
                } else {
                    BottomNavItems.CustomerItems
                }

                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    items.forEach { item ->
                        val selected =
                            currentDestination?.hierarchy?.any { it.route == item.route } == true

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(text = item.title) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    // Pop về màn hình start của graph để tránh chồng chất stack
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Tránh mở lại cùng 1 màn hình khi đang đứng ở đó
                                    launchSingleTop = true
                                    // Khôi phục trạng thái khi quay lại
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // Truyền innerPadding vào NavGraph để nội dung không bị BottomBar che khuất
        Box(modifier = Modifier.padding(innerPadding)) {
            SetupNavGraph(
                navController = navController,
                mainViewModel = viewModel,
                startDestination = startDestination
            )
        }
    }
}