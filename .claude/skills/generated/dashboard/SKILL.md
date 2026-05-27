---
name: dashboard
description: "Skill for the Dashboard area of foodya. 18 symbols across 9 files."
---

# Dashboard

18 symbols | 9 files | Cohesion: 76%

## When to Use

- Working with code in `mobile/`
- Understanding how MainScreen, RestaurantEditDialog, SetupNavGraph work
- Modifying dashboard-related functionality

## Key Files

| File | Symbols |
|------|---------|
| `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/dashboard/DashboardView.kt` | DashboardView, EmptyRestaurantState, DashboardContent, OrderCard, InfoRow (+3) |
| `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/profile/ProfileView.kt` | MerchantProfileView, MerchantProfileHeader |
| `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/MerchantViewModel.kt` | onRestaurantSelected, onOrderClick |
| `mobile/app/src/main/java/com/example/foodya/MainActivity.kt` | onCreate |
| `mobile/app/src/main/java/com/example/foodya/ui/MainScreen.kt` | MainScreen |
| `mobile/app/src/main/java/com/example/foodya/ui/components/RestaurantEditDialog.kt` | RestaurantEditDialog |
| `mobile/app/src/main/java/com/example/foodya/ui/navigation/NavGraph.kt` | SetupNavGraph |
| `mobile/app/src/main/java/com/example/foodya/ui/theme/ThemeViewModel.kt` | rememberThemePreference |
| `mobile/app/src/main/java/com/example/foodya/domain/model/enums/OrderStatus.kt` | getColor |

## Entry Points

Start here when exploring this area:

- **`MainScreen`** (Function) — `mobile/app/src/main/java/com/example/foodya/ui/MainScreen.kt:23`
- **`RestaurantEditDialog`** (Function) — `mobile/app/src/main/java/com/example/foodya/ui/components/RestaurantEditDialog.kt:16`
- **`SetupNavGraph`** (Function) — `mobile/app/src/main/java/com/example/foodya/ui/navigation/NavGraph.kt:25`
- **`DashboardView`** (Function) — `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/dashboard/DashboardView.kt:31`
- **`MerchantProfileView`** (Function) — `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/profile/ProfileView.kt:30`

## Key Symbols

| Symbol | Type | File | Line |
|--------|------|------|------|
| `MainScreen` | Function | `mobile/app/src/main/java/com/example/foodya/ui/MainScreen.kt` | 23 |
| `RestaurantEditDialog` | Function | `mobile/app/src/main/java/com/example/foodya/ui/components/RestaurantEditDialog.kt` | 16 |
| `SetupNavGraph` | Function | `mobile/app/src/main/java/com/example/foodya/ui/navigation/NavGraph.kt` | 25 |
| `DashboardView` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/dashboard/DashboardView.kt` | 31 |
| `MerchantProfileView` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/profile/ProfileView.kt` | 30 |
| `rememberThemePreference` | Function | `mobile/app/src/main/java/com/example/foodya/ui/theme/ThemeViewModel.kt` | 97 |
| `getColor` | Function | `mobile/app/src/main/java/com/example/foodya/domain/model/enums/OrderStatus.kt` | 12 |
| `onCreate` | Method | `mobile/app/src/main/java/com/example/foodya/MainActivity.kt` | 31 |
| `onRestaurantSelected` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/MerchantViewModel.kt` | 64 |
| `onOrderClick` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/MerchantViewModel.kt` | 613 |
| `EmptyRestaurantState` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/dashboard/DashboardView.kt` | 239 |
| `MerchantProfileHeader` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/profile/ProfileView.kt` | 183 |
| `DashboardContent` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/dashboard/DashboardView.kt` | 278 |
| `OrderCard` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/dashboard/DashboardView.kt` | 490 |
| `InfoRow` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/dashboard/DashboardView.kt` | 611 |
| `OrderStatusBadge` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/dashboard/DashboardView.kt` | 636 |
| `OrderStatusDialog` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/dashboard/DashboardView.kt` | 656 |
| `StatusButton` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/dashboard/DashboardView.kt` | 753 |

## Execution Flows

| Flow | Type | Steps |
|------|------|-------|
| `OnCreate → GetColor` | cross_community | 7 |
| `OnCreate → OnOrderClick` | cross_community | 6 |
| `OnCreate → MenuItemCard` | cross_community | 6 |
| `OnCreate → OnEditItem` | cross_community | 6 |
| `OnCreate → OnDeleteItem` | cross_community | 6 |
| `OnCreate → Clear` | cross_community | 6 |
| `OnCreate → RestaurantEditDialog` | intra_community | 5 |
| `OnCreate → EmptyRestaurantState` | intra_community | 5 |
| `OnCreate → MenuItemDialog` | cross_community | 5 |
| `OnCreate → EmptyRestaurantState` | cross_community | 5 |

## Connected Areas

| Area | Connections |
|------|-------------|
| Merchant | 2 calls |
| Menu | 1 calls |
| Local | 1 calls |

## How to Explore

1. `gitnexus_context({name: "MainScreen"})` — see callers and callees
2. `gitnexus_query({query: "dashboard"})` — find related execution flows
3. Read key files listed above for implementation details
