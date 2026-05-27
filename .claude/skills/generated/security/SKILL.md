---
name: security
description: "Skill for the Security area of foodya. 3 symbols across 2 files."
---

# Security

3 symbols | 2 files | Cohesion: 100%

## When to Use

- Working with code in `mobile/`
- Understanding how ChangePasswordView, onDismissSuccess, isFormValid work
- Modifying security-related functionality

## Key Files

| File | Symbols |
|------|---------|
| `mobile/app/src/main/java/com/example/foodya/ui/screen/security/ChangePasswordViewModel.kt` | onDismissSuccess, isFormValid |
| `mobile/app/src/main/java/com/example/foodya/ui/screen/security/ChangePasswordView.kt` | ChangePasswordView |

## Entry Points

Start here when exploring this area:

- **`ChangePasswordView`** (Function) — `mobile/app/src/main/java/com/example/foodya/ui/screen/security/ChangePasswordView.kt:18`
- **`onDismissSuccess`** (Method) — `mobile/app/src/main/java/com/example/foodya/ui/screen/security/ChangePasswordViewModel.kt:72`
- **`isFormValid`** (Method) — `mobile/app/src/main/java/com/example/foodya/ui/screen/security/ChangePasswordViewModel.kt:76`

## Key Symbols

| Symbol | Type | File | Line |
|--------|------|------|------|
| `ChangePasswordView` | Function | `mobile/app/src/main/java/com/example/foodya/ui/screen/security/ChangePasswordView.kt` | 18 |
| `onDismissSuccess` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/security/ChangePasswordViewModel.kt` | 72 |
| `isFormValid` | Method | `mobile/app/src/main/java/com/example/foodya/ui/screen/security/ChangePasswordViewModel.kt` | 76 |

## How to Explore

1. `gitnexus_context({name: "ChangePasswordView"})` — see callers and callees
2. `gitnexus_query({query: "security"})` — find related execution flows
3. Read key files listed above for implementation details
