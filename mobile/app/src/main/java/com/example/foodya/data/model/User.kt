package com.example.foodya.data.model

data class User(
    val id: String,
    val username: String,
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val role: String,
    val isActive: Boolean,
    val isEmailVerified: Boolean,
    val lastLoginAt: String,
    val createdAt: String,
    val updatedAt: String
)