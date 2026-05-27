---
name: merchant
description: "Skill for the Merchant area of foodya. 20 symbols across 2 files."
---

# Merchant

20 symbols | 2 files | Cohesion: 70%

## When to Use

- Working with code in `mobile/`
- Understanding how getMenuItems, deleteMenuItem, onConfirmDelete work
- Modifying merchant-related functionality

## Key Files

| File | Symbols |
|------|---------|
| `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/MerchantViewModel.kt` | loadMenuData, loadMenuItems, onConfirmDelete, onMenuRetry, loadDashboard (+8) |
| `mobile/app/src/main/java/com/example/foodya/domain/repository/MerchantRepository.kt` | getMenuItems, deleteMenuItem, getMyRestaurants, getOrdersByRestaurant, createRestaurant (+2) |

## Entry Points

Start here when exploring this area:

- **`getMenuItems`** (Method) — `mobile/app/src/main/java/com/example/foodya/domain/repository/MerchantRepository.kt:57`
- **`deleteMenuItem`** (Method) — `mobile/app/src/main/java/com/example/foodya/domain/repository/MerchantRepository.kt:81`
- **`onConfirmDelete`** (Method) — `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/MerchantViewModel.kt:922`
- **`onMenuRetry`** (Method) — `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/MerchantViewModel.kt:952`
- **`getMyRestaurants`** (Method) — `mobile/app/src/main/java/com/example/foodya/domain/repository/MerchantRepository.kt:15`

## Key Symbols

| Symbol | Type | File | Line |
|--------|------|------|------|
| `getMenuItems` | Method | `mobile/app/src/main/java/com/example/foodya/domain/repository/MerchantRepository.kt` | 57 |
| `deleteMenuItem` | Method | `mobile/app/src/main/java/com/example/foodya/domain/repository/MerchantRepository.kt` | 81 |
| `onConfirmDelete` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/MerchantViewModel.kt` | 922 |
| `onMenuRetry` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/MerchantViewModel.kt` | 952 |
| `getMyRestaurants` | Method | `mobile/app/src/main/java/com/example/foodya/domain/repository/MerchantRepository.kt` | 15 |
| `getOrdersByRestaurant` | Method | `mobile/app/src/main/java/com/example/foodya/domain/repository/MerchantRepository.kt` | 41 |
| `onDashboardRetry` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/MerchantViewModel.kt` | 701 |
| `createRestaurant` | Method | `mobile/app/src/main/java/com/example/foodya/domain/repository/MerchantRepository.kt` | 21 |
| `updateRestaurant` | Method | `mobile/app/src/main/java/com/example/foodya/domain/repository/MerchantRepository.kt` | 26 |
| `saveRestaurantChanges` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/MerchantViewModel.kt` | 147 |
| `createNewRestaurant` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/MerchantViewModel.kt` | 443 |
| `updateOrderStatus` | Method | `mobile/app/src/main/java/com/example/foodya/domain/repository/MerchantRepository.kt` | 46 |
| `onUpdateOrderStatus` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/MerchantViewModel.kt` | 625 |
| `onConfirmCancellation` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/MerchantViewModel.kt` | 636 |
| `loadMenuData` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/MerchantViewModel.kt` | 707 |
| `loadMenuItems` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/MerchantViewModel.kt` | 720 |
| `loadDashboard` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/MerchantViewModel.kt` | 534 |
| `loadOrders` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/MerchantViewModel.kt` | 592 |
| `validateRestaurantData` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/MerchantViewModel.kt` | 227 |
| `updateOrderStatusInternal` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/MerchantViewModel.kt` | 648 |

## Execution Flows

| Flow | Type | Steps |
|------|------|-------|
| `DashboardView → GetMenuItems` | cross_community | 5 |
| `DashboardView → GetOrdersByRestaurant` | cross_community | 5 |
| `OnUpdateOrderStatus → GetOrdersByRestaurant` | cross_community | 4 |
| `OnUpdateOrderStatus → ParseHttpErrorBody` | cross_community | 4 |
| `OnConfirmCancellation → GetOrdersByRestaurant` | cross_community | 4 |
| `OnConfirmCancellation → ParseHttpErrorBody` | cross_community | 4 |
| `OnDashboardRetry → GetOrdersByRestaurant` | intra_community | 4 |
| `OnDashboardRetry → GetMenuItems` | cross_community | 4 |
| `OnMenuRetry → GetMenuItems` | intra_community | 4 |
| `CreateNewRestaurant → GetMenuItems` | cross_community | 3 |

## Connected Areas

| Area | Connections |
|------|-------------|
| Repository | 3 calls |

## How to Explore

1. `gitnexus_context({name: "getMenuItems"})` — see callers and callees
2. `gitnexus_query({query: "merchant"})` — find related execution flows
3. Read key files listed above for implementation details
