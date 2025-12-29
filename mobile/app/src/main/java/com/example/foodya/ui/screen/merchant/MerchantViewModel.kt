package com.example.foodya.ui.screen.merchant

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodya.data.local.TokenManager
import com.example.foodya.data.model.MenuItemRequest
import com.example.foodya.domain.model.FoodMenuItem
import com.example.foodya.domain.model.MerchantRestaurant
import com.example.foodya.domain.model.OrderWithDetails
import com.example.foodya.domain.model.User
import com.example.foodya.domain.model.enums.OrderStatus
import com.example.foodya.domain.repository.MerchantRepository
import com.example.foodya.domain.repository.StorageRepository
import com.example.foodya.domain.repository.ThemeRepository
import com.example.foodya.ui.screen.merchant.dashboard.DashboardState
import com.example.foodya.ui.screen.merchant.menu.MenuState
import com.example.foodya.ui.screen.merchant.profile.MerchantProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MerchantViewModel @Inject constructor(
    private val merchantRepo: MerchantRepository,
    private val storageRepo: StorageRepository,
    private val tokenManager: TokenManager,
    private val themeRepository: ThemeRepository
) : ViewModel() {

    // ========== DASHBOARD STATE ==========
    private val _dashboardState = MutableStateFlow(DashboardState())
    val dashboardState = _dashboardState.asStateFlow()

    // ========== MENU STATE ==========
    private val _menuState = MutableStateFlow(MenuState())
    val menuState = _menuState.asStateFlow()

    // ========== PROFILE STATE ==========
    private val _profileState = MutableStateFlow(MerchantProfileState())
    val profileState = _profileState.asStateFlow()

    init {
        loadDashboard()
        loadMenuData()
        loadMerchantProfile()
        observeThemePreference()
    }

    fun onRestaurantSelected(restaurant: MerchantRestaurant) {
        _dashboardState.update { it.copy(selectedRestaurant = restaurant, orders = emptyList()) }
        _menuState.update { it.copy(selectedRestaurant = restaurant, menuItems = emptyList()) }
        loadMenuItems(restaurant.id)
        loadOrders(restaurant.id)
    }

    // ========== DASHBOARD SCREEN FUNCTIONS ==========

    private fun loadDashboard() {
        viewModelScope.launch {
            _dashboardState.update { it.copy(isLoading = true, error = null) }
            
            val result = merchantRepo.getMyRestaurants()
            result.onSuccess { restaurants ->
                Log.d("MerchantViewModel", "Loaded ${restaurants.size} restaurants")
                if (restaurants.isNotEmpty()) {
                    val firstRestaurant = restaurants.first()
                    _dashboardState.update {
                        it.copy(
                            isLoading = false,
                            myRestaurants = restaurants,
                            selectedRestaurant = firstRestaurant,
                        )
                    }
                    loadOrders(firstRestaurant.id)
                } else {
                    _dashboardState.update {
                        it.copy(
                            isLoading = false,
                            myRestaurants = emptyList()
                        )
                    }
                }
            }.onFailure { error ->
                Log.e("MerchantViewModel", "Error loading restaurants: ${error.message}")
                _dashboardState.update {
                    it.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
            }
        }
    }

    private fun loadOrders(restaurantId: String) {
        viewModelScope.launch {
            val result = merchantRepo.getOrdersByRestaurant(restaurantId)
            result.onSuccess { orders ->
                Log.d("MerchantViewModel", "Loaded ${orders.size} orders")
                val pendingCount = orders.count { it.status == OrderStatus.PENDING }
                _dashboardState.update {
                    it.copy(
                        orders = orders,
                        pendingOrdersCount = pendingCount
                    )
                }
            }.onFailure { error ->
                Log.e("MerchantViewModel", "Error loading orders: ${error.message}")
                _dashboardState.update {
                    it.copy(error = error.message)
                }
            }
        }
    }

    fun onOrderClick(order: OrderWithDetails) {
        _dashboardState.update { it.copy(selectedOrder = order) }
    }

    fun onDismissOrderDialog() {
        _dashboardState.update { it.copy(selectedOrder = null, cancelReason = "", showCancelReasonDialog = false) }
    }

    fun onCancelReasonChange(reason: String) {
        _dashboardState.update { it.copy(cancelReason = reason) }
    }

    fun onUpdateOrderStatus(newStatus: OrderStatus) {
        val currentOrder = _dashboardState.value.selectedOrder ?: return
        
        if (newStatus == OrderStatus.CANCELLED) {
            _dashboardState.update { it.copy(showCancelReasonDialog = true) }
            return
        }
        
        updateOrderStatusInternal(currentOrder.id, newStatus, null)
    }

    fun onConfirmCancellation() {
        val currentOrder = _dashboardState.value.selectedOrder ?: return
        val reason = _dashboardState.value.cancelReason
        
        if (reason.isBlank()) {
            _dashboardState.update { it.copy(error = "Vui lòng nhập lý do hủy") }
            return
        }
        
        updateOrderStatusInternal(currentOrder.id, OrderStatus.CANCELLED, reason)
    }

    private fun updateOrderStatusInternal(orderId: String, status: OrderStatus, cancelReason: String?) {
        viewModelScope.launch {
            _dashboardState.update { it.copy(isUpdatingOrderStatus = true, error = null) }
            delay(500)
            
            val result = merchantRepo.updateOrderStatus(orderId, status, cancelReason)
            result.onSuccess { updatedOrder ->
                Log.d("MerchantViewModel", "Order status updated: ${updatedOrder.status}")
                _dashboardState.value.selectedRestaurant?.let { restaurant ->
                    loadOrders(restaurant.id)
                }
                _dashboardState.update {
                    it.copy(
                        isUpdatingOrderStatus = false,
                        selectedOrder = null,
                        cancelReason = "",
                        showCancelReasonDialog = false
                    )
                }
            }.onFailure { error ->
                Log.e("MerchantViewModel", "Error updating order status: ${error.message}")
                _dashboardState.update {
                    it.copy(
                        isUpdatingOrderStatus = false,
                        error = error.message
                    )
                }
            }
        }
    }

    fun onNavigateToRegisterRestaurant() {
        Log.d("MerchantViewModel", "Navigate to register restaurant")
    }

    fun onDashboardRetry() {
        loadDashboard()
    }

    // ========== MENU SCREEN FUNCTIONS ==========

    private fun loadMenuData() {
        viewModelScope.launch {
            _menuState.update { it.copy(isLoading = true, error = null) }
            
            val result = merchantRepo.getMyRestaurants()
            result.onSuccess { restaurants ->
                Log.d("MerchantViewModel", "Loaded ${restaurants.size} restaurants for menu")
                if (restaurants.isNotEmpty()) {
                    val firstRestaurant = restaurants.first()
                    _menuState.update {
                        it.copy(
                            isLoading = false,
                            myRestaurants = restaurants,
                            selectedRestaurant = firstRestaurant
                        )
                    }
                    loadMenuItems(firstRestaurant.id)
                } else {
                    _menuState.update {
                        it.copy(
                            isLoading = false,
                            myRestaurants = emptyList()
                        )
                    }
                }
            }.onFailure { error ->
                Log.e("MerchantViewModel", "Error loading restaurants: ${error.message}")
                _menuState.update {
                    it.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
            }
        }
    }

    private fun loadMenuItems(restaurantId: String) {
        viewModelScope.launch {
            val result = merchantRepo.getMenuItems(restaurantId)
            result.onSuccess { items ->
                Log.d("MerchantViewModel", "Loaded ${items.size} menu items")
                _menuState.update { it.copy(menuItems = items) }
            }.onFailure { error ->
                Log.e("MerchantViewModel", "Error loading menu items: ${error.message}")
                _menuState.update { it.copy(error = error.message) }
            }
        }
    }

    fun onAddNewItem() {
        _menuState.update {
            it.copy(
                showEditDialog = true,
                isCreating = true,
                editingItem = null,
                formName = "",
                formDescription = "",
                formPrice = "",
                formImageUrl = "",
                formCategory = "",
                formIsAvailable = true
            )
        }
    }

    fun onEditItem(item: FoodMenuItem) {
        _menuState.update {
            it.copy(
                showEditDialog = true,
                isCreating = false,
                editingItem = item,
                formName = item.name,
                formDescription = item.description,
                formPrice = item.price.toString(),
                formImageUrl = item.imageUrl,
                formCategory = item.category,
                formIsAvailable = item.isAvailable
            )
        }
    }

    fun onDeleteItem(item: FoodMenuItem) {
        _menuState.update {
            it.copy(
                showDeleteConfirmation = true,
                itemToDelete = item
            )
        }
    }

    fun onDismissMenuDialog() {
        _menuState.update {
            it.copy(
                showEditDialog = false,
                showDeleteConfirmation = false,
                editingItem = null,
                itemToDelete = null,
                formName = "",
                formDescription = "",
                formPrice = "",
                formImageUrl = "",
                formCategory = "",
                formIsAvailable = true,
                selectedImageUri = null,
                isUploadingImage = false
            )
        }
    }

    fun onFormNameChange(value: String) {
        _menuState.update { it.copy(formName = value) }
    }

    fun onFormDescriptionChange(value: String) {
        _menuState.update { it.copy(formDescription = value) }
    }

    fun onFormPriceChange(value: String) {
        _menuState.update { it.copy(formPrice = value) }
    }

    fun onFormImageUrlChange(value: String) {
        _menuState.update { it.copy(formImageUrl = value) }
    }

    fun onFormCategoryChange(value: String) {
        _menuState.update { it.copy(formCategory = value) }
    }

    fun onFormIsAvailableChange(value: Boolean) {
        _menuState.update { it.copy(formIsAvailable = value) }
    }

    fun onImageSelected(uri: Uri) {
        _menuState.update { it.copy(selectedImageUri = uri) }
    }

    fun onImageCleared() {
        _menuState.update { it.copy(selectedImageUri = null, formImageUrl = "") }
    }

    fun onSaveItem() {
        val currentState = _menuState.value
        val restaurantId = currentState.selectedRestaurant?.id ?: return

        if (currentState.formName.isBlank()) {
            _menuState.update { it.copy(error = "Tên món ăn không được để trống") }
            return
        }
        if (currentState.formPrice.isBlank() || currentState.formPrice.toDoubleOrNull() == null) {
            _menuState.update { it.copy(error = "Giá không hợp lệ") }
            return
        }

        viewModelScope.launch {
            _menuState.update { it.copy(isProcessing = true, error = null) }

            var imageUrl = currentState.formImageUrl
            if (currentState.selectedImageUri != null) {
                _menuState.update { it.copy(isUploadingImage = true) }
                
                storageRepo.uploadImage(
                    imageUri = currentState.selectedImageUri,
                    bucketName = "images"
                ).collect { result ->
                    result.onSuccess { uploadedUrl ->
                        Log.d("MerchantViewModel", "Image uploaded: $uploadedUrl")
                        imageUrl = uploadedUrl
                        _menuState.update { it.copy(isUploadingImage = false) }
                    }.onFailure { error ->
                        Log.e("MerchantViewModel", "Image upload failed: ${error.message}")
                        _menuState.update {
                            it.copy(
                                isProcessing = false,
                                isUploadingImage = false,
                                error = "Tải ảnh thất bại: ${error.message}"
                            )
                        }
                        return@collect
                    }
                }
            }

            val price = currentState.formPrice.toDouble()
            
            val request = MenuItemRequest(
                name = currentState.formName,
                description = currentState.formDescription,
                price = price,
                imageUrl = imageUrl,
                category = currentState.formCategory,
                isAvailable = currentState.formIsAvailable
            )

            val result = if (currentState.isCreating) {
                merchantRepo.createMenuItem(
                    restaurantId = restaurantId,
                    request = request
                )
            } else {
                val itemId = currentState.editingItem?.id ?: return@launch
                merchantRepo.updateMenuItem(
                    restaurantId = restaurantId,
                    menuItemId = itemId,
                    request = request
                )
            }

            result.onSuccess {
                Log.d("MerchantViewModel", "Item saved successfully")
                loadMenuItems(restaurantId)
                _menuState.update {
                    it.copy(
                        isProcessing = false,
                        showEditDialog = false,
                        editingItem = null,
                        formName = "",
                        formDescription = "",
                        formPrice = "",
                        formImageUrl = "",
                        formCategory = "",
                        formIsAvailable = true,
                        selectedImageUri = null,
                        isUploadingImage = false
                    )
                }
            }.onFailure { error ->
                Log.e("MerchantViewModel", "Error saving item: ${error.message}")
                _menuState.update {
                    it.copy(
                        isProcessing = false,
                        error = error.message
                    )
                }
            }
        }
    }

    fun onConfirmDelete() {
        val itemId = _menuState.value.itemToDelete?.id ?: return
        val restaurantId = _menuState.value.selectedRestaurant?.id ?: return

        viewModelScope.launch {
            _menuState.update { it.copy(isProcessing = true, error = null) }

            val result = merchantRepo.deleteMenuItem(restaurantId, itemId)
            result.onSuccess {
                Log.d("MerchantViewModel", "Item deleted successfully")
                loadMenuItems(restaurantId)
                _menuState.update {
                    it.copy(
                        isProcessing = false,
                        showDeleteConfirmation = false,
                        itemToDelete = null
                    )
                }
            }.onFailure { error ->
                Log.e("MerchantViewModel", "Error deleting item: ${error.message}")
                _menuState.update {
                    it.copy(
                        isProcessing = false,
                        error = error.message
                    )
                }
            }
        }
    }

    fun onMenuRetry() {
        loadMenuData()
    }

    // ========== PROFILE SCREEN FUNCTIONS ==========

    private fun observeThemePreference() {
        viewModelScope.launch {
            themeRepository.isDarkMode.collect { isDark ->
                _profileState.update { it.copy(isDarkMode = isDark ?: false) }
            }
        }
    }

    private fun loadMerchantProfile() {
        viewModelScope.launch {
            _profileState.update { it.copy(isLoading = true) }

            delay(1000)

            val mockUser = User(
                id = "merchant-001",
                username = "merchant_foodya",
                email = "merchant@foodya.com",
                fullName = "Quản lý nhà hàng",
                phoneNumber = "+84 901 234 567",
                role = "MERCHANT",
                isActive = true,
                isEmailVerified = true,
                lastLoginAt = "2025-12-28T07:49:56.843Z",
                createdAt = "2025-01-01T07:49:56.843Z",
                updatedAt = "2025-12-28T07:49:56.843Z"
            )

            _profileState.update {
                it.copy(
                    isLoading = false,
                    user = mockUser
                )
            }
        }
    }

    fun onMerchantToggleDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            themeRepository.setDarkMode(isDark)
        }
    }

    fun onMerchantLogout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            _profileState.update { it.copy(isLoading = true) }
            
            tokenManager.clear()
            delay(500)

            onLogoutSuccess()
        }
    }
}
