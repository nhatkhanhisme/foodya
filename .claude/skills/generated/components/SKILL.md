---
name: components
description: "Skill for the Components area of foodya. 20 symbols across 11 files."
---

# Components

20 symbols | 11 files | Cohesion: 70%

## When to Use

- Working with code in `mobile/`
- Understanding how BottomCartSummaryBox, FoodDetailPopup, RestaurantDetailView work
- Modifying components-related functionality

## Key Files

| File | Symbols |
|------|---------|
| `mobile/app/src/main/java/com/example/foodya/ui/components/OrderDetailDialog.kt` | OrderDetailDialog, InfoRow, OrderDetailItemRow, PriceDetailRow |
| `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/restaurant/RestaurantDetailView.kt` | RestaurantDetailView, LoadingState, ErrorState |
| `mobile/app/src/main/java/com/example/foodya/ui/components/CheckoutDialog.kt` | CheckoutDialog, OrderItemRow, PriceRow |
| `mobile/app/src/main/java/com/example/foodya/ui/components/BottomCartSummaryBox.kt` | BottomCartSummaryBox, buildCartItemsText |
| `mobile/app/src/main/java/com/example/foodya/ui/components/OrderItemCard.kt` | OrderItemCard, formatPrice |
| `mobile/app/src/main/java/com/example/foodya/ui/components/FoodDetailPopup.kt` | FoodDetailPopup |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/order/dto/OrderResponse.java` | formatDate |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/utils/phone/PhoneNumberUtil.java` | format |
| `mobile/app/src/main/java/com/example/foodya/util/Extensions.kt` | toCurrency |
| `mobile/app/src/main/java/com/example/foodya/ui/components/ImagePickerBox.kt` | ImagePickerBox |

## Entry Points

Start here when exploring this area:

- **`BottomCartSummaryBox`** (Function) — `mobile/app/src/main/java/com/example/foodya/ui/components/BottomCartSummaryBox.kt:40`
- **`FoodDetailPopup`** (Function) — `mobile/app/src/main/java/com/example/foodya/ui/components/FoodDetailPopup.kt:18`
- **`RestaurantDetailView`** (Function) — `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/restaurant/RestaurantDetailView.kt:40`
- **`OrderItemCard`** (Function) — `mobile/app/src/main/java/com/example/foodya/ui/components/OrderItemCard.kt:24`
- **`CheckoutDialog`** (Function) — `mobile/app/src/main/java/com/example/foodya/ui/components/CheckoutDialog.kt:20`

## Key Symbols

| Symbol | Type | File | Line |
|--------|------|------|------|
| `BottomCartSummaryBox` | Function | `mobile/app/src/main/java/com/example/foodya/ui/components/BottomCartSummaryBox.kt` | 40 |
| `FoodDetailPopup` | Function | `mobile/app/src/main/java/com/example/foodya/ui/components/FoodDetailPopup.kt` | 18 |
| `RestaurantDetailView` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/restaurant/RestaurantDetailView.kt` | 40 |
| `OrderItemCard` | Function | `mobile/app/src/main/java/com/example/foodya/ui/components/OrderItemCard.kt` | 24 |
| `CheckoutDialog` | Function | `mobile/app/src/main/java/com/example/foodya/ui/components/CheckoutDialog.kt` | 20 |
| `toCurrency` | Function | `mobile/app/src/main/java/com/example/foodya/util/Extensions.kt` | 5 |
| `OrderDetailDialog` | Function | `mobile/app/src/main/java/com/example/foodya/ui/components/OrderDetailDialog.kt` | 26 |
| `ImagePickerBox` | Function | `mobile/app/src/main/java/com/example/foodya/ui/components/ImagePickerBox.kt` | 43 |
| `formatDate` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/order/dto/OrderResponse.java` | 164 |
| `format` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/utils/phone/PhoneNumberUtil.java` | 115 |
| `onImageSelected` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/MerchantViewModel.kt` | 817 |
| `buildCartItemsText` | Function | `mobile/app/src/main/java/com/example/foodya/ui/components/BottomCartSummaryBox.kt` | 169 |
| `LoadingState` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/restaurant/RestaurantDetailView.kt` | 159 |
| `ErrorState` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/restaurant/RestaurantDetailView.kt` | 187 |
| `formatPrice` | Function | `mobile/app/src/main/java/com/example/foodya/ui/components/OrderItemCard.kt` | 218 |
| `OrderItemRow` | Function | `mobile/app/src/main/java/com/example/foodya/ui/components/CheckoutDialog.kt` | 267 |
| `PriceRow` | Function | `mobile/app/src/main/java/com/example/foodya/ui/components/CheckoutDialog.kt` | 299 |
| `InfoRow` | Function | `mobile/app/src/main/java/com/example/foodya/ui/components/OrderDetailDialog.kt` | 321 |
| `OrderDetailItemRow` | Function | `mobile/app/src/main/java/com/example/foodya/ui/components/OrderDetailDialog.kt` | 346 |
| `PriceDetailRow` | Function | `mobile/app/src/main/java/com/example/foodya/ui/components/OrderDetailDialog.kt` | 386 |

## Execution Flows

| Flow | Type | Steps |
|------|------|-------|
| `HomeView → Format` | cross_community | 5 |
| `RestaurantDetailView → Format` | cross_community | 5 |
| `OrderHistoryView → Format` | cross_community | 4 |
| `OrderDetailDialog → Format` | cross_community | 4 |
| `SuccessContent → Format` | cross_community | 4 |
| `RestaurantDetailView → BuildCartItemsText` | intra_community | 3 |

## Connected Areas

| Area | Connections |
|------|-------------|
| Restaurant | 1 calls |

## How to Explore

1. `gitnexus_context({name: "BottomCartSummaryBox"})` — see callers and callees
2. `gitnexus_query({query: "components"})` — find related execution flows
3. Read key files listed above for implementation details
