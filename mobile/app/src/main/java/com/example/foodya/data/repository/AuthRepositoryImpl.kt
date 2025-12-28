package com.example.foodya.data.repository

import com.example.foodya.data.model.ChangePasswordRequest
import com.example.foodya.data.model.JwtAuthResponse
import com.example.foodya.data.model.LoginRequest
import com.example.foodya.data.model.RegisterRequest
import com.example.foodya.data.model.UserRole
import com.example.foodya.data.remote.AuthApi
import com.example.foodya.domain.repository.AuthRepository
import com.example.foodya.util.toUserFriendlyMessage
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
) : AuthRepository {
    override suspend fun login(username: String, password: String): Result<JwtAuthResponse> {
        return try {
            val response = api.login(LoginRequest(username, password))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun register(
        username: String,
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String,
        role: UserRole
    ): Result<JwtAuthResponse> {
        return try {
            val response = api.register(RegisterRequest(username, email, password, fullName, phoneNumber, role))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Result<Unit> {
        return try {
            api.changePassword(ChangePasswordRequest(currentPassword, newPassword, confirmPassword))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }
}