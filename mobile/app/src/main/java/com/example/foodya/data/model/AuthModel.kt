package com.example.foodya.data.model

// Request DTOs
data class LoginRequest(val username: String, val password: String)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val fullName: String,
    val phoneNumber: String,
    val role: UserRole
)

data class RefreshRequest(val refreshToken: String)

// Response DTOs - Matching Backend JwtAuthResponse
data class JwtAuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val refreshTokenExpiresIn: Long,
    val userId: String,
    val username: String,
    val role: UserRole
)

// Legacy aliases for backward compatibility (can be removed later)
typealias LoginResponse = JwtAuthResponse
typealias RegisterResponse = JwtAuthResponse
typealias RefreshResponse = JwtAuthResponse

// Enum - matches backend Role enum
enum class UserRole {
    CUSTOMER,
    MERCHANT,
    OWNER,
    ADMIN
}

