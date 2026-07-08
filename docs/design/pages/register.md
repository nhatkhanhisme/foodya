# Register

## Purpose
Create a new user account. Three registration paths: Customer (1 step), Restaurant Owner (2 steps), Shipper (2 steps).

## User Story
As a new visitor, I want to choose my role and create an account so I can use the platform.

## Entry Points
- "Sign up" link on Login or Landing
- Direct URL: `/auth/register`

## Exit Points
- Customer → `/` (Home) after JWT issued
- Owner → `/pending` (Pending Approval)
- Shipper → `/pending` (Pending Approval)

## Layout
`AuthShell`. `StepIndicator` shown for Owner (2 steps) and Shipper (2 steps). Not shown for Customer (1 step).

## Components

### Step 0 — Role Selection (all paths)
- `RoleSelector` (3 cards: Customer / Restaurant Owner / Shipper)
- `Button` (Filled) — "Continue"

### Customer Registration (1 step)
- `FormField` (full name, email)
- `PhoneInput` (+84 prefix)
- `PasswordField` × 2 (password, confirm)
- `Button` (Filled, Large) — "Create account"

### Owner Registration (Step 1 — Account)
- Same fields as Customer above

### Owner Registration (Step 2 — Restaurant)
- `FormField` (restaurant name, description, phone)
- `FormField` (street, ward, district, city)
- `MapPicker` (set lat/lng)
- `OpenHoursEditor`
- `Button` (Outlined) — "← Back"
- `Button` (Filled) — "Submit for review"

### Shipper Registration (Step 1 — Account)
- Same as Customer account fields

### Shipper Registration (Step 2 — Vehicle)
- `SelectField` (vehicle type: Motorbike / Bicycle / Car)
- `FormField` (license plate)
- `Button` (Outlined) — "← Back"
- `Button` (Filled) — "Create shipper account"

## User Actions
1. Select role → Continue.
2. Fill account details → Next (multi-step) or Submit (Customer).
3. Fill role-specific details → Submit.
4. On error → fix inline field errors → resubmit.

## Business Rules
- Role is fixed at registration; not self-changeable afterwards (BR-01).
- Email must be unique (UC-C01 alt: 409 AUTH_EMAIL_TAKEN).
- Username (if collected) must be unique (409 AUTH_USERNAME_TAKEN).
- Owner registration creates User + Restaurant (status=PENDING) in the same request flow.
- Shipper registration creates User + ShipperProfile atomically (BR-32).

## Validation Rules
| Field | Rule |
|---|---|
| Full name | Required, min 2 chars |
| Email | Required, valid email format |
| Phone | Required, Vietnamese format (0[0-9]{9}) |
| Password | Required, min 8 chars |
| Confirm password | Must match password |
| Restaurant name | Required (Owner only) |
| Street / District / City | Required (Owner only) |
| Latitude / Longitude | Required, valid range (BR-22) |
| Vehicle type | Required (Shipper only) |
| License plate | Required (Shipper only) |

Show errors on blur per field; re-validate all on submit.

## API Endpoints
```
POST /api/v1/auth/register
Body (Customer):
{ fullName, email, phone, password, role: 'CUSTOMER' }

Body (Owner):
{ fullName, email, phone, password, role: 'RESTAURANT_OWNER',
  restaurant: { name, description, phone, address, latitude, longitude, openingHours } }

Body (Shipper):
{ fullName, email, phone, password, role: 'SHIPPER',
  vehicle: { vehicleType, licensePlate } }

Success 201: { data: { accessToken, refreshToken, user } }
Error 409: AUTH_EMAIL_TAKEN | AUTH_USERNAME_TAKEN
Error 400: VALIDATION_ERROR
```

## Loading State
Submit button shows spinner and is disabled during request.

## Error State
| Error | Display |
|---|---|
| 409 AUTH_EMAIL_TAKEN | `AlertBanner` + inline error on email field: "Email is already registered." |
| 400 VALIDATION_ERROR | Inline errors on affected fields |
| Network | `Toast` (error + Retry) |

## Permissions
`PUBLIC` — redirect to role home if already authenticated.

## Responsive Notes
Full-width card on mobile. `OpenHoursEditor` stacks day-by-day vertically below 768px. `MapPicker` minimum height 200px.

## Accessibility
- `StepIndicator` uses `aria-label="Step 2 of 2"` on the active step.
- Role selection cards: `role="radio"`, `aria-checked`.
- MapPicker: `aria-label="Select restaurant location"`, lat/lng shown as read-only text below.

## Developer Notes
- Preserve form state across steps in React Hook Form with persistent form context.
- Step 1 validation fires before advancing to Step 2 — do not allow advancing on invalid Step 1.
- `MapPicker` initialises centred on Ho Chi Minh City (`10.7769° N, 106.7009° E`) when no pin is set.
- `OpenHoursEditor` default: Mon–Sun, 08:00–22:00, all days enabled.
