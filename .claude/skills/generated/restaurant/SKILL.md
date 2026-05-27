---
name: restaurant
description: "Skill for the Restaurant area of foodya. 12 symbols across 5 files."
---

# Restaurant

12 symbols | 5 files | Cohesion: 80%

## When to Use

- Working with code in `mobile/`
- Understanding how FoodItemRow, CartSummary, Success work
- Modifying restaurant-related functionality

## Key Files

| File | Symbols |
|------|---------|
| `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/restaurant/RestaurantDetailViewModel.kt` | removeItem, clearCart, calculateCartSummary, updateCartInSuccessState |
| `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/restaurant/RestaurantDetailView.kt` | SuccessContent, RestaurantHeader, RestaurantInfoCard, CategoryHeader |
| `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/restaurant/RestaurantUiState.kt` | RestaurantUiState, Success |
| `mobile/app/src/main/java/com/example/foodya/domain/model/CartSummary.kt` | CartSummary |
| `mobile/app/src/main/java/com/example/foodya/ui/components/FoodItemRow.kt` | FoodItemRow |

## Entry Points

Start here when exploring this area:

- **`FoodItemRow`** (Function) — `mobile/app/src/main/java/com/example/foodya/ui/components/FoodItemRow.kt:35`
- **`CartSummary`** (Class) — `mobile/app/src/main/java/com/example/foodya/domain/model/CartSummary.kt:2`
- **`Success`** (Class) — `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/restaurant/RestaurantUiState.kt:30`
- **`removeItem`** (Method) — `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/restaurant/RestaurantDetailViewModel.kt:192`
- **`clearCart`** (Method) — `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/restaurant/RestaurantDetailViewModel.kt:216`

## Key Symbols

| Symbol | Type | File | Line |
|--------|------|------|------|
| `CartSummary` | Class | `mobile/app/src/main/java/com/example/foodya/domain/model/CartSummary.kt` | 2 |
| `Success` | Class | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/restaurant/RestaurantUiState.kt` | 30 |
| `FoodItemRow` | Function | `mobile/app/src/main/java/com/example/foodya/ui/components/FoodItemRow.kt` | 35 |
| `RestaurantUiState` | Interface | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/restaurant/RestaurantUiState.kt` | 16 |
| `removeItem` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/restaurant/RestaurantDetailViewModel.kt` | 192 |
| `clearCart` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/restaurant/RestaurantDetailViewModel.kt` | 216 |
| `SuccessContent` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/restaurant/RestaurantDetailView.kt` | 241 |
| `RestaurantHeader` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/restaurant/RestaurantDetailView.kt` | 307 |
| `RestaurantInfoCard` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/restaurant/RestaurantDetailView.kt` | 355 |
| `CategoryHeader` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/restaurant/RestaurantDetailView.kt` | 432 |
| `calculateCartSummary` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/restaurant/RestaurantDetailViewModel.kt` | 225 |
| `updateCartInSuccessState` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/restaurant/RestaurantDetailViewModel.kt` | 234 |

## Execution Flows

| Flow | Type | Steps |
|------|------|-------|
| `PlaceOrder → CartSummary` | cross_community | 4 |
| `AddItem → CartSummary` | cross_community | 4 |
| `SuccessContent → Format` | cross_community | 4 |
| `RemoveItem → CartSummary` | intra_community | 4 |
| `ClearCart → CartSummary` | intra_community | 4 |
| `Retry → CartSummary` | cross_community | 4 |

## Connected Areas

| Area | Connections |
|------|-------------|
| Components | 1 calls |

## How to Explore

1. `gitnexus_context({name: "FoodItemRow"})` — see callers and callees
2. `gitnexus_query({query: "restaurant"})` — find related execution flows
3. Read key files listed above for implementation details
