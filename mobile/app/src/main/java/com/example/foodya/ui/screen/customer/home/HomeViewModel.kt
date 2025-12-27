package com.example.foodya.ui.screen.customer.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodya.domain.model.Food
import com.example.foodya.domain.model.Restaurant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    // Danh sách giả để lọc gợi ý tìm kiếm
    private val allKeywords = listOf("Pizza", "Burger", "Sushi", "Italian", "Vietnamese", "Coffee", "Tea")

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            // Giả lập độ trễ mạng
            delay(1500)

            _state.update {
                it.copy(
                    isLoading = false,
                    nearbyRestaurants = mockRestaurants(),
                    popularFoods = mockFoods()
                )
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
                rating = 4.5
            )
        }
    }

    private fun mockRestaurants(): List<Restaurant> {
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
