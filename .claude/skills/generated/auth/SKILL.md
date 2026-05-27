---
name: auth
description: "Skill for the Auth area of foodya. 6 symbols across 3 files."
---

# Auth

6 symbols | 3 files | Cohesion: 100%

## When to Use

- Working with code in `mobile/`
- Understanding how AuthView, saveAuthResponse, onRoleChange work
- Modifying auth-related functionality

## Key Files

| File | Symbols |
|------|---------|
| `mobile/app/src/main/java/com/example/foodya/ui/screen/auth/AuthViewModel.kt` | onRoleChange, submit, login, register |
| `mobile/app/src/main/java/com/example/foodya/data/local/TokenManager.kt` | saveAuthResponse |
| `mobile/app/src/main/java/com/example/foodya/ui/screen/auth/AuthView.kt` | AuthView |

## Entry Points

Start here when exploring this area:

- **`AuthView`** (Function) — `mobile/app/src/main/java/com/example/foodya/ui/screen/auth/AuthView.kt:34`
- **`saveAuthResponse`** (Method) — `mobile/app/src/main/java/com/example/foodya/data/local/TokenManager.kt:38`
- **`onRoleChange`** (Method) — `mobile/app/src/main/java/com/example/foodya/ui/screen/auth/AuthViewModel.kt:49`
- **`submit`** (Method) — `mobile/app/src/main/java/com/example/foodya/ui/screen/auth/AuthViewModel.kt:59`

## Key Symbols

| Symbol | Type | File | Line |
|--------|------|------|------|
| `AuthView` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/auth/AuthView.kt` | 34 |
| `saveAuthResponse` | Method | `mobile/app/src/main/java/com/example/foodya/data/local/TokenManager.kt` | 38 |
| `onRoleChange` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/auth/AuthViewModel.kt` | 49 |
| `submit` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/auth/AuthViewModel.kt` | 59 |
| `login` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/auth/AuthViewModel.kt` | 65 |
| `register` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/auth/AuthViewModel.kt` | 108 |

## Execution Flows

| Flow | Type | Steps |
|------|------|-------|
| `AuthView → SaveAuthResponse` | intra_community | 4 |

## How to Explore

1. `gitnexus_context({name: "AuthView"})` — see callers and callees
2. `gitnexus_query({query: "auth"})` — find related execution flows
3. Read key files listed above for implementation details
