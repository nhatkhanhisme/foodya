package com.example.foodya.data.model

import com.example.foodya.domain.model.MerchantRestaurant
import com.google.gson.annotations.SerializedName

data class MerchantRestaurantResponse(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("address")
    val address: String,
    
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    
    @SerializedName("email")
    val email: String? = null,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("cuisine")
    val cuisine: String,
    
    @SerializedName("imageUrl")
    val imageUrl: String? = null,
    
    @SerializedName("coverImageUrl")
    val coverImageUrl: String? = null,
    
    @SerializedName("rating")
    val rating: Double? = 0.0,
    
    @SerializedName("totalReviews")
    val totalReviews: Int? = 0,
    
    @SerializedName("isOpen")
    val isOpen: Boolean = true,
    
    @SerializedName("isActive")
    val isActive: Boolean = true,
    
    @SerializedName("openingTime")
    val openingTime: String? = null,
    
    @SerializedName("closingTime")
    val closingTime: String? = null,
    
    @SerializedName("openingHours")
    val openingHours: String? = null,
    
    @SerializedName("deliveryFee")
    val deliveryFee: Double? = 0.0,
    
    @SerializedName("minimumOrder")
    val minimumOrder: Double? = null,
    
    @SerializedName("maxDeliveryDistance")
    val maxDeliveryDistance: Double? = null,
    
    @SerializedName("estimatedDeliveryTime")
    val estimatedDeliveryTime: Int? = 30,
    
    @SerializedName("ownerId")
    val ownerId: String,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null,
    
    @SerializedName("menuItemsCount")
    val menuItemsCount: Int? = 0
) {
    fun toDomain(): MerchantRestaurant {
        return MerchantRestaurant(
            id = id,
            name = name,
            address = address,
            phoneNumber = phoneNumber,
            email = email,
            description = description,
            cuisine = cuisine,
            imageUrl = imageUrl,
            coverImageUrl = coverImageUrl,
            rating = rating ?: 0.0,
            totalReviews = totalReviews ?: 0,
            isOpen = isOpen,
            isActive = isActive,
            openingTime = openingTime,
            closingTime = closingTime,
            openingHours = openingHours,
            deliveryFee = deliveryFee ?: 0.0,
            minimumOrder = minimumOrder,
            maxDeliveryDistance = maxDeliveryDistance,
            estimatedDeliveryTime = estimatedDeliveryTime ?: 30,
            ownerId = ownerId,
            menuItemsCount = menuItemsCount ?: 0
        )
    }
}

data class RegisterRestaurantRequest(
    val name: String,
    val address: String,
    val description: String,
    val deliveryFee: Double,
    val estimatedDeliveryTime: Int,
    val imageUrl: String
)
