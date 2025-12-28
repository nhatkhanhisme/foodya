package com.example.foodya.domain.repository

import com.example.foodya.domain.model.Food
import com.example.foodya.domain.model.Restaurant

interface RestaurantRepository {
    /**
     * Get restaurants with pagination and filters
     * @param keyword Search keyword
     * @param cuisine Filter by cuisine type
     * @param minRating Filter by minimum rating
     * @param sortBy Sort by: popular, rating, name
     * @param page Page number (0-based)
     * @param size Page size
     * @return Result with list of restaurants and pagination info
     */
    suspend fun getRestaurants(
        keyword: String? = null,
        cuisine: String? = null,
        minRating: Double? = null,
        sortBy: String? = "popular",
        page: Int = 0,
        size: Int = 20
    ): Result<Pair<List<Restaurant>, Boolean>> // Returns (restaurants, hasMore)

    /**
     * Get restaurant by ID
     */
    suspend fun getRestaurantById(restaurantId: String): Result<Restaurant>

    /**
     * Get popular restaurants
     */
    suspend fun getPopularRestaurants(limit: Int = 10): Result<List<Restaurant>>

    /**
     * Get menu items by restaurant ID
     */
    suspend fun getMenuByRestaurantId(restaurantId: String): Result<List<Food>>

    /**
     * Legacy method for backward compatibility
     */
    @Deprecated("Use getRestaurants() instead", ReplaceWith("getRestaurants()"))
    suspend fun getAllRestaurant(): Result<Pair<List<Restaurant>, Boolean>> {
        return getRestaurants()
    }
}