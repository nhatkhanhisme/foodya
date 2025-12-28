package com.example.foodya.ui.screen.merchant.menu

import com.example.foodya.domain.model.FoodMenuItem
import com.example.foodya.domain.model.MerchantRestaurant

data class MenuState(
    val isLoading: Boolean = true,
    val myRestaurants: List<MerchantRestaurant> = emptyList(),
    val selectedRestaurant: MerchantRestaurant? = null,
    val menuItems: List<FoodMenuItem> = emptyList(),
    val error: String? = null,
    
    // Edit/Create dialog state
    val showEditDialog: Boolean = false,
    val editingItem: FoodMenuItem? = null,
    val isCreating: Boolean = false,
    
    // Form fields
    val formName: String = "",
    val formDescription: String = "",
    val formPrice: String = "",
    val formImageUrl: String = "",
    val formCategory: String = "",
    val formIsAvailable: Boolean = true,
    
    // Delete confirmation
    val showDeleteConfirmation: Boolean = false,
    val itemToDelete: FoodMenuItem? = null,
    
    val isProcessing: Boolean = false
)
