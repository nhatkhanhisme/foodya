package com.example.foodya.ui.screen.merchant.menu

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodya.data.model.MenuItemRequest
import com.example.foodya.domain.model.FoodMenuItem
import com.example.foodya.domain.model.MerchantRestaurant
import com.example.foodya.domain.repository.MerchantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val merchantRepo: MerchantRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MenuState())
    val state = _state.asStateFlow()

    init {
        loadMenuData()
    }

    private fun loadMenuData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            val result = merchantRepo.getMyRestaurants()
            result.onSuccess { restaurants ->
                Log.d("MenuViewModel", "Loaded ${restaurants.size} restaurants")
                if (restaurants.isNotEmpty()) {
                    val firstRestaurant = restaurants.first()
                    _state.update {
                        it.copy(
                            isLoading = false,
                            myRestaurants = restaurants,
                            selectedRestaurant = firstRestaurant
                        )
                    }
                    loadMenuItems(firstRestaurant.id)
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            myRestaurants = emptyList()
                        )
                    }
                }
            }.onFailure { error ->
                Log.e("MenuViewModel", "Error loading restaurants: ${error.message}")
                _state.update {
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
                Log.d("MenuViewModel", "Loaded ${items.size} menu items")
                _state.update { it.copy(menuItems = items) }
            }.onFailure { error ->
                Log.e("MenuViewModel", "Error loading menu items: ${error.message}")
                _state.update { it.copy(error = error.message) }
            }
        }
    }

    fun onRestaurantSelected(restaurant: MerchantRestaurant) {
        _state.update { it.copy(selectedRestaurant = restaurant, menuItems = emptyList()) }
        loadMenuItems(restaurant.id)
    }

    fun onAddNewItem() {
        _state.update {
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
        _state.update {
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
        _state.update {
            it.copy(
                showDeleteConfirmation = true,
                itemToDelete = item
            )
        }
    }

    fun onDismissDialog() {
        _state.update {
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
                formIsAvailable = true
            )
        }
    }

    fun onFormNameChange(value: String) {
        _state.update { it.copy(formName = value) }
    }

    fun onFormDescriptionChange(value: String) {
        _state.update { it.copy(formDescription = value) }
    }

    fun onFormPriceChange(value: String) {
        _state.update { it.copy(formPrice = value) }
    }

    fun onFormImageUrlChange(value: String) {
        _state.update { it.copy(formImageUrl = value) }
    }

    fun onFormCategoryChange(value: String) {
        _state.update { it.copy(formCategory = value) }
    }

    fun onFormIsAvailableChange(value: Boolean) {
        _state.update { it.copy(formIsAvailable = value) }
    }

    fun onSaveItem() {
        val currentState = _state.value
        val restaurantId = currentState.selectedRestaurant?.id ?: return

        // Validation
        if (currentState.formName.isBlank()) {
            _state.update { it.copy(error = "Tên món ăn không được để trống") }
            return
        }
        if (currentState.formPrice.isBlank() || currentState.formPrice.toDoubleOrNull() == null) {
            _state.update { it.copy(error = "Giá không hợp lệ") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isProcessing = true, error = null) }

            val price = currentState.formPrice.toDouble()
            
            val request = MenuItemRequest(
                name = currentState.formName,
                description = currentState.formDescription,
                price = price,
                imageUrl = currentState.formImageUrl,
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
                Log.d("MenuViewModel", "Item saved successfully")
                loadMenuItems(restaurantId)
                _state.update {
                    it.copy(
                        isProcessing = false,
                        showEditDialog = false,
                        editingItem = null,
                        formName = "",
                        formDescription = "",
                        formPrice = "",
                        formImageUrl = "",
                        formCategory = "",
                        formIsAvailable = true
                    )
                }
            }.onFailure { error ->
                Log.e("MenuViewModel", "Error saving item: ${error.message}")
                _state.update {
                    it.copy(
                        isProcessing = false,
                        error = error.message
                    )
                }
            }
        }
    }

    fun onConfirmDelete() {
        val itemId = _state.value.itemToDelete?.id ?: return
        val restaurantId = _state.value.selectedRestaurant?.id ?: return

        viewModelScope.launch {
            _state.update { it.copy(isProcessing = true, error = null) }

            val result = merchantRepo.deleteMenuItem(restaurantId, itemId)
            result.onSuccess {
                Log.d("MenuViewModel", "Item deleted successfully")
                loadMenuItems(restaurantId)
                _state.update {
                    it.copy(
                        isProcessing = false,
                        showDeleteConfirmation = false,
                        itemToDelete = null
                    )
                }
            }.onFailure { error ->
                Log.e("MenuViewModel", "Error deleting item: ${error.message}")
                _state.update {
                    it.copy(
                        isProcessing = false,
                        error = error.message
                    )
                }
            }
        }
    }

    fun onRetry() {
        loadMenuData()
    }
}
