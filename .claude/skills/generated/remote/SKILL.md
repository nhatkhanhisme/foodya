---
name: remote
description: "Skill for the Remote area of foodya. 6 symbols across 5 files."
---

# Remote

6 symbols | 5 files | Cohesion: 100%

## When to Use

- Working with code in `mobile/`
- Understanding how RefreshRequest, emitLogout, getRefreshTokenBlocking work
- Modifying remote-related functionality

## Key Files

| File | Symbols |
|------|---------|
| `mobile/app/src/main/java/com/example/foodya/data/remote/TokenAuthenticator.kt` | authenticate, responseCount |
| `mobile/app/src/main/java/com/example/foodya/data/local/AuthEventManager.kt` | emitLogout |
| `mobile/app/src/main/java/com/example/foodya/data/local/TokenManager.kt` | getRefreshTokenBlocking |
| `mobile/app/src/main/java/com/example/foodya/data/model/AuthModel.kt` | RefreshRequest |
| `mobile/app/src/main/java/com/example/foodya/data/remote/AuthApi.kt` | refresh |

## Entry Points

Start here when exploring this area:

- **`RefreshRequest`** (Class) — `mobile/app/src/main/java/com/example/foodya/data/model/AuthModel.kt:14`
- **`emitLogout`** (Method) — `mobile/app/src/main/java/com/example/foodya/data/local/AuthEventManager.kt:12`
- **`getRefreshTokenBlocking`** (Method) — `mobile/app/src/main/java/com/example/foodya/data/local/TokenManager.kt:84`
- **`refresh`** (Method) — `mobile/app/src/main/java/com/example/foodya/data/remote/AuthApi.kt:18`
- **`authenticate`** (Method) — `mobile/app/src/main/java/com/example/foodya/data/remote/TokenAuthenticator.kt:20`

## Key Symbols

| Symbol | Type | File | Line |
|--------|------|------|------|
| `RefreshRequest` | Class | `mobile/app/src/main/java/com/example/foodya/data/model/AuthModel.kt` | 14 |
| `emitLogout` | Method | `mobile/app/src/main/java/com/example/foodya/data/local/AuthEventManager.kt` | 12 |
| `getRefreshTokenBlocking` | Method | `mobile/app/src/main/java/com/example/foodya/data/local/TokenManager.kt` | 84 |
| `refresh` | Method | `mobile/app/src/main/java/com/example/foodya/data/remote/AuthApi.kt` | 18 |
| `authenticate` | Method | `mobile/app/src/main/java/com/example/foodya/data/remote/TokenAuthenticator.kt` | 20 |
| `responseCount` | Method | `mobile/app/src/main/java/com/example/foodya/data/remote/TokenAuthenticator.kt` | 77 |

## How to Explore

1. `gitnexus_context({name: "RefreshRequest"})` — see callers and callees
2. `gitnexus_query({query: "remote"})` — find related execution flows
3. Read key files listed above for implementation details
