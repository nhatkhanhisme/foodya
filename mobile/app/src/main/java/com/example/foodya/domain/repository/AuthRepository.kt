package com.example.foodya.domain.repository

import com.example.foodya.data.model.JwtAuthResponse
import com.example.foodya.data.model.UserRole

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<JwtAuthResponse>

    suspend fun register(
        username: String,
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String,
        role: UserRole
    ): Result<JwtAuthResponse>

    suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit>
}