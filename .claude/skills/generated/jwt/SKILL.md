---
name: jwt
description: "Skill for the Jwt area of foodya. 8 symbols across 3 files."
---

# Jwt

8 symbols | 3 files | Cohesion: 69%

## When to Use

- Working with code in `foodya-backend/`
- Understanding how refreshToken, refreshToken, key work
- Modifying jwt-related functionality

## Key Files

| File | Symbols |
|------|---------|
| `foodya-backend/src/main/java/com/foodya/foodya_backend/jwt/JwtService.java` | key, generateToken, extractClaims, validateToken, extractUsername (+1) |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/auth/controller/AuthController.java` | refreshToken |
| `foodya-backend/src/main/java/com/foodya/foodya_backend/auth/service/AuthService.java` | refreshToken |

## Entry Points

Start here when exploring this area:

- **`refreshToken`** (Method) — `foodya-backend/src/main/java/com/foodya/foodya_backend/auth/controller/AuthController.java:93`
- **`refreshToken`** (Method) — `foodya-backend/src/main/java/com/foodya/foodya_backend/auth/service/AuthService.java:108`
- **`key`** (Method) — `foodya-backend/src/main/java/com/foodya/foodya_backend/jwt/JwtService.java:31`
- **`generateToken`** (Method) — `foodya-backend/src/main/java/com/foodya/foodya_backend/jwt/JwtService.java:36`
- **`extractClaims`** (Method) — `foodya-backend/src/main/java/com/foodya/foodya_backend/jwt/JwtService.java:63`

## Key Symbols

| Symbol | Type | File | Line |
|--------|------|------|------|
| `refreshToken` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/auth/controller/AuthController.java` | 93 |
| `refreshToken` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/auth/service/AuthService.java` | 108 |
| `key` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/jwt/JwtService.java` | 31 |
| `generateToken` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/jwt/JwtService.java` | 36 |
| `extractClaims` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/jwt/JwtService.java` | 63 |
| `validateToken` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/jwt/JwtService.java` | 75 |
| `extractUsername` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/jwt/JwtService.java` | 98 |
| `extractRole` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/jwt/JwtService.java` | 102 |

## Execution Flows

| Flow | Type | Steps |
|------|------|-------|
| `Login → Key` | cross_community | 7 |
| `RefreshToken → Key` | intra_community | 5 |
| `DoFilterInternal → Key` | cross_community | 4 |
| `RefreshToken → UnauthorizedException` | intra_community | 3 |
| `RefreshToken → FindByUsername` | cross_community | 3 |
| `ExtractRole → Key` | intra_community | 3 |

## Connected Areas

| Area | Connections |
|------|-------------|
| Service | 2 calls |
| Controller | 1 calls |

## How to Explore

1. `gitnexus_context({name: "refreshToken"})` — see callers and callees
2. `gitnexus_query({query: "jwt"})` — find related execution flows
3. Read key files listed above for implementation details
