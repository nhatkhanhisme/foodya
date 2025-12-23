package com.example.foodya.ui.screen.auth

import com.example.foodya.data.model.UserRole

data class AuthState(
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val role: UserRole = UserRole.CUSTOMER,
    val isLoginMode: Boolean = true, // true = Login, false = Register
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null
)