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
import com.example.foodya.util.toUserFriendlyMessage
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
    private val userRepo: com.example.foodya.domain.repository.UserRepository,
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
        // loadMenuData is not needed here as loadDashboard already initializes menu state
        loadMerchantProfile()
        observeThemePreference()
    }

    fun onRestaurantSelected(restaurant: MerchantRestaurant) {
        _dashboardState.update { it.copy(selectedRestaurant = restaurant, orders = emptyList()) }
        _menuState.update { it.copy(isLoading = true, selectedRestaurant = restaurant, menuItems = emptyList()) }
        loadMenuItems(restaurant.id)
        loadOrders(restaurant.id)
    }
    
    // ========== RESTAURANT EDIT FUNCTIONS ==========
    
    fun showEditRestaurantDialog() {
        val restaurant = _dashboardState.value.selectedRestaurant ?: return
        _dashboardState.update {
            it.copy(
                showEditRestaurantDialog = true,
                editRestaurantError = null,
                editName = restaurant.name,
                editAddress = restaurant.address,
                editPhoneNumber = restaurant.phoneNumber,
                editEmail = restaurant.email ?: "",
                editDescription = restaurant.description ?: "",
                editCuisine = restaurant.cuisine,
                editOpeningTime = restaurant.openingTime ?: "",
                editClosingTime = restaurant.closingTime ?: "",
                editOpeningHours = restaurant.openingHours ?: "",
                editMinimumOrder = restaurant.minimumOrder?.toString() ?: "",
                editMaxDeliveryDistance = restaurant.maxDeliveryDistance?.toString() ?: ""
            )
        }
    }
    
    fun hideEditRestaurantDialog() {
        _dashboardState.update { 
            it.copy(
                showEditRestaurantDialog = false,
                editRestaurantError = null
            ) 
        }
    }
    
    fun onEditNameChange(value: String) {
        _dashboardState.update { it.copy(editName = value) }
    }
    
    fun onEditAddressChange(value: String) {
        _dashboardState.update { it.copy(editAddress = value) }
    }
    
    fun onEditPhoneNumberChange(value: String) {
        _dashboardState.update { it.copy(editPhoneNumber = value) }
    }
    
    fun onEditEmailChange(value: String) {
        _dashboardState.update { it.copy(editEmail = value) }
    }
    
    fun onEditDescriptionChange(value: String) {
        _dashboardState.update { it.copy(editDescription = value) }
    }
    
    fun onEditCuisineChange(value: String) {
        _dashboardState.update { it.copy(editCuisine = value) }
    }
    
    fun onEditOpeningTimeChange(value: String) {
        _dashboardState.update { it.copy(editOpeningTime = value) }
    }
    
    fun onEditClosingTimeChange(value: String) {
        _dashboardState.update { it.copy(editClosingTime = value) }
    }
    
    fun onEditOpeningHoursChange(value: String) {
        _dashboardState.update { it.copy(editOpeningHours = value) }
    }
    
    fun onEditMinimumOrderChange(value: String) {
        _dashboardState.update { it.copy(editMinimumOrder = value) }
    }
    
    fun onEditMaxDeliveryDistanceChange(value: String) {
        _dashboardState.update { it.copy(editMaxDeliveryDistance = value) }
    }
    
    fun saveRestaurantChanges() {
        val currentState = _dashboardState.value
        val restaurant = currentState.selectedRestaurant ?: return
        
        // Comprehensive validation based on backend constraints
        val validationError = validateRestaurantData(
            name = currentState.editName,
            address = currentState.editAddress,
            phoneNumber = currentState.editPhoneNumber,
            email = currentState.editEmail,
            description = currentState.editDescription,
            cuisine = currentState.editCuisine,
            openingTime = currentState.editOpeningTime,
            closingTime = currentState.editClosingTime,
            openingHours = currentState.editOpeningHours,
            minimumOrder = currentState.editMinimumOrder,
            maxDeliveryDistance = currentState.editMaxDeliveryDistance
        )
        
        if (validationError != null) {
            _dashboardState.update { it.copy(editRestaurantError = validationError) }
            return
        }
        
        viewModelScope.launch {
            _dashboardState.update { it.copy(isUpdatingRestaurant = true, editRestaurantError = null) }
            
            val request = com.example.foodya.data.model.RestaurantRequest(
                name = currentState.editName.trim(),
                address = currentState.editAddress.trim(),
                phoneNumber = currentState.editPhoneNumber.trim(),
                email = currentState.editEmail.trim().ifBlank { null },
                description = currentState.editDescription.trim().ifBlank { null },
                cuisine = currentState.editCuisine.trim(),
                openingTime = currentState.editOpeningTime.trim().ifBlank { null },
                closingTime = currentState.editClosingTime.trim().ifBlank { null },
                openingHours = currentState.editOpeningHours.trim().ifBlank { null },
                minimumOrder = currentState.editMinimumOrder.trim().toDoubleOrNull(),
                maxDeliveryDistance = currentState.editMaxDeliveryDistance.trim().toDoubleOrNull(),
                isOpen = restaurant.isOpen
            )
            
            val result = merchantRepo.updateRestaurant(restaurant.id, request)
            
            result.onSuccess { updatedRestaurant ->
                Log.d("MerchantViewModel", "Restaurant updated successfully: ${updatedRestaurant.name}")
                
                // Update the restaurants list and selected restaurant
                _dashboardState.update { state ->
                    state.copy(
                        isUpdatingRestaurant = false,
                        showEditRestaurantDialog = false,
                        selectedRestaurant = updatedRestaurant,
                        myRestaurants = state.myRestaurants.map { 
                            if (it.id == updatedRestaurant.id) updatedRestaurant else it 
                        },
                        editRestaurantError = null
                    )
                }
                
                // Update menu state as well
                _menuState.update { it.copy(selectedRestaurant = updatedRestaurant) }
                
            }.onFailure { error ->
                Log.e("MerchantViewModel", "Failed to update restaurant: ${error.message}", error)
                val friendlyError = error.toUserFriendlyMessage()
                _dashboardState.update {
                    it.copy(
                        isUpdatingRestaurant = false,
                        editRestaurantError = friendlyError
                    )
                }
            }
        }
    }
    
    /**
     * Validates restaurant data based on backend constraints
     * Returns error message if validation fails, null otherwise
     */
    private fun validateRestaurantData(
        name: String,
        address: String,
        phoneNumber: String,
        email: String,
        description: String,
        cuisine: String,
        openingTime: String,
        closingTime: String,
        openingHours: String,
        minimumOrder: String,
        maxDeliveryDistance: String
    ): String? {
        // Name validation: @NotBlank @Size(min = 2, max = 200)
        if (name.isBlank()) {
            return "Tên nhà hàng không được để trống"
        }
        if (name.trim().length < 2) {
            return "Tên nhà hàng phải có ít nhất 2 ký tự"
        }
        if (name.trim().length > 200) {
            return "Tên nhà hàng không được vượt quá 200 ký tự"
        }
        
        // Address validation: @NotBlank @Size(max = 500)
        if (address.isBlank()) {
            return "Địa chỉ không được để trống"
        }
        if (address.trim().length > 500) {
            return "Địa chỉ không được vượt quá 500 ký tự"
        }
        
        // Phone number validation: @NotBlank @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$")
        if (phoneNumber.isBlank()) {
            return "Số điện thoại không được để trống"
        }
        val phoneRegex = Regex("^\\+?[1-9]\\d{1,14}$")
        if (!phoneNumber.trim().matches(phoneRegex)) {
            return "Số điện thoại không hợp lệ (ví dụ: +84909123456 hoặc 0909123456)"
        }
        
        // Email validation: @Email (optional)
        if (email.isNotBlank()) {
            val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
            if (!email.trim().matches(emailRegex)) {
                return "Email không hợp lệ"
            }
        }
        
        // Description validation: @Size(max = 2000)
        if (description.trim().length > 2000) {
            return "Mô tả không được vượt quá 2000 ký tự"
        }
        
        // Cuisine validation: @NotBlank @Size(max = 100)
        if (cuisine.isBlank()) {
            return "Loại ẩm thực không được để trống"
        }
        if (cuisine.trim().length > 100) {
            return "Loại ẩm thực không được vượt quá 100 ký tự"
        }
        
        // Opening time validation: @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")
        if (openingTime.isNotBlank()) {
            val timeRegex = Regex("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")
            if (!openingTime.trim().matches(timeRegex)) {
                return "Giờ mở cửa không hợp lệ (định dạng: HH:mm, ví dụ: 08:00)"
            }
        }
        
        // Closing time validation: @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")
        if (closingTime.isNotBlank()) {
            val timeRegex = Regex("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")
            if (!closingTime.trim().matches(timeRegex)) {
                return "Giờ đóng cửa không hợp lệ (định dạng: HH:mm, ví dụ: 22:00)"
            }
        }
        
        // Opening hours validation: @Size(max = 200)
        if (openingHours.trim().length > 200) {
            return "Mô tả giờ mở cửa không được vượt quá 200 ký tự"
        }
        
        // Minimum order validation: @Min(0)
        if (minimumOrder.isNotBlank()) {
            val minOrderValue = minimumOrder.trim().toDoubleOrNull()
            if (minOrderValue == null) {
                return "Đơn hàng tối thiểu phải là số"
            }
            if (minOrderValue < 0) {
                return "Đơn hàng tối thiểu không được âm"
            }
        }
        
        // Max delivery distance validation: @Min(0) @Max(100)
        if (maxDeliveryDistance.isNotBlank()) {
            val maxDistance = maxDeliveryDistance.trim().toDoubleOrNull()
            if (maxDistance == null) {
                return "Khoảng cách giao hàng tối đa phải là số"
            }
            if (maxDistance < 0) {
                return "Khoảng cách giao hàng không được âm"
            }
            if (maxDistance > 100) {
                return "Khoảng cách giao hàng không được vượt quá 100 km"
            }
        }
        
        return null
    }
    
    fun toggleRestaurantStatus() {
        val restaurant = _dashboardState.value.selectedRestaurant ?: return
        
        viewModelScope.launch {
            _dashboardState.update { it.copy(isTogglingStatus = true) }
            
            val result = merchantRepo.toggleRestaurantStatus(restaurant.id)
            
            result.onSuccess { updatedRestaurant ->
                Log.d("MerchantViewModel", "Restaurant status toggled: ${updatedRestaurant.isOpen}")
                
                _dashboardState.update { state ->
                    state.copy(
                        isTogglingStatus = false,
                        selectedRestaurant = updatedRestaurant,
                        myRestaurants = state.myRestaurants.map { 
                            if (it.id == updatedRestaurant.id) updatedRestaurant else it 
                        }
                    )
                }
                
                _menuState.update { it.copy(selectedRestaurant = updatedRestaurant) }
                
            }.onFailure { error ->
                Log.e("MerchantViewModel", "Failed to toggle restaurant status: ${error.message}")
                _dashboardState.update { it.copy(isTogglingStatus = false) }
            }
        }
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
                    // Sync menu state with the same selected restaurant
                    _menuState.update {
                        it.copy(
                            isLoading = false,
                            myRestaurants = restaurants,
                            selectedRestaurant = firstRestaurant
                        )
                    }
                    loadOrders(firstRestaurant.id)
                    loadMenuItems(firstRestaurant.id)
                } else {
                    _dashboardState.update {
                        it.copy(
                            isLoading = false,
                            myRestaurants = emptyList()
                        )
                    }
                    _menuState.update {
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
                _menuState.update {
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
            
            // Wait for dashboard to load restaurants first, then load menu items
            val selectedRestaurant = _menuState.value.selectedRestaurant
            if (selectedRestaurant != null) {
                loadMenuItems(selectedRestaurant.id)
            }
            _menuState.update { it.copy(isLoading = false) }
        }
    }

    private fun loadMenuItems(restaurantId: String) {
        viewModelScope.launch {
            val result = merchantRepo.getMenuItems(restaurantId)
            result.onSuccess { items ->
                Log.d("MerchantViewModel", "Loaded ${items.size} menu items")
                _menuState.update { it.copy(isLoading = false, menuItems = items) }
            }.onFailure { error ->
                Log.e("MerchantViewModel", "Error loading menu items: ${error.message}")
                _menuState.update { it.copy(isLoading = false, error = error.message) }
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

            val result = userRepo.getCurrentUserProfile()
            
            result.onSuccess { user ->
                Log.d("MerchantViewModel", "Loaded user profile: ${user.username}")
                _profileState.update {
                    it.copy(
                        isLoading = false,
                        user = user
                    )
                }
            }.onFailure { error ->
                Log.e("MerchantViewModel", "Failed to load user profile: ${error.message}")
                _profileState.update {
                    it.copy(
                        isLoading = false,
                        user = null
                    )
                }
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
