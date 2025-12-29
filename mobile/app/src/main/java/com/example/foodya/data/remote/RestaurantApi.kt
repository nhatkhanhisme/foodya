package com.example.foodya.data.remote

import com.example.foodya.data.model.MenuItemResponse
import com.example.foodya.data.model.PageResponse
import com.example.foodya.data.model.RestaurantResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RestaurantApi {
    
    /**
     * Get restaurants with filters and pagination
     * @param keyword Search keyword (searches in name, description, cuisine)
     * @param cuisine Filter by cuisine type
     * @param minRating Filter by minimum rating (1.0 - 5.0)
     * @param sortBy Sort by: popular (default), rating, name
     * @param page Page number (0-based)
     * @param size Page size (default: 20)
     */
    @GET("restaurants")
    suspend fun getRestaurants(
        @Query("keyword") keyword: String? = null,
        @Query("cuisine") cuisine: String? = null,
        @Query("minRating") minRating: Double? = null,
        @Query("sortBy") sortBy: String? = "popular",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): PageResponse<RestaurantResponse>

    /**
     * Get restaurant by ID
     */
    @GET("restaurants/{id}")
    suspend fun getRestaurantById(@Path("id") id: String): RestaurantResponse

    /**
     * Get popular restaurants
     * @param limit Number of restaurants to return (default: 10)
     */
    @GET("restaurants/popular")
    suspend fun getPopularRestaurants(@Query("limit") limit: Int = 10): List<RestaurantResponse>

    /**
     * Get menu items by restaurant ID
     */
    @GET("restaurants/{restaurantId}/menu-items/all")
    suspend fun getMenuByRestaurantId(@Path("restaurantId") restaurantId: String): List<MenuItemResponse>
}