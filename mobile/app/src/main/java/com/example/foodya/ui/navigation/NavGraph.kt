package com.example.foodya.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.foodya.data.model.UserRole
import com.example.foodya.ui.MainViewModel
import com.example.foodya.ui.screen.auth.AuthView
import com.example.foodya.ui.screen.customer.cart.CartView
import com.example.foodya.ui.screen.merchant.dashboard.DashboardView
import com.example.foodya.ui.screen.customer.home.HomeView
import com.example.foodya.ui.screen.customer.profile.CustomerProfileView
import com.example.foodya.ui.screen.merchant.menu.MenuView
import com.example.foodya.ui.screen.merchant.profile.MerchantProfileView

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = Graph.AUTH
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
                HomeView(navController)
            }
            composable(route = Screen.Cart.route) {
                CartView(navController)
            }
            composable(route = Screen.CustomerProfile.route) {
                CustomerProfileView(navController)
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