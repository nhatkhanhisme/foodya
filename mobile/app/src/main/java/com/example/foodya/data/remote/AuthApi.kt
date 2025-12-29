package com.example.foodya.data.remote

import com.example.foodya.data.model.ChangePasswordRequest
import com.example.foodya.data.model.JwtAuthResponse
import com.example.foodya.data.model.LoginRequest
import com.example.foodya.data.model.RefreshRequest
import com.example.foodya.data.model.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): JwtAuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): JwtAuthResponse

    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): JwtAuthResponse

    @POST("auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest)
}