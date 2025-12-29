package com.example.foodya.ui.screen.customer.restaurant

import com.example.foodya.domain.model.CartItem
import com.example.foodya.domain.model.CartSummary
import com.example.foodya.domain.model.Food
import com.example.foodya.domain.model.Restaurant

/**
 * RestaurantUiState - Sealed interface for Restaurant Detail screen states
 * 
 * This pattern provides:
 * - Clear state management
 * - Type-safe state transitions
 * - Easy error handling
 * - Clean UI rendering logic
 */
sealed interface RestaurantUiState {
    /**
     * Loading - Initial state while fetching data
     */
    data object Loading : RestaurantUiState

    /**
     * Success - Data loaded successfully
     * 
     * @param restaurant Restaurant information (name, address, rating, etc.)
     * @param groupedMenu Menu items grouped by category
     * @param cartMap Current cart items mapped by food ID
     * @param cartSummary Cart summary (total quantity and price)
     */
    data class Success(
        val restaurant: Restaurant,
        val groupedMenu: Map<String, List<Food>>,
        val cartMap: Map<String, CartItem>,
        val cartSummary: CartSummary
    ) : RestaurantUiState

    /**
     * Error - Failed to load data
     * 
     * Note: The error message is logged to Logcat, not shown to users
     * The UI shows a friendly "restaurant closed" message instead
     */
    data object Error : RestaurantUiState
}
