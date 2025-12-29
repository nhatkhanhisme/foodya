package com.example.foodya.ui.screen.customer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodya.data.local.TokenManager
import com.example.foodya.data.model.OrderItemRequest
import com.example.foodya.data.model.OrderRequest
import com.example.foodya.data.model.toDomain
import com.example.foodya.domain.model.CartItem
import com.example.foodya.domain.model.Food
import com.example.foodya.domain.model.Order
import com.example.foodya.domain.model.User
import com.example.foodya.domain.model.enums.OrderStatus
import com.example.foodya.domain.repository.OrderRepository
import com.example.foodya.domain.repository.RestaurantRepository
import com.example.foodya.domain.repository.ThemeRepository
import com.example.foodya.ui.screen.customer.home.HomeState
import com.example.foodya.ui.screen.customer.order.OrderHistoryState
import com.example.foodya.ui.screen.customer.profile.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val restaurantRepo: RestaurantRepository,
    private val orderRepo: OrderRepository,
    private val userRepo: com.example.foodya.domain.repository.UserRepository,
    private val tokenManager: TokenManager,
    private val themeRepository: ThemeRepository
) : ViewModel() {

    // ========== HOME STATE ==========
    private val _homeState = MutableStateFlow(HomeState())
    val homeState = _homeState.asStateFlow()

    // ========== ORDER HISTORY STATE ==========
    private val _orderHistoryState = MutableStateFlow(OrderHistoryState())
    val orderHistoryState = _orderHistoryState.asStateFlow()

    // ========== PROFILE STATE ==========
    private val _profileState = MutableStateFlow(ProfileState())
    val profileState = _profileState.asStateFlow()

    private val allKeywords = listOf("Pizza", "Burger", "Sushi", "Italian", "Vietnamese", "Coffee", "Tea")

    init {
        loadHomeData()
        loadOrders()
        loadUserProfile()
        observeThemePreference()
    }

    // ========== HOME SCREEN FUNCTIONS ==========

    private fun loadHomeData() {
        viewModelScope.launch {
            _homeState.update { it.copy(isLoading = true) }
            
            val result = restaurantRepo.getRestaurants(
                sortBy = "popular",
                page = 0,
                size = 20
            )
            
            result.onSuccess { (list, hasMore) ->
                Log.d("CustomerViewModel", "Loaded ${list.size} restaurants, hasMore: $hasMore")
                _homeState.update {
                    it.copy(
                        isLoading = false,
                        nearbyRestaurants = list,
                        popularFoods = mockFoods()
                    )
                }
            }.onFailure { exception ->
                Log.e("CustomerViewModel", "Error loading restaurants: ${exception.message}")
                _homeState.update {
                    it.copy(
                        isLoading = false,
                        nearbyRestaurants = emptyList(),
                        popularFoods = mockFoods()
                    )
                }
            }
        }
    }

    fun onQueryChange(query: String) {
        _homeState.update {
            it.copy(
                searchQuery = query,
                searchSuggestions = if (query.isBlank()) emptyList() else allKeywords.filter { keyword ->
                    keyword.contains(query, ignoreCase = true)
                }
            )
        }
    }

    fun onSearchActiveChange(active: Boolean) {
        _homeState.update { it.copy(isSearchActive = active) }
    }

    fun onClearSearch() {
        _homeState.update { it.copy(searchQuery = "", searchSuggestions = emptyList()) }
    }

    fun onFoodSelected(food: Food) {
        _homeState.update { it.copy(selectedFood = food) }
    }

    fun onDismissFoodDetail() {
        _homeState.update { it.copy(selectedFood = null) }
    }

    fun addToCart(food: Food, restaurantId: String, restaurantName: String) {
        val currentCart = _homeState.value.cartItems
        val existingItem = currentCart.find { it.menuItem.id == food.id }
        
        if (currentCart.isNotEmpty() && _homeState.value.selectedRestaurantId != restaurantId) {
            _homeState.update {
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
            _homeState.update {
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
            _homeState.update {
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
        _homeState.update {
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
        _homeState.update {
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
        _homeState.update {
            it.copy(
                cartItems = emptyList(),
                selectedRestaurantId = null,
                selectedRestaurantName = null
            )
        }
    }

    fun showCheckout() {
        if (_homeState.value.cartItems.isEmpty()) {
            Log.w("CustomerViewModel", "Cannot show checkout with empty cart")
            return
        }
        _homeState.update { 
            it.copy(
                showCheckoutDialog = true,
                orderError = null,
                orderSuccess = false
            ) 
        }
    }
    
    fun hideCheckout() {
        _homeState.update { 
            it.copy(
                showCheckoutDialog = false,
                orderError = null
            ) 
        }
    }
    
    fun onDeliveryAddressChange(address: String) {
        _homeState.update { it.copy(deliveryAddress = address) }
    }
    
    fun onOrderNotesChange(notes: String) {
        _homeState.update { it.copy(orderNotes = notes) }
    }
    
    fun placeOrder() {
        val currentState = _homeState.value
        
        if (currentState.cartItems.isEmpty()) {
            _homeState.update { it.copy(orderError = "Cart is empty") }
            return
        }
        
        if (currentState.deliveryAddress.isBlank()) {
            _homeState.update { it.copy(orderError = "Please enter delivery address") }
            return
        }
        
        if (currentState.selectedRestaurantId == null) {
            _homeState.update { it.copy(orderError = "No restaurant selected") }
            return
        }
        
        viewModelScope.launch {
            _homeState.update { it.copy(isPlacingOrder = true, orderError = null) }
            
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
                Log.d("CustomerViewModel", "Order placed successfully: ${response.id}")
                _homeState.update {
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
                
                delay(2000)
                _homeState.update { it.copy(showCheckoutDialog = false, orderSuccess = false) }
                
            }.onFailure { error ->
                Log.e("CustomerViewModel", "Failed to place order: ${error.message}")
                _homeState.update {
                    it.copy(
                        isPlacingOrder = false,
                        orderError = error.message ?: "Failed to place order. Please try again."
                    )
                }
            }
        }
    }

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

    // ========== ORDER HISTORY FUNCTIONS ==========

    private fun loadOrders() {
        viewModelScope.launch {
            _orderHistoryState.update { it.copy(isLoading = true, error = null) }

            val result = orderRepo.getMyOrders()
            
            result.onSuccess { orderResponses ->
                val orders = orderResponses.map { it.toDomain() }
                Log.d("CustomerViewModel", "Loaded ${orders.size} orders")
                
                _orderHistoryState.update { 
                    it.copy(
                        allOrders = orders,
                        isLoading = false,
                        error = null
                    ) 
                }
                
                filterOrders(_orderHistoryState.value.selectedStatus)
            }.onFailure { error ->
                Log.e("CustomerViewModel", "Failed to load orders: ${error.message}")
                _orderHistoryState.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "Không thể tải danh sách đơn hàng",
                        orders = emptyList(),
                        allOrders = emptyList()
                    )
                }
            }
        }
    }

    fun onStatusSelected(status: OrderStatus) {
        _orderHistoryState.update { it.copy(selectedStatus = status) }
        filterOrders(status)
    }

    private fun filterOrders(status: OrderStatus) {
        val filtered = _orderHistoryState.value.allOrders.filter { it.status == status }
        _orderHistoryState.update {
            it.copy(orders = filtered)
        }
    }
    
    fun cancelOrder(orderId: String, reason: String?) {
        viewModelScope.launch {
            _orderHistoryState.update { 
                it.copy(
                    isCancelling = true, 
                    cancelError = null,
                    cancellingOrderId = orderId
                ) 
            }
            
            val result = orderRepo.cancelOrder(orderId, reason)
            
            result.onSuccess { orderResponse ->
                Log.d("CustomerViewModel", "Order cancelled successfully: $orderId")
                
                // Update the order in the list
                val updatedOrder = orderResponse.toDomain()
                val allOrders = _orderHistoryState.value.allOrders.map { order ->
                    if (order.id == orderId) updatedOrder else order
                }
                
                _orderHistoryState.update {
                    it.copy(
                        isCancelling = false,
                        cancelError = null,
                        cancellingOrderId = null,
                        allOrders = allOrders
                    )
                }
                
                // Refresh the filtered list
                filterOrders(_orderHistoryState.value.selectedStatus)
                
            }.onFailure { error ->
                Log.e("CustomerViewModel", "Failed to cancel order: ${error.message}")
                _orderHistoryState.update {
                    it.copy(
                        isCancelling = false,
                        cancelError = error.message ?: "Không thể hủy đơn hàng",
                        cancellingOrderId = null
                    )
                }
            }
        }
    }
    
    fun clearCancelError() {
        _orderHistoryState.update { it.copy(cancelError = null) }
    }
    
    fun refreshOrders() {
        loadOrders()
    }

    // ========== PROFILE FUNCTIONS ==========

    private fun observeThemePreference() {
        viewModelScope.launch {
            themeRepository.isDarkMode.collect { isDark ->
                _profileState.update { it.copy(isDarkMode = isDark ?: false) }
            }
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _profileState.update { it.copy(isLoading = true) }

            val result = userRepo.getCurrentUserProfile()
            
            result.onSuccess { user ->
                Log.d("CustomerViewModel", "Loaded user profile: ${user.username}")
                _profileState.update {
                    it.copy(
                        isLoading = false,
                        user = user
                    )
                }
            }.onFailure { error ->
                Log.e("CustomerViewModel", "Failed to load user profile: ${error.message}")
                _profileState.update {
                    it.copy(
                        isLoading = false,
                        user = null
                    )
                }
            }
        }
    }

    fun onToggleDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            themeRepository.setDarkMode(isDark)
        }
    }

    fun onLogout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            _profileState.update { it.copy(isLoading = true) }
            tokenManager.clear()
            delay(500)
            onLogoutSuccess()
        }
    }
}
