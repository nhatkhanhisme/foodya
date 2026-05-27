---
name: model
description: "Skill for the Model area of foodya. 36 symbols across 24 files."
---

# Model

36 symbols | 24 files | Cohesion: 88%

## When to Use

- Working with code in `mobile/`
- Understanding how toDomain, Order, OrderItem work
- Modifying model-related functionality

## Key Files

| File | Symbols |
|------|---------|
| `mobile/app/src/main/java/com/example/foodya/data/repository/MerchantRepositoryImpl.kt` | getMyRestaurants, getOrdersByRestaurant, getMenuItems, toDomain |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/order/model/OrderItem.java` | getMenuItemId, getMenuItemName, calculateSubtotal, prePersist |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/order/model/Order.java` | addOrderItem, removeOrderItem, recalculateTotals, prePersist |
| `mobile/app/src/main/java/com/example/foodya/domain/repository/RestaurantRepository.kt` | getRestaurantById, getMenuByRestaurantId |
| `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/restaurant/RestaurantDetailViewModel.kt` | loadRestaurantDetails, retry |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/restaurant/model/Restaurant.java` | isFreeDelivery, calculateDeliveryFee |
| `mobile/app/src/main/java/com/example/foodya/data/model/OrderResponse.kt` | toDomain |
| `mobile/app/src/main/java/com/example/foodya/domain/model/Order.kt` | Order |
| `mobile/app/src/main/java/com/example/foodya/domain/model/OrderItem.kt` | OrderItem |
| `mobile/app/src/main/java/com/example/foodya/domain/model/OrderWithDetails.kt` | OrderWithDetails |

## Entry Points

Start here when exploring this area:

- **`toDomain`** (Function) — `mobile/app/src/main/java/com/example/foodya/data/model/OrderResponse.kt:72`
- **`Order`** (Class) — `mobile/app/src/main/java/com/example/foodya/domain/model/Order.kt:4`
- **`OrderItem`** (Class) — `mobile/app/src/main/java/com/example/foodya/domain/model/OrderItem.kt:2`
- **`OrderWithDetails`** (Class) — `mobile/app/src/main/java/com/example/foodya/domain/model/OrderWithDetails.kt:4`
- **`Food`** (Class) — `mobile/app/src/main/java/com/example/foodya/domain/model/Food.kt:2`

## Key Symbols

| Symbol | Type | File | Line |
|--------|------|------|------|
| `Order` | Class | `mobile/app/src/main/java/com/example/foodya/domain/model/Order.kt` | 4 |
| `OrderItem` | Class | `mobile/app/src/main/java/com/example/foodya/domain/model/OrderItem.kt` | 2 |
| `OrderWithDetails` | Class | `mobile/app/src/main/java/com/example/foodya/domain/model/OrderWithDetails.kt` | 4 |
| `Food` | Class | `mobile/app/src/main/java/com/example/foodya/domain/model/Food.kt` | 2 |
| `FoodMenuItem` | Class | `mobile/app/src/main/java/com/example/foodya/domain/model/FoodMenuItem.kt` | 2 |
| `MerchantRestaurant` | Class | `mobile/app/src/main/java/com/example/foodya/domain/model/MerchantRestaurant.kt` | 2 |
| `Restaurant` | Class | `mobile/app/src/main/java/com/example/foodya/domain/model/Restaurant.kt` | 2 |
| `User` | Class | `mobile/app/src/main/java/com/example/foodya/domain/model/User.kt` | 2 |
| `toDomain` | Function | `mobile/app/src/main/java/com/example/foodya/data/model/OrderResponse.kt` | 72 |
| `getMyRestaurants` | Method | `mobile/app/src/main/java/com/example/foodya/data/repository/MerchantRepositoryImpl.kt` | 17 |
| `getOrdersByRestaurant` | Method | `mobile/app/src/main/java/com/example/foodya/data/repository/MerchantRepositoryImpl.kt` | 71 |
| `getMenuItems` | Method | `mobile/app/src/main/java/com/example/foodya/data/repository/MerchantRepositoryImpl.kt` | 101 |
| `toDomain` | Method | `mobile/app/src/main/java/com/example/foodya/data/model/MenuItemResponse.kt` | 63 |
| `getRestaurantById` | Method | `mobile/app/src/main/java/com/example/foodya/domain/repository/RestaurantRepository.kt` | 28 |
| `getMenuByRestaurantId` | Method | `mobile/app/src/main/java/com/example/foodya/domain/repository/RestaurantRepository.kt` | 38 |
| `retry` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/restaurant/RestaurantDetailViewModel.kt` | 247 |
| `createOrder` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/order/controller/OrderController.java` | 32 |
| `fromEntity` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/order/dto/OrderItemResponse.java` | 47 |
| `getMenuItemId` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/order/model/OrderItem.java` | 78 |
| `getMenuItemName` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/order/model/OrderItem.java` | 85 |

## Execution Flows

| Flow | Type | Steps |
|------|------|-------|
| `UpdateStatus → OrderItemResponse` | cross_community | 6 |
| `UpdateStatus → GetMenuItemId` | cross_community | 6 |
| `UpdateStatus → GetMenuItemName` | cross_community | 6 |
| `ListOrders → OrderItemResponse` | cross_community | 5 |
| `ListOrders → GetMenuItemId` | cross_community | 5 |
| `ListOrders → GetMenuItemName` | cross_community | 5 |
| `UpdateOrderStatus → OrderItemResponse` | cross_community | 5 |
| `UpdateOrderStatus → GetMenuItemId` | cross_community | 5 |
| `UpdateOrderStatus → GetMenuItemName` | cross_community | 5 |
| `GetOrder → OrderItemResponse` | cross_community | 5 |

## Connected Areas

| Area | Connections |
|------|-------------|
| Repository | 3 calls |
| Service | 1 calls |
| Controller | 1 calls |
| Restaurant | 1 calls |

## How to Explore

1. `gitnexus_context({name: "toDomain"})` — see callers and callees
2. `gitnexus_query({query: "model"})` — find related execution flows
3. Read key files listed above for implementation details
