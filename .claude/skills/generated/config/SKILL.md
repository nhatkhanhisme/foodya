---
name: config
description: "Skill for the Config area of foodya. 3 symbols across 1 files."
---

# Config

3 symbols | 1 files | Cohesion: 100%

## When to Use

- Working with code in `foodya-backend/`
- Understanding how securityFilterChain, authenticationProvider, passwordEncoder work
- Modifying config-related functionality

## Key Files

| File | Symbols |
|------|---------|
| `foodya-backend/src/main/java/com/foodya/foodya_backend/config/SecurityConfig.java` | securityFilterChain, authenticationProvider, passwordEncoder |

## Entry Points

Start here when exploring this area:

- **`securityFilterChain`** (Method) — `foodya-backend/src/main/java/com/foodya/foodya_backend/config/SecurityConfig.java:34`
- **`authenticationProvider`** (Method) — `foodya-backend/src/main/java/com/foodya/foodya_backend/config/SecurityConfig.java:101`
- **`passwordEncoder`** (Method) — `foodya-backend/src/main/java/com/foodya/foodya_backend/config/SecurityConfig.java:114`

## Key Symbols

| Symbol | Type | File | Line |
|--------|------|------|------|
| `securityFilterChain` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/config/SecurityConfig.java` | 34 |
| `authenticationProvider` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/config/SecurityConfig.java` | 101 |
| `passwordEncoder` | Method | `foodya-backend/src/main/java/com/foodya/foodya_backend/config/SecurityConfig.java` | 114 |

## Execution Flows

| Flow | Type | Steps |
|------|------|-------|
| `SecurityFilterChain → PasswordEncoder` | intra_community | 3 |

## How to Explore

1. `gitnexus_context({name: "securityFilterChain"})` — see callers and callees
2. `gitnexus_query({query: "config"})` — find related execution flows
3. Read key files listed above for implementation details
