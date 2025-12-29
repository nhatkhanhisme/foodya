package com.example.foodya.ui.screen.customer.home

import com.example.foodya.domain.model.CartItem
import com.example.foodya.domain.model.Food
import com.example.foodya.domain.model.Restaurant

data class HomeState(
    // Trạng thái tải trang
    val isLoading: Boolean = true,

    // Dữ liệu chính
    val nearbyRestaurants: List<Restaurant> = emptyList(),
    val popularFoods: List<Food> = emptyList(),

    // Trạng thái tìm kiếm
    val searchQuery: String = "",
    val isSearchActive: Boolean = false, // Thanh tìm kiếm đang mở hay đóng
    val searchSuggestions: List<String> = emptyList(), // Gợi ý khi gõ

    val selectedFood: Food? = null,

    // Cart & Checkout
    val cartItems: List<CartItem> = emptyList(),
    val selectedRestaurantId: String? = null,
    val selectedRestaurantName: String? = null,
    val showCheckoutDialog: Boolean = false,
    val deliveryAddress: String = "",
    val orderNotes: String = "",
    val deliveryFee: Double = 2.0,
    val isPlacingOrder: Boolean = false,
    val orderError: String? = null,
    val orderSuccess: Boolean = false
)