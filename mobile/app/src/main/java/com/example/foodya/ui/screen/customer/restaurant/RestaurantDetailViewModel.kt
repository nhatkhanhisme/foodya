package com.example.foodya.ui.screen.customer.restaurant

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodya.domain.model.CartItem
import com.example.foodya.domain.model.CartSummary
import com.example.foodya.domain.model.Food
import com.example.foodya.domain.repository.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantDetailViewModel @Inject constructor(
    private val repository: RestaurantRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _menuList = MutableStateFlow<List<Food>>(emptyList())
    val menuList: StateFlow<List<Food>> = _menuList.asStateFlow()

    private val _cartMap = MutableStateFlow<Map<String, CartItem>>(emptyMap())
    val cartMap = _cartMap.asStateFlow()

    val cartSummary = _cartMap.map { map ->
        val totalQuantity = map.values.sumOf { it.quantity }
        val totalPrice = map.values.sumOf { it.menuItem.price * it.quantity }
        CartSummary(totalQuantity, totalPrice)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CartSummary(0, 0.0))

    init {
        loadRestaurantDetails()
    }

    private fun loadRestaurantDetails() {
        val restaurantId = savedStateHandle.get<String>("restaurantId") ?: return
        viewModelScope.launch {
            val result = repository.getMenuByRestaurantId(restaurantId)
            result.onSuccess {items ->
                _menuList.value = items.map { item ->
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
                        isActive = item.isActive,
                    )
                }
            }
                .onFailure {  e ->
                    Log.e("MenuLoad", "Lỗi khi load menu: ${e.message}")
                }
        }
    }

    // --- LOGIC TĂNG GIẢM MÓN ---

    fun addItem(item: Food) {
        val currentMap = _cartMap.value.toMutableMap()
        // item.id là String, Map key là String -> Hợp lệ
        val existingItem = currentMap[item.id]

        if (existingItem != null) {
            currentMap[item.id] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            currentMap[item.id] = CartItem(item, 1)
        }
        _cartMap.value = currentMap
    }

    fun removeItem(item: Food) {
        val currentMap = _cartMap.value.toMutableMap()
        val existingItem = currentMap[item.id] ?: return

        if (existingItem.quantity > 1) {
            currentMap[item.id] = existingItem.copy(quantity = existingItem.quantity - 1)
        } else {
            currentMap.remove(item.id)
        }
        _cartMap.value = currentMap
    }

    fun getQuantity(itemId: String): Int {
        return _cartMap.value[itemId]?.quantity ?: 0
    }
}