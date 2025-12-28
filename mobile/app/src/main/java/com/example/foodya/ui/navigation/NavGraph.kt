package com.example.foodya.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.foodya.data.model.UserRole
import com.example.foodya.ui.MainViewModel
import com.example.foodya.ui.screen.auth.AuthView
import com.example.foodya.ui.screen.merchant.dashboard.DashboardView
import com.example.foodya.ui.screen.customer.home.HomeView
import com.example.foodya.ui.screen.customer.order.OrderHistoryView
import com.example.foodya.ui.screen.customer.profile.CustomerProfileView
import com.example.foodya.ui.screen.customer.restaurant.RestaurantDetailView
import com.example.foodya.ui.screen.merchant.menu.MenuView
import com.example.foodya.ui.screen.merchant.profile.MerchantProfileView
import com.example.foodya.ui.screen.security.ChangePasswordView

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

                        // Clear entire back stack and navigate to the target graph
                        navController.navigate(targetGraph) {
                            popUpTo(Graph.ROOT) {
                                inclusive = false
                                saveState = false
                            }
                            launchSingleTop = true
                            restoreState = false
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
                        navController.navigate(Screen.SearchResults.createRoute(query))
                    },
                    onRestaurantClick = { restaurantId ->
                        navController.navigate(Screen.RestaurantDetail.createRoute(restaurantId))
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
                    onNavigateToChangePassword = { 
                        navController.navigate(Screen.ChangePassword.route) 
                    },
                    onNavigateToTerms = { navController.navigate("terms_screen") }
                )
            }

            composable(route = Screen.ChangePassword.route) {
                ChangePasswordView(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Screen.RestaurantDetail.route, // Ví dụ: "restaurant_detail/{restaurantId}"
                arguments = listOf(
                    navArgument("restaurantId") {
                        type = NavType.StringType // Khớp với ID là String bạn đã sửa
                    }
                )
            ) { backStackEntry ->
                // Lấy ID từ argument (Dù ViewModel đã tự lấy, nhưng View vẫn cần tham số này)
                val restaurantId = backStackEntry.arguments?.getString("restaurantId") ?: ""

                RestaurantDetailView(
                    restaurantId = restaurantId,
                    onBackClick = {
                        navController.popBackStack() // Quay lại trang trước (Home hoặc Search)
                    },
                    onGoToCheckout = {
                        navController.navigate(Screen.Checkout.route)
                    }
                )
            }
            // --- Màn hình Kết quả tìm kiếm (Cũng cần thêm nếu chưa có) ---
            composable(
                route = Screen.SearchResults.route, // Ví dụ: "search_results/{query}"
                arguments = listOf(navArgument("query") { type = NavType.StringType })
            ) { backStackEntry ->
                val query = backStackEntry.arguments?.getString("query") ?: ""
                // SearchResultsView(query = query, ...)
            }
        }

        // MERCHANT graph
        navigation(
            startDestination = Screen.MerchantDashboard.route,
            route = Graph.MERCHANT
        ) {
            composable(route = Screen.MerchantDashboard.route) {
                DashboardView(
                    onNavigateToRegisterRestaurant = {
                        // TODO: Navigate to register restaurant screen
                    }
                )
            }
            composable(route = Screen.ManageMenu.route) {
                MenuView()
            }
            composable(route = Screen.MerchantProfile.route) {
                MerchantProfileView(
                    onNavigateToLogin = {
                        navController.navigate(Graph.AUTH) {
                            popUpTo(Graph.ROOT) { inclusive = true }
                        }
                    },
                    onNavigateToChangePassword = { 
                        navController.navigate(Screen.ChangePassword.route) 
                    },
                    onNavigateToTerms = { navController.navigate("terms_screen") }
                )
            }

            composable(route = Screen.ChangePassword.route) {
                ChangePasswordView(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            }
        }
    }
