package com.example.foodya.ui.screen.merchant.profile

import com.example.foodya.domain.model.User

data class MerchantProfileState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val isDarkMode: Boolean = false
)
