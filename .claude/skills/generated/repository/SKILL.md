---
name: repository
description: "Skill for the Repository area of foodya. 61 symbols across 31 files."
---

# Repository

61 symbols | 31 files | Cohesion: 87%

## When to Use

- Working with code in `mobile/`
- Understanding how toUserFriendlyMessage, LoginRequest, RegisterRequest work
- Modifying repository-related functionality

## Key Files

| File | Symbols |
|------|---------|
| `mobile/app/src/main/java/com/example/foodya/data/repository/MerchantRepositoryImpl.kt` | createRestaurant, updateRestaurant, toggleRestaurantStatus, updateOrderStatus, createMenuItem (+4) |
| `mobile/app/src/main/java/com/example/foodya/data/repository/RestaurantRepositoryImpl.kt` | getRestaurants, getRestaurantById, getPopularRestaurants, getMenuByRestaurantId, RestaurantRepositoryImpl |
| `mobile/app/src/main/java/com/example/foodya/data/repository/AuthRepositoryImpl.kt` | login, register, changePassword, AuthRepositoryImpl |
| `mobile/app/src/main/java/com/example/foodya/data/repository/StorageRepositoryImpl.kt` | uploadImage, uriToByteArray, getFileExtension, StorageRepositoryImpl |
| `mobile/app/src/main/java/com/example/foodya/domain/repository/MerchantRepository.kt` | createMenuItem, updateMenuItem, MerchantRepository |
| `mobile/app/src/main/java/com/example/foodya/util/NetworkUtils.kt` | toUserFriendlyMessage, parseHttpErrorBody |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/restaurant/repository/RestaurantRepository.java` | existsByName, existsByPhoneNumber |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/restaurant/service/RestaurantService.java` | createRestaurant, updateRestaurant |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/utils/phone/PhoneNumberUtil.java` | normalize, normalize |
| `mobile/app/src/main/java/com/example/foodya/domain/repository/StorageRepository.kt` | uploadImage, StorageRepository |

## Entry Points

Start here when exploring this area:

- **`toUserFriendlyMessage`** (Function) — `mobile/app/src/main/java/com/example/foodya/util/NetworkUtils.kt:6`
- **`LoginRequest`** (Class) — `foodya-backend/src/main/java/com/foodya/foodya_backend/auth/dto/LoginRequest.java:9`
- **`RegisterRequest`** (Class) — `foodya-backend/src/main/java/com/foodya/foodya_backend/auth/dto/RegisterRequest.java:12`
- **`ChangePasswordRequest`** (Class) — `mobile/app/src/main/java/com/example/foodya/data/model/ChangePasswordRequest.kt:2`
- **`UpdateOrderStatusRequest`** (Class) — `mobile/app/src/main/java/com/example/foodya/data/model/OrderResponse.kt:139`

## Key Symbols

| Symbol | Type | File | Line |
|--------|------|------|------|
| `LoginRequest` | Class | `foodya-backend/src/main/java/com/foodya/foodya_backend/auth/dto/LoginRequest.java` | 9 |
| `RegisterRequest` | Class | `foodya-backend/src/main/java/com/foodya/foodya_backend/auth/dto/RegisterRequest.java` | 12 |
| `ChangePasswordRequest` | Class | `mobile/app/src/main/java/com/example/foodya/data/model/ChangePasswordRequest.kt` | 2 |
| `UpdateOrderStatusRequest` | Class | `mobile/app/src/main/java/com/example/foodya/data/model/OrderResponse.kt` | 139 |
| `MenuItemRequest` | Class | `foodya-backend/src/main/java/com/foodya/foodya_backend/restaurant/dto/MenuItemRequest.java` | 9 |
| `AuthRepositoryImpl` | Class | `mobile/app/src/main/java/com/example/foodya/data/repository/AuthRepositoryImpl.kt` | 12 |
| `MerchantRepositoryImpl` | Class | `mobile/app/src/main/java/com/example/foodya/data/repository/MerchantRepositoryImpl.kt` | 11 |
| `OrderRepositoryImpl` | Class | `mobile/app/src/main/java/com/example/foodya/data/repository/OrderRepositoryImpl.kt` | 11 |
| `RestaurantRepositoryImpl` | Class | `mobile/app/src/main/java/com/example/foodya/data/repository/RestaurantRepositoryImpl.kt` | 10 |
| `StorageRepositoryImpl` | Class | `mobile/app/src/main/java/com/example/foodya/data/repository/StorageRepositoryImpl.kt` | 27 |
| `ThemeRepositoryImpl` | Class | `mobile/app/src/main/java/com/example/foodya/data/repository/ThemeRepositoryImpl.kt` | 13 |
| `UserRepositoryImpl` | Class | `mobile/app/src/main/java/com/example/foodya/data/repository/UserRepositoryImpl.kt` | 8 |
| `toUserFriendlyMessage` | Function | `mobile/app/src/main/java/com/example/foodya/util/NetworkUtils.kt` | 6 |
| `AuthRepository` | Interface | `mobile/app/src/main/java/com/example/foodya/domain/repository/AuthRepository.kt` | 5 |
| `MerchantRepository` | Interface | `mobile/app/src/main/java/com/example/foodya/domain/repository/MerchantRepository.kt` | 9 |
| `OrderRepository` | Interface | `mobile/app/src/main/java/com/example/foodya/domain/repository/OrderRepository.kt` | 5 |
| `RestaurantRepository` | Interface | `mobile/app/src/main/java/com/example/foodya/domain/repository/RestaurantRepository.kt` | 5 |
| `StorageRepository` | Interface | `mobile/app/src/main/java/com/example/foodya/domain/repository/StorageRepository.kt` | 11 |
| `ThemeRepository` | Interface | `mobile/app/src/main/java/com/example/foodya/domain/repository/ThemeRepository.kt` | 9 |
| `UserRepository` | Interface | `mobile/app/src/main/java/com/example/foodya/domain/repository/UserRepository.kt` | 4 |

## Execution Flows

| Flow | Type | Steps |
|------|------|-------|
| `OrderHistoryView → ParseHttpErrorBody` | cross_community | 4 |
| `OnUpdateOrderStatus → ParseHttpErrorBody` | cross_community | 4 |
| `OnConfirmCancellation → ParseHttpErrorBody` | cross_community | 4 |
| `CreateRestaurant → FindByUsername` | cross_community | 3 |
| `CreateRestaurant → ResourceNotFoundException` | cross_community | 3 |
| `CreateRestaurant → ExistsByName` | intra_community | 3 |
| `CreateRestaurant → DuplicateResourceException` | intra_community | 3 |
| `CreateRestaurant → Normalize` | intra_community | 3 |
| `CreateRestaurant → ExistsByPhoneNumber` | intra_community | 3 |
| `UpdateRestaurant → UnauthorizedException` | cross_community | 3 |

## Connected Areas

| Area | Connections |
|------|-------------|
| Controller | 4 calls |
| Service | 1 calls |
| Merchant | 1 calls |

## How to Explore

1. `gitnexus_context({name: "toUserFriendlyMessage"})` — see callers and callees
2. `gitnexus_query({query: "repository"})` — find related execution flows
3. Read key files listed above for implementation details
