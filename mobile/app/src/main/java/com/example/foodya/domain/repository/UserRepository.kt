package com.example.foodya.domain.repository

import com.example.foodya.domain.model.User

interface UserRepository {
    suspend fun getCurrentUserProfile(): Result<User>
}
