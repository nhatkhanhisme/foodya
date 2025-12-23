package com.example.foodya.data.model

// Data Class
data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val accessToken: String, val refreshToken: String)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val fullName: String,
    val phoneNumber: String,
    val role: UserRole
)
data class RegisterResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresIn: Int
)

data class RefreshRequest(val refreshToken: String)
data class RefreshResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresIn: Int
)

// Enum
enum class UserRole {
    CUSTOMER,
    MERCHANT,
    OWNER,
    ADMIN
}

