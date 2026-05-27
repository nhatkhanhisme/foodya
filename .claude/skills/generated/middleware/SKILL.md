---
name: middleware
description: "Skill for the Middleware area of foodya. 7 symbols across 3 files."
---

# Middleware

7 symbols | 3 files | Cohesion: 80%

## When to Use

- Working with code in `foodya-backend/`
- Understanding how testAuth, doFilterInternal, getJwtTokenFromRequest work
- Modifying middleware-related functionality

## Key Files

| File | Symbols |
|------|---------|
| `foodya-backend/src/main/java/com/foodya/foodya_backend/middleware/JwtAuthenticationFilter.java` | doFilterInternal, getJwtTokenFromRequest, writeUnauthorized |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/user/model/User.java` | getAuthorities, isAccountNonLocked, isEnabled |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/merchant/controller/MerchantTestController.java` | testAuth |

## Entry Points

Start here when exploring this area:

- **`testAuth`** (Method) — `foodya-backend/src/main/java/com/foodya/foodya_backend/merchant/controller/MerchantTestController.java:24`
- **`doFilterInternal`** (Method) — `foodya-backend/src/main/java/com/foodya/foodya_backend/middleware/JwtAuthenticationFilter.java:37`
- **`getJwtTokenFromRequest`** (Method) — `foodya-backend/src/main/java/com/foodya/foodya_backend/middleware/JwtAuthenticationFilter.java:112`
- **`writeUnauthorized`** (Method) — `foodya-backend/src/main/java/com/foodya/foodya_backend/middleware/JwtAuthenticationFilter.java:128`
- **`getAuthorities`** (Method) — `foodya-backend/src/main/java/com/foodya/foodya_backend/user/model/User.java:84`

## Key Symbols

| Symbol | Type | File | Line |
|--------|------|------|------|
| `testAuth` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/merchant/controller/MerchantTestController.java` | 24 |
| `doFilterInternal` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/middleware/JwtAuthenticationFilter.java` | 37 |
| `getJwtTokenFromRequest` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/middleware/JwtAuthenticationFilter.java` | 112 |
| `writeUnauthorized` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/middleware/JwtAuthenticationFilter.java` | 128 |
| `getAuthorities` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/user/model/User.java` | 84 |
| `isAccountNonLocked` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/user/model/User.java` | 96 |
| `isEnabled` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/user/model/User.java` | 108 |

## Execution Flows

| Flow | Type | Steps |
|------|------|-------|
| `DoFilterInternal → Key` | cross_community | 4 |

## Connected Areas

| Area | Connections |
|------|-------------|
| Jwt | 2 calls |
| Service | 1 calls |

## How to Explore

1. `gitnexus_context({name: "testAuth"})` — see callers and callees
2. `gitnexus_query({query: "middleware"})` — find related execution flows
3. Read key files listed above for implementation details
