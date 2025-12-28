package com.example.foodya.data.remote

import com.example.foodya.data.model.ChangePasswordRequest
import com.example.foodya.data.model.LoginRequest
import com.example.foodya.data.model.LoginResponse
import com.example.foodya.data.model.RefreshRequest
import com.example.foodya.data.model.RefreshResponse
import com.example.foodya.data.model.RegisterRequest
import com.example.foodya.data.model.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApi {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest) : LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest) : RegisterResponse

    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): RefreshResponse

    @PUT("auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest)
}