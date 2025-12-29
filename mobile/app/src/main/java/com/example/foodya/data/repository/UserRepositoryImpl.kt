package com.example.foodya.data.repository

import android.util.Log
import com.example.foodya.data.remote.UserApi
import com.example.foodya.domain.model.User
import com.example.foodya.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
) : UserRepository {
    
    override suspend fun getCurrentUserProfile(): Result<User> {
        return try {
            val response = userApi.getCurrentUserProfile()
            if (response.isSuccessful) {
                val userProfileResponse = response.body()
                if (userProfileResponse != null) {
                    Result.success(userProfileResponse.toDomain())
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                Log.e("UserRepository", "Error getting user profile: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception getting user profile", e)
            Result.failure(e)
        }
    }
}
