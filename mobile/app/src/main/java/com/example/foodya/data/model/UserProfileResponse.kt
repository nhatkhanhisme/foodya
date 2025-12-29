package com.example.foodya.data.model

import com.example.foodya.domain.model.User
import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("username")
    val username: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("fullName")
    val fullName: String,
    
    @SerializedName("phoneNumber")
    val phoneNumber: String?,
    
    @SerializedName("role")
    val role: String,
    
    @SerializedName("isActive")
    val isActive: Boolean,
    
    @SerializedName("isEmailVerified")
    val isEmailVerified: Boolean,
    
    @SerializedName("isPhoneNumberVerified")
    val isPhoneNumberVerified: Boolean? = null,
    
    @SerializedName("profileImageUrl")
    val profileImageUrl: String? = null,
    
    @SerializedName("lastLoginAt")
    val lastLoginAt: String?,
    
    @SerializedName("createdAt")
    val createdAt: String,
    
    @SerializedName("updatedAt")
    val updatedAt: String
) {
    fun toDomain(): User {
        return User(
            id = id,
            username = username,
            email = email,
            fullName = fullName,
            phoneNumber = phoneNumber ?: "",
            role = role,
            isActive = isActive,
            isEmailVerified = isEmailVerified,
            lastLoginAt = lastLoginAt ?: "",
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
