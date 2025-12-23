package com.example.foodya.data.repository

import android.app.Application
import com.example.foodya.R
import com.example.foodya.data.model.LoginRequest
import com.example.foodya.data.model.LoginResponse
import com.example.foodya.data.model.RefreshResponse
import com.example.foodya.data.model.RegisterRequest
import com.example.foodya.data.model.RegisterResponse
import com.example.foodya.data.model.UserRole
import com.example.foodya.data.remote.AuthApi
import com.example.foodya.domain.repository.AuthRepository
import com.example.foodya.util.toUserFriendlyMessage
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val appContext: Application
) : AuthRepository {

    init {
        val appName = appContext.getString(R.string.app_name)
        println("Repository initialized. App: $appName")
    }

    override suspend fun login(username: String, password: String): Result<LoginResponse> {
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
    ): Result<RegisterResponse> {
        return try {
            val response = api.register(RegisterRequest(username, email, password, fullName, phoneNumber, role))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun refresh(): Result<RefreshResponse> {
        TODO("Not yet implemented")
    }
}