---
name: customer
description: "Skill for the Customer area of foodya. 29 symbols across 12 files."
---

# Customer

29 symbols | 12 files | Cohesion: 77%

## When to Use

- Working with code in `mobile/`
- Understanding how OrderHistoryView, FoodItemCard, SectionTitle work
- Modifying customer-related functionality

## Key Files

| File | Symbols |
|------|---------|
| `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/CustomerViewModel.kt` | loadOrders, onStatusSelected, filterOrders, cancelOrder, refreshOrders (+11) |
| `mobile/app/src/main/java/com/example/foodya/domain/repository/OrderRepository.kt` | getMyOrders, createOrder |
| `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/restaurant/RestaurantDetailViewModel.kt` | placeOrder, addItem |
| `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/order/OrderHistoryView.kt` | OrderHistoryView |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/order/dto/OrderItemRequest.java` | OrderItemRequest |
| `mobile/app/src/main/java/com/example/foodya/data/model/OrderRequest.kt` | OrderRequest |
| `mobile/app/src/main/java/com/example/foodya/ui/components/FoodItemCard.kt` | FoodItemCard |
| `mobile/app/src/main/java/com/example/foodya/ui/components/SectionTitle.kt` | SectionTitle |
| `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/home/HomeView.kt` | HomeView |
| `mobile/app/src/main/java/com/example/foodya/domain/model/CartItem.kt` | CartItem |

## Entry Points

Start here when exploring this area:

- **`OrderHistoryView`** (Function) — `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/order/OrderHistoryView.kt:26`
- **`FoodItemCard`** (Function) — `mobile/app/src/main/java/com/example/foodya/ui/components/FoodItemCard.kt:22`
- **`SectionTitle`** (Function) — `mobile/app/src/main/java/com/example/foodya/ui/components/SectionTitle.kt:10`
- **`HomeView`** (Function) — `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/home/HomeView.kt:35`
- **`RestaurantCard`** (Function) — `mobile/app/src/main/java/com/example/foodya/ui/components/RestaurantCard.kt:27`

## Key Symbols

| Symbol | Type | File | Line |
|--------|------|------|------|
| `OrderItemRequest` | Class | `foodya-backend/src/main/java/com/foodya/foodya_backend/order/dto/OrderItemRequest.java` | 11 |
| `OrderRequest` | Class | `mobile/app/src/main/java/com/example/foodya/data/model/OrderRequest.kt` | 2 |
| `CartItem` | Class | `mobile/app/src/main/java/com/example/foodya/domain/model/CartItem.kt` | 2 |
| `OrderHistoryView` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/order/OrderHistoryView.kt` | 26 |
| `FoodItemCard` | Function | `mobile/app/src/main/java/com/example/foodya/ui/components/FoodItemCard.kt` | 22 |
| `SectionTitle` | Function | `mobile/app/src/main/java/com/example/foodya/ui/components/SectionTitle.kt` | 10 |
| `HomeView` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/home/HomeView.kt` | 35 |
| `RestaurantCard` | Function | `mobile/app/src/main/java/com/example/foodya/ui/components/RestaurantCard.kt` | 27 |
| `SearchResultView` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/search/SearchResultView.kt` | 18 |
| `getMyOrders` | Method | `mobile/app/src/main/java/com/example/foodya/domain/repository/OrderRepository.kt` | 7 |
| `onStatusSelected` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/CustomerViewModel.kt` | 502 |
| `cancelOrder` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/CustomerViewModel.kt` | 514 |
| `refreshOrders` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/CustomerViewModel.kt` | 569 |
| `createOrder` | Method | `mobile/app/src/main/java/com/example/foodya/domain/repository/OrderRepository.kt` | 6 |
| `placeOrder` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/CustomerViewModel.kt` | 327 |
| `placeOrder` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/restaurant/RestaurantDetailViewModel.kt` | 276 |
| `onSearchActiveChange` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/CustomerViewModel.kt` | 175 |
| `onClearSearch` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/CustomerViewModel.kt` | 179 |
| `onFoodSelected` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/CustomerViewModel.kt` | 183 |
| `addToCart` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/CustomerViewModel.kt` | 191 |

## Execution Flows

| Flow | Type | Steps |
|------|------|-------|
| `HomeView → Format` | cross_community | 5 |
| `OrderHistoryView → GetMyOrders` | intra_community | 4 |
| `OrderHistoryView → FilterOrders` | intra_community | 4 |
| `OrderHistoryView → Format` | cross_community | 4 |
| `OrderHistoryView → ParseHttpErrorBody` | cross_community | 4 |
| `PlaceOrder → CartSummary` | cross_community | 4 |
| `AddItem → CartSummary` | cross_community | 4 |
| `SearchResultView → PerformSearch` | intra_community | 3 |

## Connected Areas

| Area | Connections |
|------|-------------|
| Components | 4 calls |
| Repository | 3 calls |
| Restaurant | 2 calls |

## How to Explore

1. `gitnexus_context({name: "OrderHistoryView"})` — see callers and callees
2. `gitnexus_query({query: "customer"})` — find related execution flows
3. Read key files listed above for implementation details
