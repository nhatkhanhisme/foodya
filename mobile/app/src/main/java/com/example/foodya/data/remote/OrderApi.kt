package com.example.foodya.data.remote

import com.example.foodya.data.model.OrderRequest
import com.example.foodya.data.model.OrderResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface OrderApi {
    
    @POST("orders")
    suspend fun createOrder(@Body request: OrderRequest): OrderResponse
    
    @GET("orders/me")
    suspend fun getMyOrders(): List<OrderResponse>
    
    @GET("orders/me/active")
    suspend fun getMyActiveOrders(): List<OrderResponse>
    
    @PATCH("orders/{id}/cancel")
    suspend fun cancelOrder(
        @Path("id") orderId: String,
        @Query("reason") reason: String?
    ): OrderResponse
}
