package com.example.foodya.data.repository

import android.util.Log
import com.example.foodya.data.remote.RestaurantApi
import com.example.foodya.domain.model.Food
import com.example.foodya.domain.model.Restaurant
import com.example.foodya.domain.repository.RestaurantRepository
import com.example.foodya.util.toUserFriendlyMessage
import kotlinx.coroutines.delay
import javax.inject.Inject

class RestaurantRepositoryImpl @Inject constructor(
    private val api: RestaurantApi
) : RestaurantRepository {

    override suspend fun getAllRestaurant(): Result<List<Restaurant>> {
        return try {
            // ============ REAL API CALL (COMMENTED OUT) ============
            // val response = api.getAllRestaurant()
            // Log.d("RestaurantRepositoryImpl", "Response: $response")
            // val result = response.map{it ->
            //     Restaurant(
            //         id = it.id,
            //         name = it.name,
            //         address = it.address,
            //         description = it.address,
            //         rating = it.rating,
            //         deliveryFee = it.deliveryFee,
            //         estimatedDeliveryTime = it.estimatedDeliveryTime,
            //         imageUrl = it.imageUrl
            //     )
            // }
            // Result.success(result)
            // ======================================================

            // Simulate network latency
            delay(1500)

            // Mock Data
            val mockRestaurants = listOf(
                Restaurant(
                    id = "res-001",
                    name = "The Italian Corner",
                    address = "123 Đường Lê Lợi, Quận 1, TP.HCM",
                    description = "Nhà hàng Ý chính thống với pizza và pasta thượng hạng",
                    rating = 4.8,
                    deliveryFee = 15000.0,
                    estimatedDeliveryTime = 30,
                    imageUrl = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800"
                ),
                Restaurant(
                    id = "res-002",
                    name = "Phở Hà Nội",
                    address = "456 Đường Nguyễn Huệ, Quận 1, TP.HCM",
                    description = "Phở bò truyền thống Hà Nội, nước dùng đậm đà",
                    rating = 4.6,
                    deliveryFee = 10000.0,
                    estimatedDeliveryTime = 25,
                    imageUrl = "https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=800"
                ),
                Restaurant(
                    id = "res-003",
                    name = "Sushi Master",
                    address = "789 Đường Hai Bà Trưng, Quận 3, TP.HCM",
                    description = "Sushi & Sashimi tươi ngon, đầu bếp Nhật Bản",
                    rating = 4.9,
                    deliveryFee = 20000.0,
                    estimatedDeliveryTime = 35,
                    imageUrl = "https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=800"
                ),
                Restaurant(
                    id = "res-004",
                    name = "Burger House",
                    address = "321 Đường Pasteur, Quận 1, TP.HCM",
                    description = "Burger bò Úc cao cấp, khoai tây chiên giòn rụm",
                    rating = 4.5,
                    deliveryFee = 12000.0,
                    estimatedDeliveryTime = 20,
                    imageUrl = "https://images.unsplash.com/photo-1550547660-d9450f859349?w=800"
                ),
                Restaurant(
                    id = "res-005",
                    name = "Bánh Mì Saigon",
                    address = "567 Đường Trần Hưng Đạo, Quận 5, TP.HCM",
                    description = "Bánh mì Sài Gòn truyền thống, nhân đa dạng",
                    rating = 4.7,
                    deliveryFee = 8000.0,
                    estimatedDeliveryTime = 15,
                    imageUrl = "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=800"
                ),
                Restaurant(
                    id = "res-006",
                    name = "Cơm Tấm Kiều Giang",
                    address = "890 Đường Cách Mạng Tháng 8, Quận 10, TP.HCM",
                    description = "Cơm tấm sườn nướng thơm ngon, đặc sản Sài Gòn",
                    rating = 4.4,
                    deliveryFee = 9000.0,
                    estimatedDeliveryTime = 20,
                    imageUrl = "https://images.unsplash.com/photo-1512058564366-18510be2db19?w=800"
                ),
                Restaurant(
                    id = "res-007",
                    name = "Thai Spice",
                    address = "234 Đường Võ Văn Tần, Quận 3, TP.HCM",
                    description = "Món Thái cay nồng đậm đà, pad thai hảo hạng",
                    rating = 4.6,
                    deliveryFee = 15000.0,
                    estimatedDeliveryTime = 30,
                    imageUrl = "https://images.unsplash.com/photo-1559314809-0d155014e29e?w=800"
                ),
                Restaurant(
                    id = "res-008",
                    name = "Lẩu Hải Sản",
                    address = "678 Đường Điện Biên Phủ, Quận Bình Thạnh, TP.HCM",
                    description = "Lẩu hải sản tươi sống, nước lẩu chua cay hấp dẫn",
                    rating = 4.7,
                    deliveryFee = 18000.0,
                    estimatedDeliveryTime = 40,
                    imageUrl = "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=800"
                )
            )

            Log.d("RestaurantRepositoryImpl", "Mock data loaded: ${mockRestaurants.size} restaurants")
            Result.success(mockRestaurants)
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun getMenuByRestaurantId(restaurantId: String): Result<List<Food>> {
        return try {
            // ============ REAL API CALL (COMMENTED OUT) ============
            // val response = api.getMenuByRestaurantId(restaurantId = restaurantId)
            // val result = response.map{it ->
            //     Food(
            //         id = it.id,
            //         restaurantId = it.restaurantId,
            //         restaurantName = it.restaurantName,
            //         name = it.name,
            //         description = it.description,
            //         price = it.price,
            //         imageUrl = it.imageUrl,
            //         category = it.category,
            //         isAvailable = it.isAvailable,
            //         isActive = it.isActive
            //     )
            // }
            // Result.success(result)
            // ======================================================

            // Simulate network latency
            delay(1500)

            // Mock Data - Get restaurant name from our mock list
            val restaurantName = when (restaurantId) {
                "res-001" -> "The Italian Corner"
                "res-002" -> "Phở Hà Nội"
                "res-003" -> "Sushi Master"
                "res-004" -> "Burger House"
                "res-005" -> "Bánh Mì Saigon"
                "res-006" -> "Cơm Tấm Kiều Giang"
                "res-007" -> "Thai Spice"
                "res-008" -> "Lẩu Hải Sản"
                else -> "Restaurant"
            }

            val mockMenuItems = when (restaurantId) {
                "res-001" -> listOf(
                    Food(
                        id = "food-001",
                        restaurantId = restaurantId,
                        restaurantName = restaurantName,
                        name = "Margherita Pizza",
                        description = "Pizza truyền thống với sốt cà chua, phô mai mozzarella và lá húng quế tươi",
                        price = 129000.0,
                        imageUrl = "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=800",
                        category = "Pizza",
                        isAvailable = true,
                        isActive = true
                    ),
                    Food(
                        id = "food-002",
                        restaurantId = restaurantId,
                        restaurantName = restaurantName,
                        name = "Carbonara Pasta",
                        description = "Mì Ý sốt kem trứng, thịt xông khói và phô mai Parmesan",
                        price = 149000.0,
                        imageUrl = "https://images.unsplash.com/photo-1612874742237-6526221588e3?w=800",
                        category = "Pasta",
                        isAvailable = true,
                        isActive = true
                    ),
                    Food(
                        id = "food-003",
                        restaurantId = restaurantId,
                        restaurantName = restaurantName,
                        name = "Tiramisu",
                        description = "Bánh ngọt Ý truyền thống với cà phê espresso và mascarpone",
                        price = 69000.0,
                        imageUrl = "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=800",
                        category = "Dessert",
                        isAvailable = true,
                        isActive = true
                    )
                )
                "res-002" -> listOf(
                    Food(
                        id = "food-004",
                        restaurantId = restaurantId,
                        restaurantName = restaurantName,
                        name = "Phở Bò Đặc Biệt",
                        description = "Phở bò với đầy đủ các loại thịt: tái, nạm, gầu, gân, sách",
                        price = 65000.0,
                        imageUrl = "https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=800",
                        category = "Phở",
                        isAvailable = true,
                        isActive = true
                    ),
                    Food(
                        id = "food-005",
                        restaurantId = restaurantId,
                        restaurantName = restaurantName,
                        name = "Phở Gà",
                        description = "Phở với thịt gà luộc, nước dùng thanh ngọt",
                        price = 55000.0,
                        imageUrl = "https://images.unsplash.com/photo-1555126634-323283e090fa?w=800",
                        category = "Phở",
                        isAvailable = true,
                        isActive = true
                    ),
                    Food(
                        id = "food-006",
                        restaurantId = restaurantId,
                        restaurantName = restaurantName,
                        name = "Nem Rán",
                        description = "Chả giò chiên giòn, nhân thịt và rau củ, chấm nước mắm",
                        price = 45000.0,
                        imageUrl = "https://images.unsplash.com/photo-1534422298391-e4f8c172dddb?w=800",
                        category = "Khai vị",
                        isAvailable = true,
                        isActive = true
                    )
                )
                "res-003" -> listOf(
                    Food(
                        id = "food-007",
                        restaurantId = restaurantId,
                        restaurantName = restaurantName,
                        name = "Sushi Set A",
                        description = "10 miếng sushi cá hồi, cá ngừ và bạch tuộc tươi",
                        price = 199000.0,
                        imageUrl = "https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=800",
                        category = "Sushi",
                        isAvailable = true,
                        isActive = true
                    ),
                    Food(
                        id = "food-008",
                        restaurantId = restaurantId,
                        restaurantName = restaurantName,
                        name = "Sashimi Premium",
                        description = "Sashimi cá hồi, cá ngừ đại dương cao cấp",
                        price = 249000.0,
                        imageUrl = "https://images.unsplash.com/photo-1617196034796-73dfa7b1fd56?w=800",
                        category = "Sashimi",
                        isAvailable = true,
                        isActive = true
                    ),
                    Food(
                        id = "food-009",
                        restaurantId = restaurantId,
                        restaurantName = restaurantName,
                        name = "Ramen Tonkotsu",
                        description = "Mì ramen với nước súp xương heo đậm đà",
                        price = 119000.0,
                        imageUrl = "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=800",
                        category = "Ramen",
                        isAvailable = true,
                        isActive = true
                    )
                )
                "res-004" -> listOf(
                    Food(
                        id = "food-010",
                        restaurantId = restaurantId,
                        restaurantName = restaurantName,
                        name = "Classic Beef Burger",
                        description = "Burger bò Úc 200g, phô mai cheddar, rau và sốt đặc biệt",
                        price = 99000.0,
                        imageUrl = "https://images.unsplash.com/photo-1550547660-d9450f859349?w=800",
                        category = "Burger",
                        isAvailable = true,
                        isActive = true
                    ),
                    Food(
                        id = "food-011",
                        restaurantId = restaurantId,
                        restaurantName = restaurantName,
                        name = "Double Cheese Burger",
                        description = "Burger 2 lớp thịt bò, phô mai cheddar và bacon giòn",
                        price = 139000.0,
                        imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=800",
                        category = "Burger",
                        isAvailable = true,
                        isActive = true
                    ),
                    Food(
                        id = "food-012",
                        restaurantId = restaurantId,
                        restaurantName = restaurantName,
                        name = "French Fries",
                        description = "Khoai tây chiên giòn rụm phục vụ kèm sốt mayonnaise",
                        price = 39000.0,
                        imageUrl = "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=800",
                        category = "Side",
                        isAvailable = true,
                        isActive = true
                    )
                )
                else -> listOf(
                    Food(
                        id = "food-default-1",
                        restaurantId = restaurantId,
                        restaurantName = restaurantName,
                        name = "Món đặc sản 1",
                        description = "Món ăn ngon đặc trưng của nhà hàng",
                        price = 89000.0,
                        imageUrl = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=800",
                        category = "Món chính",
                        isAvailable = true,
                        isActive = true
                    ),
                    Food(
                        id = "food-default-2",
                        restaurantId = restaurantId,
                        restaurantName = restaurantName,
                        name = "Món đặc sản 2",
                        description = "Món ăn được yêu thích nhất",
                        price = 109000.0,
                        imageUrl = "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=800",
                        category = "Món chính",
                        isAvailable = true,
                        isActive = true
                    ),
                    Food(
                        id = "food-default-3",
                        restaurantId = restaurantId,
                        restaurantName = restaurantName,
                        name = "Đồ uống",
                        description = "Nước giải khát tươi mát",
                        price = 25000.0,
                        imageUrl = "https://images.unsplash.com/photo-1437418747212-8d9709afab22?w=800",
                        category = "Đồ uống",
                        isAvailable = true,
                        isActive = true
                    )
                )
            }

            Log.d("RestaurantRepositoryImpl", "Mock menu loaded for $restaurantName: ${mockMenuItems.size} items")
            Result.success(mockMenuItems)
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }
}