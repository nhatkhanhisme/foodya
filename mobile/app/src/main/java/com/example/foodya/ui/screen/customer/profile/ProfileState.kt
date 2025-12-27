package com.example.foodya.ui.screen.customer.profile

import com.example.foodya.domain.model.User

data class ProfileState(
    val isLoading: Boolean = true,
    val user: User? = null, // Dữ liệu user lấy từ API
    val isDarkMode: Boolean = false, // Trạng thái Dark Mode
    val error: String? = null
)