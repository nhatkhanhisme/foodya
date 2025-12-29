package com.example.foodya.data.remote

import com.example.foodya.data.model.UserProfileResponse
import retrofit2.Response
import retrofit2.http.GET

interface UserApi {
    @GET("users/me")
    suspend fun getCurrentUserProfile(): Response<UserProfileResponse>
}
