# Change Password

## Purpose
Allow any authenticated user to update their account password by verifying their current password first.

## User Story
As an authenticated user, I want to change my password securely so I can maintain account safety.

## Entry Points
- Profile → "Change password" link (Customer, Owner, Driver)

## Exit Points
- On success → back to Profile
- "Cancel" → back to Profile

## Layout
Rendered within the role-appropriate shell (CustomerShell, OwnerShell, DriverShell). Single-column form card, `max-width: 480px`.

## Components
- `FormField` wrapping `PasswordField` (current password, show/hide toggle)
- `FormField` wrapping `PasswordField` (new password, show/hide toggle)
- `FormField` wrapping `PasswordField` (confirm new password, show/hide toggle)
- `Button` (Outlined) — "Cancel"
- `Button` (Filled) — "Change password"

## User Actions
1. Enter current password.
2. Enter new password.
3. Confirm new password.
4. Click "Change password".

## Business Rules
- Current password must be verified by the server (not just client).
- New password cannot be identical to current (enforced server-side).

## Validation Rules
| Field | Rule | When |
|---|---|---|
| Current password | Required | On blur |
| New password | Required, min 8 chars | On blur |
| Confirm password | Must match new password | On blur (client-side check) |
| New ≠ current | Must differ from current | Server-side (400) |

## API Endpoints
```
POST /api/v1/auth/change-password
Headers: Authorization: Bearer <accessToken>
Body: { currentPassword: string, newPassword: string, confirmPassword: string }

Success 200: { success: true, message: "Password changed successfully" }
Error 400: VALIDATION_ERROR (current password wrong, or new = current, or mismatch)
```

## Loading State
Submit button shows spinner and is disabled.

## Empty State
N/A.

## Error State
| Error | Display |
|---|---|
| Wrong current password | Inline error on current password field: "Current password is incorrect." |
| New passwords don't match | Client-side inline on confirm field: "Passwords do not match." |
| New same as current | Inline error on new password field: "New password must be different." |
| Network | `Toast` (error + Retry) |

## Permissions
`AUTH` — any authenticated user. Route guard enforces authentication.

## Responsive Notes
No responsive differences. Form always single-column.

## Accessibility
- All password fields: `type="password"`, `autocomplete` attributes (`current-password`, `new-password`).
- Show/hide toggle: `aria-label="Show password"` / `"Hide password"`.
- Error messages linked via `aria-describedby`.

## Developer Notes
- On success: show `Toast` (success): "Password changed successfully." then navigate to Profile.
- Clear all three fields on success; do not navigate without the toast.
- On cancel: navigate back to Profile without API call.
