# Address Form

## Purpose
Create or update a delivery address. Used standalone and as a modal within Checkout.

## User Story
As a customer, I want to add or edit a delivery address with a map pin so my food is delivered to the right place.

## Entry Points
- Address Management → "Add new address" FAB
- Address Management → address card "Edit" action
- Checkout → "Add new address" link (opens as modal/dialog)

## Exit Points
- Save success → Address Management (standalone) / Checkout (modal close)
- Cancel → same back navigation

## Layout
- **Standalone** (`/profile/addresses/new`, `/profile/addresses/:id/edit`): `CustomerShell`, single-column form, `max-width: 640px`.
- **Modal** (within Checkout): `FormDialog` (560px max-width).

## Components
- `FormField` (label — e.g., "Home", "Work")
- `FormField` (recipient name)
- `PhoneInput` (recipient phone)
- `FormField` (street)
- `FormField` (ward)
- `FormField` (district)
- `FormField` (city)
- `MapPicker` (drag pin to set lat/lng; displays coordinates read-only below)
- `Toggle` (is_default — "Use as my default address")
- `Button` (Outlined) — "Cancel"
- `Button` (Filled) — "Save address"

## Business Rules
- Lat/lng must be provided (MapPicker required — BR-22 coordinate validation).
- Coordinates checked server-side: lat ∈ [-90, 90], lng ∈ [-180, 180] (BR-22).
- Setting `is_default = true`: server atomically sets `is_default = false` on all others for this user.
- Edit of address referenced by a non-terminal order: allowed; does not affect in-progress delivery.

## Validation Rules
| Field | Rule |
|---|---|
| Label | Required, max 50 chars |
| Recipient name | Required |
| Recipient phone | Valid format |
| Street | Required |
| Ward | Required |
| District | Required |
| City | Required |
| Latitude | Required, -90 to 90 |
| Longitude | Required, -180 to 180 |

## API Endpoints
```
POST /api/v1/users/me/addresses
Body: AddressInput { label, recipientName, phone, street, ward, district, city, latitude, longitude, isDefault }
Response 201: Address

PUT /api/v1/users/me/addresses/{id}
Body: same
Response 200: Address
Error 400: VALIDATION_ERROR (coordinate out of range)
```

## Loading State
- **Edit mode:** `SkeletonLoader` per field while fetching existing address data.
- Save button: spinner when request in flight.

## Empty State
N/A.

## Error State
- 400 VALIDATION_ERROR: inline field errors.
- Coordinate out of range: MapPicker shows validation message.
- Network: `Toast` (error + Retry).

## Permissions
`CUSTOMER`.

## Responsive Notes
Single-column at all breakpoints. MapPicker min 200px height.

## Accessibility
- MapPicker: `aria-label="Select delivery location on map"`. Lat/lng shown as `<output>` below map.
- is_default toggle: `aria-label="Use as default address"`.

## Developer Notes
- `MapPicker` initialises on Ho Chi Minh City centre if creating new; on existing pin if editing.
- Reverse geocoding (pin → address fields): optional enhancement — not required at MVP.
- Unsaved changes guard: warn on navigation away.
- Modal variant (within Checkout): on save success, close dialog, call `onAddressAdded(newAddress)` callback to refresh the address list and auto-select the new address.
