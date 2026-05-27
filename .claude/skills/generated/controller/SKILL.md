---
name: controller
description: "Skill for the Controller area of foodya. 106 symbols across 34 files."
---

# Controller

106 symbols | 34 files | Cohesion: 81%

## When to Use

- Working with code in `foodya-backend/`
- Understanding how OrderResponse, User, listOrders work
- Modifying controller-related functionality

## Key Files

| File | Symbols |
|------|---------|
| `foodya-backend/src/main/java/com/foodya/foodya_backend/order/service/OrderService.java` | cancelMyOrder, getOrderById, getOrdersByRestaurant, adminListOrders, updateOrderStatus (+3) |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/restaurant/service/RestaurantService.java` | getRestaurantsByOwner, toggleRestaurantStatus, getAllRestaurants, getAllRestaurantsIncludingInactive, getRestaurantById (+3) |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/restaurant/service/MenuItemService.java` | getMenuItemsByDietaryPreferences, getMenuItemsByRestaurant, getAllMenuItemsByRestaurant, searchMenuItems, getMenuItemsByCategory (+2) |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/middleware/controller/MenuItemController.java` | getMenuItemsByDietaryPreferences, getMenuItems, getAllMenuItems, searchMenuItems, getMenuItemsByCategory (+1) |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/restaurant/controller/MenuItemController.java` | getMenuItemsByDietaryPreferences, getMenuItems, getAllMenuItems, searchMenuItems, getMenuItemsByCategory (+1) |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/restaurant/repository/MenuItemRepository.java` | findByDietaryPreferences, findByRestaurantIdAndIsActiveTrueAndIsAvailableTrue, findByRestaurantId, searchByRestaurantAndName, findByRestaurantIdAndCategoryAndIsActiveTrue (+1) |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/admin/controller/AdminOrderController.java` | listOrders, getOrder, updateStatus, revenue, deleteOrder |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/order/model/Order.java` | getCustomerId, getRestaurantId, cancel, isCancellable, updateStatus |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/merchant/controller/MerchantRestaurantController.java` | getCurrentUser, isAdmin, getMyRestaurants, updateRestaurant, toggleRestaurantStatus |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/restaurant/repository/RestaurantRepository.java` | findByOwnerId, findByIsActiveTrue, findByFilters, findByIsActiveTrue |

## Entry Points

Start here when exploring this area:

- **`OrderResponse`** (Class) — `foodya-backend/src/main/java/com/foodya/foodya_backend/order/dto/OrderResponse.java:16`
- **`User`** (Class) — `foodya-backend/src/main/java/com/foodya/foodya_backend/user/model/User.java:15`
- **`listOrders`** (Method) — `foodya-backend/src/main/java/com/foodya/foodya_backend/admin/controller/AdminOrderController.java:30`
- **`getOrder`** (Method) — `foodya-backend/src/main/java/com/foodya/foodya_backend/admin/controller/AdminOrderController.java:42`
- **`getRestaurantOrders`** (Method) — `foodya-backend/src/main/java/com/foodya/foodya_backend/merchant/controller/MerchantOrderController.java:37`

## Key Symbols

| Symbol | Type | File | Line |
|--------|------|------|------|
| `OrderResponse` | Class | `foodya-backend/src/main/java/com/foodya/foodya_backend/order/dto/OrderResponse.java` | 16 |
| `User` | Class | `foodya-backend/src/main/java/com/foodya/foodya_backend/user/model/User.java` | 15 |
| `listOrders` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/admin/controller/AdminOrderController.java` | 30 |
| `getOrder` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/admin/controller/AdminOrderController.java` | 42 |
| `getRestaurantOrders` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/merchant/controller/MerchantOrderController.java` | 37 |
| `getOrderById` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/merchant/controller/MerchantOrderController.java` | 61 |
| `cancelOrder` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/order/controller/OrderController.java` | 82 |
| `fromEntity` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/order/dto/OrderResponse.java` | 102 |
| `getCustomerId` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/order/model/Order.java` | 100 |
| `getRestaurantId` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/order/model/Order.java` | 104 |
| `cancel` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/order/model/Order.java` | 143 |
| `isCancellable` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/order/model/Order.java` | 148 |
| `findByRestaurant_Id` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/order/repository/OrderRepository.java` | 27 |
| `adminSearch` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/order/repository/OrderRepository.java` | 57 |
| `cancelMyOrder` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/order/service/OrderService.java` | 143 |
| `getOrderById` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/order/service/OrderService.java` | 165 |
| `getOrdersByRestaurant` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/order/service/OrderService.java` | 172 |
| `adminListOrders` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/order/service/OrderService.java` | 196 |
| `getCurrentUser` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/merchant/controller/MerchantRestaurantController.java` | 46 |
| `isAdmin` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/merchant/controller/MerchantRestaurantController.java` | 52 |

## Execution Flows

| Flow | Type | Steps |
|------|------|-------|
| `Login → Key` | cross_community | 7 |
| `UpdateStatus → OrderItemResponse` | cross_community | 6 |
| `UpdateStatus → GetMenuItemId` | cross_community | 6 |
| `UpdateStatus → GetMenuItemName` | cross_community | 6 |
| `ListOrders → OrderItemResponse` | cross_community | 5 |
| `ListOrders → GetMenuItemId` | cross_community | 5 |
| `ListOrders → GetMenuItemName` | cross_community | 5 |
| `UpdateStatus → OrderResponse` | cross_community | 5 |
| `UpdateStatus → GetCustomerId` | cross_community | 5 |
| `UpdateStatus → GetRestaurantId` | cross_community | 5 |

## Connected Areas

| Area | Connections |
|------|-------------|
| Service | 10 calls |
| Repository | 4 calls |
| Model | 1 calls |
| Components | 1 calls |

## How to Explore

1. `gitnexus_context({name: "OrderResponse"})` — see callers and callees
2. `gitnexus_query({query: "controller"})` — find related execution flows
3. Read key files listed above for implementation details
