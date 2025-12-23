package com.example.foodya.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

object BottomNavItems {
    // Menu cho Khách Hàng
    val CustomerItems = listOf(
        BottomNavItem("Home", Screen.CustomerHome.route, Icons.Filled.Home),
        BottomNavItem("Cart", Screen.Cart.route, Icons.Filled.ShoppingCart),
        BottomNavItem("Account", Screen.CustomerProfile.route , Icons.Filled.Person) // Giả sử có màn này
    )

    // Menu cho Chủ Quán
    val MerchantItems = listOf(
        BottomNavItem("Dashboard", Screen.MerchantDashboard.route, Icons.Filled.Home),
        BottomNavItem("Menu", Screen.ManageMenu.route, Icons.AutoMirrored.Filled.List),
        BottomNavItem("Account", Screen.MerchantProfile.route, Icons.Filled.Person)
    )
}