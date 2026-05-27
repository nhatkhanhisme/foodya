---
name: menu
description: "Skill for the Menu area of foodya. 7 symbols across 4 files."
---

# Menu

7 symbols | 4 files | Cohesion: 92%

## When to Use

- Working with code in `mobile/`
- Understanding how MenuView, MenuItemCard, MenuItemDialog work
- Modifying menu-related functionality

## Key Files

| File | Symbols |
|------|---------|
| `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/menu/MenuView.kt` | MenuView, EmptyRestaurantState, MenuContent |
| `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/MerchantViewModel.kt` | onEditItem, onDeleteItem |
| `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/menu/components/MenuItemCard.kt` | MenuItemCard |
| `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/menu/components/MenuItemDialog.kt` | MenuItemDialog |

## Entry Points

Start here when exploring this area:

- **`MenuView`** (Function) — `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/menu/MenuView.kt:20`
- **`MenuItemCard`** (Function) — `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/menu/components/MenuItemCard.kt:25`
- **`MenuItemDialog`** (Function) — `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/menu/components/MenuItemDialog.kt:12`
- **`onEditItem`** (Method) — `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/MerchantViewModel.kt:749`
- **`onDeleteItem`** (Method) — `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/MerchantViewModel.kt:765`

## Key Symbols

| Symbol | Type | File | Line |
|--------|------|------|------|
| `MenuView` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/menu/MenuView.kt` | 20 |
| `MenuItemCard` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/menu/components/MenuItemCard.kt` | 25 |
| `MenuItemDialog` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/menu/components/MenuItemDialog.kt` | 12 |
| `onEditItem` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/MerchantViewModel.kt` | 749 |
| `onDeleteItem` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/MerchantViewModel.kt` | 765 |
| `EmptyRestaurantState` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/menu/MenuView.kt` | 154 |
| `MenuContent` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/menu/MenuView.kt` | 184 |

## Execution Flows

| Flow | Type | Steps |
|------|------|-------|
| `OnCreate → MenuItemCard` | cross_community | 6 |
| `OnCreate → OnEditItem` | cross_community | 6 |
| `OnCreate → OnDeleteItem` | cross_community | 6 |
| `OnCreate → MenuItemDialog` | cross_community | 5 |
| `OnCreate → EmptyRestaurantState` | cross_community | 5 |

## How to Explore

1. `gitnexus_context({name: "MenuView"})` — see callers and callees
2. `gitnexus_query({query: "menu"})` — find related execution flows
3. Read key files listed above for implementation details
