# Restaurant Settings

## Purpose
Edit the restaurant's public profile (name, description, contact, address, hours) and control the open/closed status.

## User Story
As a restaurant owner, I want to keep my restaurant profile and operating hours up to date so customers see accurate information.

## Entry Points
- SideNav "Settings" item

## Exit Points
- "Save changes" → stays on Settings with Toast
- "Discard" → reverts form, stays on Settings

## Layout
`OwnerShell`. Single-column form, `max-width: 720px`.

## Components
- **Open/Close Toggle** (top of page, immediate effect, no form save needed)
  - `Toggle` + label: "Restaurant status — Open / Closed"
  - Description: "Close temporarily to stop new orders without changing your hours."
- **Profile section:**
  - `FormField` (restaurant name)
  - `Textarea` (description, optional)
  - `PhoneInput` (restaurant contact phone)
- **Images section:**
  - `ImageUpload` (logo — 1:1, 512px max)
  - `ImageUpload` (cover photo — 16:9, 1200×400 max)
- **Address section:**
  - `FormField` (street, ward, district, city)
  - `MapPicker` ("Update pin" button)
- **Opening hours section:**
  - `OpenHoursEditor`: day-of-week toggles + open/close time pickers per day
- `Button` (Outlined) — "Discard"
- `Button` (Filled) — "Save changes"

## Business Rules
- Open/Close toggle: immediate `PATCH` — does NOT require form save. Reversible instantly.
- Closing while active confirmed orders exist: `AlertBanner` (warning): "You have N active orders. Closing will not affect them."
- Reopening: immediate; customers can order again at once.
- Address change updates lat/lng for distance calculations on future orders.
- Opening hours define when `BR-06` allows new orders. Closed days → `RESTAURANT_CLOSED` 422 on checkout attempt.

## Validation Rules
| Field | Rule |
|---|---|
| Restaurant name | Required, min 2 chars |
| Phone | Valid format |
| Street / District / City | Required |
| Latitude / Longitude | Valid range (BR-22) |
| Opening hours | Close time must be after open time per day |

## API Endpoints
```
GET /api/v1/restaurants/{id}  (pre-populate form)

PUT /api/v1/restaurants/{id}
Body: { name, description?, phone, address, latitude, longitude, openingHours }

PATCH /api/v1/restaurants/{id}/toggle-status
Body: { open: boolean }
```

## Loading State
Form fields: `SkeletonLoader` (text variant) while fetching current data.

## Empty State
N/A.

## Error State
- Toggle error: `Toast` (error + Retry). Toggle reverts.
- Save error (400): inline field errors.
- Save error (network): `Toast` (error + Retry).

## Permissions
`OWNER`.

## Responsive Notes
- Form single-column.
- `OpenHoursEditor`: compact on mobile (stacked; one row per day).
- `MapPicker`: minimum 200px height.

## Accessibility
- Open/Close toggle: `aria-label="Restaurant is currently {open|closed}. Toggle to {close|open}."`.
- `OpenHoursEditor` day checkboxes: `aria-label="{Monday} is {enabled|disabled}"`.

## Developer Notes
- Pre-populate all form fields on mount via GET /restaurants/:id.
- Open/Close toggle: fire-and-forget PATCH (no confirm dialog). Show `Toast` (success): "Restaurant is now {open/closed}." on success.
- "Discard" button: call React Hook Form `reset()` with original server values.
- Opening hours format: `[{ day: 'MON', open: '08:00', close: '22:00', enabled: true }]` — one entry per day of week.
