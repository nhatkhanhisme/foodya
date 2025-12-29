package com.example.foodya.ui.navigation

object Graph {
    const val ROOT = "root_graph"
    const val AUTH = "auth_graph"
    const val CUSTOMER = "customer_graph"
    const val MERCHANT = "merchant_graph"
}

sealed class Screen(val route: String){
    // Auth Screens
    data object Login : Screen("login_screen")
    data object Register : Screen("register_screen")

    // Customer Screens
    data object CustomerHome : Screen("customer_home")
    data object Order : Screen("order_screen")
    data object CustomerProfile : Screen("customer_profile")
    data object SearchResults : Screen("search_results/{query}") {
        fun createRoute(query: String) = "search_results/$query"
    }
    data object RestaurantDetail : Screen("restaurant_detail/{restaurantId}") {
        fun createRoute(restaurantId: String) = "restaurant_detail/$restaurantId"
    }
    data object Checkout : Screen("checkout")

    // Merchant Screens
    data object MerchantDashboard : Screen("merchant_dashboard")
    data object ManageMenu : Screen("manage_menu")
    data object MerchantProfile : Screen("merchant_profile")

    // Security Screens (Shared)
    data object ChangePassword : Screen("change_password")
}


