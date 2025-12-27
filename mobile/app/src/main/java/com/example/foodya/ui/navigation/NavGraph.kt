package com.example.foodya.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.foodya.data.model.UserRole
import com.example.foodya.ui.MainViewModel
import com.example.foodya.ui.screen.auth.AuthView
import com.example.foodya.ui.screen.merchant.dashboard.DashboardView
import com.example.foodya.ui.screen.customer.home.HomeView
import com.example.foodya.ui.screen.customer.order.OrderHistoryView
import com.example.foodya.ui.screen.customer.profile.CustomerProfileView
import com.example.foodya.ui.screen.merchant.menu.MenuView
import com.example.foodya.ui.screen.merchant.profile.MerchantProfileView

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    startDestination: String
) {
    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = startDestination
    ) {
        // AUTH graph
        navigation(
            startDestination = Screen.Login.route,
            route = Graph.AUTH
        ) {
            composable(route = Screen.Login.route) {
                AuthView(
                    onAuthSuccess = { role ->
                        val targetGraph = when (role) {
                            UserRole.CUSTOMER -> Graph.CUSTOMER
                            UserRole.MERCHANT -> Graph.MERCHANT
                            else -> Graph.CUSTOMER
                        }

                        navController.navigate(targetGraph) {
                            popUpTo(Graph.AUTH) { inclusive = true } // Chặn back về lại bằng nút khi login thành công
                        }
                    }
                )
            }
        }

        // CUSTOMER graph
        navigation(
            startDestination = Screen.CustomerHome.route,
            route = Graph.CUSTOMER
        ) {
            composable(route = Screen.CustomerHome.route) {
                HomeView(
                    onNavigateToSearchResult = { query ->
                        navController.navigate("search_result_screen/$query")
                    },
                    onRestaurantClick = { id ->
                        navController.navigate("restaurant_detail/$id")
                    },
                    onQuickOrderClick = { foodId ->
                        navController.navigate("order_screen/$foodId")
                    }
                )
            }
            composable(route = Screen.Order.route) {
                OrderHistoryView(
                    onOrderClick = { orderId ->
                        navController.navigate("order_detail/$orderId")
                    }
                )
            }
            composable(route = Screen.CustomerProfile.route) {
                CustomerProfileView(
                    onNavigateToLogin = {
                        navController.navigate(Graph.AUTH) {
                            popUpTo(Graph.ROOT) { inclusive = true }
                        }
                    },
                    onNavigateToChangePassword = { navController.navigate("change_password_screen") },
                    onNavigateToTerms = { navController.navigate("terms_screen") }
                )
            }
        }

        // MERCHANT graph
        navigation(
            startDestination = Screen.MerchantDashboard.route,
            route = Graph.MERCHANT
        ) {
            composable(route = Screen.MerchantDashboard.route) {
                DashboardView(navController)
            }
            composable(route = Screen.ManageMenu.route) {
                MenuView(navController)
            }
            composable(route = Screen.MerchantProfile.route) {
                MerchantProfileView(navController)
            }
        }
    }
}