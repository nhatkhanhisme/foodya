package com.example.foodya.domain.repository

import com.example.foodya.data.model.LoginResponse
import com.example.foodya.data.model.RefreshResponse
import com.example.foodya.data.model.RegisterRequest
import com.example.foodya.data.model.RegisterResponse
import com.example.foodya.data.model.UserRole

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<LoginResponse>

    suspend fun register(
        username: String,
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String,
        role: UserRole
    ): Result<RegisterResponse>

    suspend fun refresh(): Result<RefreshResponse>
}