---
name: local
description: "Skill for the Local area of foodya. 10 symbols across 6 files."
---

# Local

10 symbols | 6 files | Cohesion: 95%

## When to Use

- Working with code in `mobile/`
- Understanding how CustomerProfileView, ProfileHeader, clear work
- Modifying local-related functionality

## Key Files

| File | Symbols |
|------|---------|
| `mobile/app/src/main/java/com/example/foodya/data/local/TokenManager.kt` | clear, getRoleBlocking |
| `mobile/app/src/main/java/com/example/foodya/data/local/UserSessionManager.kt` | clearSession, getCurrentRoleBlocking |
| `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/profile/ProfileView.kt` | CustomerProfileView, ProfileHeader |
| `mobile/app/src/main/java/com/example/foodya/data/local/ThemeManager.kt` | setDarkMode, toggleDarkMode |
| `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/CustomerViewModel.kt` | onLogout |
| `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/MerchantViewModel.kt` | onMerchantLogout |

## Entry Points

Start here when exploring this area:

- **`CustomerProfileView`** (Function) — `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/profile/ProfileView.kt:28`
- **`ProfileHeader`** (Function) — `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/profile/ProfileView.kt:124`
- **`clear`** (Method) — `mobile/app/src/main/java/com/example/foodya/data/local/TokenManager.kt:80`
- **`clearSession`** (Method) — `mobile/app/src/main/java/com/example/foodya/data/local/UserSessionManager.kt:89`
- **`onLogout`** (Method) — `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/CustomerViewModel.kt:616`

## Key Symbols

| Symbol | Type | File | Line |
|--------|------|------|------|
| `CustomerProfileView` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/profile/ProfileView.kt` | 28 |
| `ProfileHeader` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/profile/ProfileView.kt` | 124 |
| `clear` | Method | `mobile/app/src/main/java/com/example/foodya/data/local/TokenManager.kt` | 80 |
| `clearSession` | Method | `mobile/app/src/main/java/com/example/foodya/data/local/UserSessionManager.kt` | 89 |
| `onLogout` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/customer/CustomerViewModel.kt` | 616 |
| `onMerchantLogout` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/merchant/MerchantViewModel.kt` | 998 |
| `setDarkMode` | Method | `mobile/app/src/main/java/com/example/foodya/data/local/ThemeManager.kt` | 49 |
| `toggleDarkMode` | Method | `mobile/app/src/main/java/com/example/foodya/data/local/ThemeManager.kt` | 59 |
| `getRoleBlocking` | Method | `mobile/app/src/main/java/com/example/foodya/data/local/TokenManager.kt` | 90 |
| `getCurrentRoleBlocking` | Method | `mobile/app/src/main/java/com/example/foodya/data/local/UserSessionManager.kt` | 75 |

## Execution Flows

| Flow | Type | Steps |
|------|------|-------|
| `OnCreate → Clear` | cross_community | 6 |
| `CustomerProfileView → Clear` | intra_community | 3 |

## How to Explore

1. `gitnexus_context({name: "CustomerProfileView"})` — see callers and callees
2. `gitnexus_query({query: "local"})` — find related execution flows
3. Read key files listed above for implementation details
