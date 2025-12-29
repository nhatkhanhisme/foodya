package com.example.foodya.ui.screen.customer.restaurant

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodya.data.model.OrderItemRequest
import com.example.foodya.data.model.OrderRequest
import com.example.foodya.domain.model.CartItem
import com.example.foodya.domain.model.CartSummary
import com.example.foodya.domain.model.Food
import com.example.foodya.domain.repository.OrderRepository
import com.example.foodya.domain.repository.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * RestaurantDetailViewModel - Manages UI state for Restaurant Detail screen
 * 
 * Features:
 * - Loads restaurant info and menu items
 * - Groups menu items by category
 * - Manages cart (add/remove items)
 * - Exposes unified UI state (Loading/Success/Error)
 */
@HiltViewModel
class RestaurantDetailViewModel @Inject constructor(
    private val repository: RestaurantRepository,
    private val orderRepository: OrderRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val TAG = "RestaurantDetailVM"
    }

    // Unified UI State
    private val _uiState = MutableStateFlow<RestaurantUiState>(RestaurantUiState.Loading)
    val uiState: StateFlow<RestaurantUiState> = _uiState.asStateFlow()

    // Internal cart state (not exposed directly)
    private val _cartMap = MutableStateFlow<Map<String, CartItem>>(emptyMap())

    // Checkout state
    private val _showCheckoutDialog = MutableStateFlow(false)
    val showCheckoutDialog = _showCheckoutDialog.asStateFlow()

    private val _deliveryAddress = MutableStateFlow("")
    val deliveryAddress = _deliveryAddress.asStateFlow()

    private val _orderNotes = MutableStateFlow("")
    val orderNotes = _orderNotes.asStateFlow()

    private val _isPlacingOrder = MutableStateFlow(false)
    val isPlacingOrder = _isPlacingOrder.asStateFlow()

    private val _orderError = MutableStateFlow<String?>(null)
    val orderError = _orderError.asStateFlow()

    private val _orderSuccess = MutableStateFlow(false)
    val orderSuccess = _orderSuccess.asStateFlow()

    init {
        loadRestaurantDetails()
    }

    /**
     * Load restaurant details and menu items from API
     * Updates UI state based on result
     */
    private fun loadRestaurantDetails() {
        val restaurantId = savedStateHandle.get<String>("restaurantId")
        
        if (restaurantId == null) {
            Log.e(TAG, "Restaurant ID is null from navigation arguments")
            _uiState.value = RestaurantUiState.Error
            return
        }

        viewModelScope.launch {
            // Set loading state
            _uiState.value = RestaurantUiState.Loading

            try {
                // Fetch restaurant info
                val restaurantResult = repository.getRestaurantById(restaurantId)
                
                // Fetch menu items
                val menuResult = repository.getMenuByRestaurantId(restaurantId)

                // Check both results
                if (restaurantResult.isSuccess && menuResult.isSuccess) {
                    val restaurant = restaurantResult.getOrThrow()
                    val menuItems = menuResult.getOrThrow()

                    // Convert menu items to Food domain model
                    val foodList = menuItems.map { item ->
                        Food(
                            id = item.id,
                            restaurantId = item.restaurantId,
                            restaurantName = item.restaurantName,
                            name = item.name,
                            description = item.description,
                            price = item.price,
                            imageUrl = item.imageUrl,
                            category = item.category,
                            isAvailable = item.isAvailable,
                            isActive = item.isActive
                        )
                    }

                    // Group menu items by category
                    val groupedMenu = foodList
                        .filter { it.isAvailable && it.isActive }
                        .groupBy { it.category.ifBlank { "Khác" } }
                        .toSortedMap()

                    Log.d(TAG, "Successfully loaded restaurant: ${restaurant.name}")
                    Log.d(TAG, "Menu items grouped into ${groupedMenu.size} categories")

                    // Update to success state
                    _uiState.value = RestaurantUiState.Success(
                        restaurant = restaurant,
                        groupedMenu = groupedMenu,
                        cartMap = _cartMap.value,
                        cartSummary = calculateCartSummary()
                    )

                } else {
                    // Log the actual error for debugging
                    val error = restaurantResult.exceptionOrNull() ?: menuResult.exceptionOrNull()
                    Log.e(TAG, "Failed to load restaurant details: ${error?.message}", error)
                    
                    _uiState.value = RestaurantUiState.Error
                }

            } catch (e: Exception) {
                Log.e(TAG, "Exception while loading restaurant details", e)
                _uiState.value = RestaurantUiState.Error
            }
        }
    }

    /**
     * Add item to cart or increment quantity
     */
    fun addItem(item: Food) {
        val currentMap = _cartMap.value.toMutableMap()
        val existingItem = currentMap[item.id]

        if (existingItem != null) {
            currentMap[item.id] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            currentMap[item.id] = CartItem(item, 1)
        }
        
        _cartMap.value = currentMap
        updateCartInSuccessState()
    }

    /**
     * Remove item from cart or decrement quantity
     */
    fun removeItem(item: Food) {
        val currentMap = _cartMap.value.toMutableMap()
        val existingItem = currentMap[item.id] ?: return

        if (existingItem.quantity > 1) {
            currentMap[item.id] = existingItem.copy(quantity = existingItem.quantity - 1)
        } else {
            currentMap.remove(item.id)
        }
        
        _cartMap.value = currentMap
        updateCartInSuccessState()
    }

    /**
     * Get quantity of specific item in cart
     */
    fun getQuantity(itemId: String): Int {
        return _cartMap.value[itemId]?.quantity ?: 0
    }

    /**
     * Clear all items from cart
     */
    fun clearCart() {
        _cartMap.value = emptyMap()
        updateCartInSuccessState()
        Log.d(TAG, "Cart cleared")
    }

    /**
     * Calculate cart summary (total quantity and price)
     */
    private fun calculateCartSummary(): CartSummary {
        val totalQuantity = _cartMap.value.values.sumOf { it.quantity }
        val totalPrice = _cartMap.value.values.sumOf { it.menuItem.price * it.quantity }
        return CartSummary(totalQuantity, totalPrice)
    }

    /**
     * Update cart data in success state after cart modifications
     */
    private fun updateCartInSuccessState() {
        val currentState = _uiState.value
        if (currentState is RestaurantUiState.Success) {
            _uiState.value = currentState.copy(
                cartMap = _cartMap.value,
                cartSummary = calculateCartSummary()
            )
        }
    }

    /**
     * Retry loading after error
     */
    fun retry() {
        loadRestaurantDetails()
    }

    // --- Checkout Functions ---

    fun showCheckout() {
        if (_cartMap.value.isEmpty()) {
            Log.w(TAG, "Cannot show checkout with empty cart")
            return
        }
        _showCheckoutDialog.value = true
        _orderError.value = null
        _orderSuccess.value = false
    }

    fun hideCheckout() {
        _showCheckoutDialog.value = false
        _orderError.value = null
    }

    fun onDeliveryAddressChange(address: String) {
        _deliveryAddress.value = address
    }

    fun onOrderNotesChange(notes: String) {
        _orderNotes.value = notes
    }

    fun placeOrder() {
        val currentUiState = _uiState.value
        if (currentUiState !is RestaurantUiState.Success) {
            _orderError.value = "Restaurant information not loaded"
            return
        }

        if (_cartMap.value.isEmpty()) {
            _orderError.value = "Cart is empty"
            return
        }

        if (_deliveryAddress.value.isBlank()) {
            _orderError.value = "Please enter delivery address"
            return
        }

        viewModelScope.launch {
            _isPlacingOrder.value = true
            _orderError.value = null

            val orderRequest = OrderRequest(
                restaurantId = currentUiState.restaurant.id,
                items = _cartMap.value.values.map { cartItem ->
                    OrderItemRequest(
                        menuItemId = cartItem.menuItem.id,
                        quantity = cartItem.quantity,
                        notes = null
                    )
                },
                deliveryAddress = _deliveryAddress.value,
                deliveryFee = currentUiState.restaurant.deliveryFee,
                orderNotes = _orderNotes.value.ifBlank { null }
            )

            val result = orderRepository.createOrder(orderRequest)

            result.onSuccess { response ->
                Log.d(TAG, "Order placed successfully: ${response.id}")
                _isPlacingOrder.value = false
                _orderSuccess.value = true
                _orderError.value = null

                // Clear cart and reset form
                _cartMap.value = emptyMap()
                _deliveryAddress.value = ""
                _orderNotes.value = ""
                updateCartInSuccessState()

                // Auto-close dialog after success
                delay(2000)
                _showCheckoutDialog.value = false
                _orderSuccess.value = false

            }.onFailure { error ->
                Log.e(TAG, "Failed to place order: ${error.message}")
                _isPlacingOrder.value = false
                _orderError.value = error.message ?: "Failed to place order. Please try again."
            }
        }
    }
}