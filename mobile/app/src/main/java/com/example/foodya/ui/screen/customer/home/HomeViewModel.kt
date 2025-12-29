package com.example.foodya.ui.screen.customer.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodya.data.model.OrderItemRequest
import com.example.foodya.data.model.OrderRequest
import com.example.foodya.domain.model.CartItem
import com.example.foodya.domain.model.Food
import com.example.foodya.domain.model.Restaurant
import com.example.foodya.domain.repository.OrderRepository
import com.example.foodya.domain.repository.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val restaurantRepo: RestaurantRepository,
    private val orderRepo: OrderRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val allKeywords = listOf("Pizza", "Burger", "Sushi", "Italian", "Vietnamese", "Coffee", "Tea")

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // Load restaurants using the new API
            val result = restaurantRepo.getRestaurants(
                sortBy = "popular",
                page = 0,
                size = 20
            )
            
            result.onSuccess { (list, hasMore) ->
                Log.d("HomeViewModel", "Loaded ${list.size} restaurants, hasMore: $hasMore")
                _state.update {
                    it.copy(
                        isLoading = false,
                        nearbyRestaurants = list,
                        popularFoods = mockFoods() // Can be replaced with real popular foods API later
                    )
                }
            }.onFailure { exception ->
                Log.e("HomeViewModel", "Error loading restaurants: ${exception.message}")
                _state.update {
                    it.copy(
                        isLoading = false,
                        nearbyRestaurants = emptyList(),
                        popularFoods = mockFoods()
                    )
                }
            }
        }
    }

    // --- Search Logic ---

    fun onQueryChange(query: String) {
        _state.update {
            it.copy(
                searchQuery = query,
                // Lọc gợi ý đơn giản
                searchSuggestions = if (query.isBlank()) emptyList() else allKeywords.filter { keyword ->
                    keyword.contains(query, ignoreCase = true)
                }
            )
        }
    }

    fun onSearchActiveChange(active: Boolean) {
        _state.update { it.copy(isSearchActive = active) }
    }

    fun onClearSearch() {
        _state.update { it.copy(searchQuery = "", searchSuggestions = emptyList()) }
    }

    // --- Popup Food Detail
    fun onFoodSelected(food: Food) {
        _state.update { it.copy(selectedFood = food) }
    }

    fun onDismissFoodDetail() {
        _state.update { it.copy(selectedFood = null) }
    }

    // --- Cart Management ---
    
    fun addToCart(food: Food, restaurantId: String, restaurantName: String) {
        val currentCart = _state.value.cartItems
        val existingItem = currentCart.find { it.menuItem.id == food.id }
        
        // Check if adding from different restaurant
        if (currentCart.isNotEmpty() && _state.value.selectedRestaurantId != restaurantId) {
            // Clear cart and add new item
            _state.update {
                it.copy(
                    cartItems = listOf(
                        CartItem(
                            menuItem = food,
                            quantity = 1,
                        )
                    ),
                    selectedRestaurantId = restaurantId,
                    selectedRestaurantName = restaurantName
                )
            }
        } else if (existingItem != null) {
            // Update quantity
            _state.update {
                it.copy(
                    cartItems = currentCart.map { item ->
                        if (item.menuItem.id == food.id) {
                            item.copy(quantity = item.quantity + 1)
                        } else {
                            item
                        }
                    }
                )
            }
        } else {
            // Add new item
            _state.update {
                it.copy(
                    cartItems = currentCart + CartItem(
                        menuItem = food,
                        quantity = 1,
                    ),
                    selectedRestaurantId = restaurantId,
                    selectedRestaurantName = restaurantName
                )
            }
        }
    }
    
    fun removeFromCart(menuItemId: String) {
        _state.update {
            val newCart = it.cartItems.filter { item -> item.menuItem.id != menuItemId }
            it.copy(
                cartItems = newCart,
                selectedRestaurantId = if (newCart.isEmpty()) null else it.selectedRestaurantId,
                selectedRestaurantName = if (newCart.isEmpty()) null else it.selectedRestaurantName
            )
        }
    }
    
    fun updateCartItemQuantity(menuItemId: String, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(menuItemId)
            return
        }
        _state.update {
            it.copy(
                cartItems = it.cartItems.map { item ->
                    if (item.menuItem.id == menuItemId) {
                        item.copy(quantity = quantity)
                    } else {
                        item
                    }
                }
            )
        }
    }
    
    fun clearCart() {
        _state.update {
            it.copy(
                cartItems = emptyList(),
                selectedRestaurantId = null,
                selectedRestaurantName = null
            )
        }
    }

    // --- Checkout ---
    
    fun showCheckout() {
        if (_state.value.cartItems.isEmpty()) {
            Log.w("HomeViewModel", "Cannot show checkout with empty cart")
            return
        }
        _state.update { 
            it.copy(
                showCheckoutDialog = true,
                orderError = null,
                orderSuccess = false
            ) 
        }
    }
    
    fun hideCheckout() {
        _state.update { 
            it.copy(
                showCheckoutDialog = false,
                orderError = null
            ) 
        }
    }
    
    fun onDeliveryAddressChange(address: String) {
        _state.update { it.copy(deliveryAddress = address) }
    }
    
    fun onOrderNotesChange(notes: String) {
        _state.update { it.copy(orderNotes = notes) }
    }
    
    fun placeOrder() {
        val currentState = _state.value
        
        if (currentState.cartItems.isEmpty()) {
            _state.update { it.copy(orderError = "Cart is empty") }
            return
        }
        
        if (currentState.deliveryAddress.isBlank()) {
            _state.update { it.copy(orderError = "Please enter delivery address") }
            return
        }
        
        if (currentState.selectedRestaurantId == null) {
            _state.update { it.copy(orderError = "No restaurant selected") }
            return
        }
        
        viewModelScope.launch {
            _state.update { it.copy(isPlacingOrder = true, orderError = null) }
            
            val orderRequest = OrderRequest(
                restaurantId = currentState.selectedRestaurantId,
                items = currentState.cartItems.map { item ->
                    OrderItemRequest(
                        menuItemId = item.menuItem.id,
                        quantity = item.quantity,
                        notes = null
                    )
                },
                deliveryAddress = currentState.deliveryAddress,
                deliveryFee = currentState.deliveryFee,
                orderNotes = currentState.orderNotes.ifBlank { null }
            )
            
            val result = orderRepo.createOrder(orderRequest)
            
            result.onSuccess { response ->
                Log.d("HomeViewModel", "Order placed successfully: ${response.id}")
                _state.update {
                    it.copy(
                        isPlacingOrder = false,
                        orderSuccess = true,
                        orderError = null,
                        cartItems = emptyList(),
                        selectedRestaurantId = null,
                        selectedRestaurantName = null,
                        deliveryAddress = "",
                        orderNotes = ""
                    )
                }
                
                // Auto-close dialog after success
                delay(2000)
                _state.update { it.copy(showCheckoutDialog = false, orderSuccess = false) }
                
            }.onFailure { error ->
                Log.e("HomeViewModel", "Failed to place order: ${error.message}")
                _state.update {
                    it.copy(
                        isPlacingOrder = false,
                        orderError = error.message ?: "Failed to place order. Please try again."
                    )
                }
            }
        }
    }

    // --- Mock Data Generators (Dựa trên JSON của bạn) ---

    private fun mockFoods(): List<Food> {
        return List(5) { index ->
            Food(
                id = "food-$index",
                restaurantId = "res-1",
                restaurantName = "The Italian Corner",
                name = if (index % 2 == 0) "Margherita Pizza $index" else "Pasta Carbonara $index",
                description = "Classic Italian dish with premium ingredients",
                price = 12.99 + index,
                imageUrl = "https://via.placeholder.com/150",
                category = "Main Course",
            )
        }
    }

    private fun loadRestaurants(): List<Restaurant> {
        return List(3) { index ->
            Restaurant(
                id = "res-$index",
                name = if (index == 0) "The Italian Corner" else "Foodya Restaurant $index",
                address = "123 Street, City",
                description = "Best food in town",
                rating = 4.5 + (index * 0.1),
                deliveryFee = 2.0,
                estimatedDeliveryTime = 30,
                imageUrl = "https://via.placeholder.com/150"
            )
        }
    }
}
