---
name: service
description: "Skill for the Service area of foodya. 53 symbols across 21 files."
---

# Service

53 symbols | 21 files | Cohesion: 82%

## When to Use

- Working with code in `foodya-backend/`
- Understanding how getAllCategories, deleteCategory, getAllMenuItems work
- Modifying service-related functionality

## Key Files

| File | Symbols |
|------|---------|
| `foodya-backend/src/main/java/com/foodya/foodya_backend/merchant/controller/MerchantMenuItemController.java` | getAllMenuItems, getActiveMenuItems, createMenuItem, updateMenuItem, deleteMenuItem (+1) |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/restaurant/service/MenuItemService.java` | createMenuItem, updateMenuItem, softDeleteMenuItem, getMenuItemById, getActiveMenuItemsByRestaurant (+1) |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/restaurant/service/OwnershipService.java` | getCurrentUser, isRestaurantOwner, isMenuItemOwner, isAdmin |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/user/service/UserService.java` | getAllUsers, toggleUserActiveStatus, getCurrentUserProfile, mapToUserProfileResponse |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/user/service/CustomUserDetailsService.java` | loadUserByUsername, loadUserByEmail, loadUserById, buildUserDetails |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/restaurant/dto/MenuItemMapper.java` | toMenuItemResponse, toMenuItem, updateMenuItemFromRequest |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/order/service/OrderService.java` | getMyOrders, getMyActiveOrders, getCurrentUser |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/auth/service/AuthService.java` | generateTokenResponse, getExpireIn, getRefreshTokenExpireIn |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/merchant/controller/MerchantCategoryController.java` | getAllCategories, deleteCategory |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/restaurant/repository/MenuItemRepository.java` | findByRestaurantIdAndIsActiveTrue, existsByNameAndRestaurantId |

## Entry Points

Start here when exploring this area:

- **`getAllCategories`** (Method) — `foodya-backend/src/main/java/com/foodya/foodya_backend/merchant/controller/MerchantCategoryController.java:55`
- **`deleteCategory`** (Method) — `foodya-backend/src/main/java/com/foodya/foodya_backend/merchant/controller/MerchantCategoryController.java:92`
- **`getAllMenuItems`** (Method) — `foodya-backend/src/main/java/com/foodya/foodya_backend/merchant/controller/MerchantMenuItemController.java:38`
- **`getActiveMenuItems`** (Method) — `foodya-backend/src/main/java/com/foodya/foodya_backend/merchant/controller/MerchantMenuItemController.java:64`
- **`createMenuItem`** (Method) — `foodya-backend/src/main/java/com/foodya/foodya_backend/merchant/controller/MerchantMenuItemController.java:90`

## Key Symbols

| Symbol | Type | File | Line |
|--------|------|------|------|
| `getAllCategories` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/merchant/controller/MerchantCategoryController.java` | 55 |
| `deleteCategory` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/merchant/controller/MerchantCategoryController.java` | 92 |
| `getAllMenuItems` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/merchant/controller/MerchantMenuItemController.java` | 38 |
| `getActiveMenuItems` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/merchant/controller/MerchantMenuItemController.java` | 64 |
| `createMenuItem` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/merchant/controller/MerchantMenuItemController.java` | 90 |
| `updateMenuItem` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/merchant/controller/MerchantMenuItemController.java` | 121 |
| `deleteMenuItem` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/merchant/controller/MerchantMenuItemController.java` | 150 |
| `toggleAvailability` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/merchant/controller/MerchantMenuItemController.java` | 177 |
| `getMenuItemById` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/middleware/controller/MenuItemController.java` | 72 |
| `getMenuItemById` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/restaurant/controller/MenuItemController.java` | 72 |
| `toMenuItemResponse` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/restaurant/dto/MenuItemMapper.java` | 11 |
| `toMenuItem` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/restaurant/dto/MenuItemMapper.java` | 45 |
| `updateMenuItemFromRequest` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/restaurant/dto/MenuItemMapper.java` | 66 |
| `findAllByRestaurantId` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/restaurant/repository/CategoryRepository.java` | 19 |
| `findByRestaurantIdAndIsActiveTrue` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/restaurant/repository/MenuItemRepository.java` | 21 |
| `existsByNameAndRestaurantId` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/restaurant/repository/MenuItemRepository.java` | 86 |
| `deleteCategory` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/restaurant/service/CategoryService.java` | 112 |
| `getAllCategoriesByRestaurant` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/restaurant/service/CategoryService.java` | 139 |
| `createMenuItem` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/restaurant/service/MenuItemService.java` | 37 |
| `updateMenuItem` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/restaurant/service/MenuItemService.java` | 66 |

## Execution Flows

| Flow | Type | Steps |
|------|------|-------|
| `Login → Key` | cross_community | 7 |
| `CreateCategory → FindByUsername` | cross_community | 5 |
| `CreateCategory → ResourceNotFoundException` | cross_community | 5 |
| `UpdateCategory → FindByUsername` | cross_community | 5 |
| `UpdateCategory → ResourceNotFoundException` | cross_community | 5 |
| `DeleteCategory → FindByUsername` | cross_community | 5 |
| `DeleteCategory → ResourceNotFoundException` | intra_community | 5 |
| `GetAllCategories → FindByUsername` | cross_community | 5 |
| `GetAllCategories → ResourceNotFoundException` | intra_community | 5 |
| `GetMyOrders → OrderItemResponse` | cross_community | 5 |

## Connected Areas

| Area | Connections |
|------|-------------|
| Controller | 10 calls |
| Jwt | 3 calls |

## How to Explore

1. `gitnexus_context({name: "getAllCategories"})` — see callers and callees
2. `gitnexus_query({query: "service"})` — find related execution flows
3. Read key files listed above for implementation details
