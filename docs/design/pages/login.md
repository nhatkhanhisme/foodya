# Login

## Purpose
Authenticate an existing user of any role and route them to their role-appropriate home screen.

## User Story
As any registered user, I want to sign in with my email and password so I can access my account.

## Entry Points
- Direct URL: `/auth/login`
- Redirect from any protected route (with `?next=<path>` preserved)
- "Log in" link from Landing, Register, or Pending Approval

## Exit Points
- `CUSTOMER` → `/` (Home — Discovery)
- `RESTAURANT_OWNER` + `APPROVED` → `/owner/dashboard`
- `RESTAURANT_OWNER` + `PENDING/REJECTED` → `/pending`
- `SHIPPER` + `APPROVED` → `/shipper/jobs`
- `SHIPPER` + `PENDING_APPROVAL/REJECTED` → `/pending`
- `ADMIN` → `/admin/dashboard`
- Register link → `/auth/register`

## Layout
`AuthShell`: logo centred at top, single card (`max-width: 480px`) vertically centred on page.

## Components
- `AuthShell`
- `FormField` (email)
- `PasswordField` (password, with show/hide toggle)
- `Button` (Filled, Large, full-width) — "Sign in"
- `AlertBanner` (error variant) — form-level errors
- `Toast` (error) — rate limit

## User Actions
1. Enter email address.
2. Enter password.
3. Click "Sign in".
4. (Optional) Click "Sign up" to navigate to Register.

## Business Rules
- Login identifier is **email** only — never username (UC-C02).
- Rate limit: 10 requests/minute/IP (BR-02).
- Banned accounts return 403 (BR-30); error message must not distinguish from wrong credentials.
- Do not distinguish "wrong password" from "email not found" — both return the same 401 (BR: prevents email enumeration).

## Validation Rules
| Field | Rule |
|---|---|
| Email | Required, valid email format (client-side) |
| Password | Required, min 1 character |

Client-side validation fires on submit, not on keystroke.

## API Endpoints
```
POST /api/v1/auth/login
Body: { email: string, password: string }

Success 200:
{ data: { accessToken, refreshToken, expiresIn, user: { id, fullName, role } } }

Error 401: AUTH_INVALID_CREDENTIALS
Error 403: AUTH_ACCOUNT_BANNED
Error 429: RATE_LIMIT_EXCEEDED
```

## Loading State
"Sign in" button shows `Spinner` (sm, white) and is disabled while request is in flight.

## Empty State
N/A (no data-dependent content).

## Error State
| Error | Display |
|---|---|
| 401 AUTH_INVALID_CREDENTIALS | `AlertBanner` above form: "Invalid email or password." |
| 403 AUTH_ACCOUNT_BANNED | `AlertBanner` above form: "Your account has been suspended. Contact support." |
| 429 RATE_LIMIT_EXCEEDED | `Toast` (error): "Too many login attempts. Please wait a minute." |
| Network error | `Toast` (error + Retry): "Unable to connect. Check your connection." |

## Permissions
`PUBLIC` — redirect to role home if already authenticated.

## Responsive Notes
Card fills full width with 16px margin on mobile. Form fields full-width at all breakpoints.

## Accessibility
- `<form>` with `aria-label="Sign in"`.
- `<label>` associated with each input via `htmlFor`.
- Error messages linked to inputs via `aria-describedby`.
- Submit on Enter key from any field.

## Developer Notes
- On success: store `accessToken` and `refreshToken` in `authStore`.
- After storing tokens, call `GET /users/me` to hydrate full user profile if not already in JWT payload.
- If `?next=<path>` is present and path starts with the user's role prefix, redirect there; otherwise redirect to role default.
- Auto-redirect to role home if already authenticated on mount.
