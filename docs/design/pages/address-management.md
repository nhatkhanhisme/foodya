# Address Management

## Purpose
List, manage, and set default delivery addresses for a Customer account.

## User Story
As a customer, I want to manage my saved delivery addresses so I can quickly select where to deliver my orders.

## Entry Points
- Profile → "Manage delivery addresses →"
- Checkout → "Add new address" → returns here

## Exit Points
- Address card "Edit" → `/profile/addresses/:id/edit` (Address Form)
- "Add new address" FAB → `/profile/addresses/new` (Address Form)
- `← Back` → Profile

## Layout
`CustomerShell`. Single-column list, `max-width: 640px`.

## Components
- `AddressCard` list (label, recipient, street, default badge, set-default and edit and delete actions)
- `FAB` (Extended, bottom-right) — "+ Add address"
- `ConfirmDialog` — delete confirmation
- `SkeletonLoader` (× 3)
- `EmptyState`

## User Actions
1. View saved addresses.
2. Set an address as default (PATCH).
3. Edit an address (navigate to form).
4. Delete an address (with confirmation).
5. Add new address (navigate to form).

## Business Rules
- Cannot delete the only address if it is referenced by a non-terminal order (UC-C06 alt, `ADDRESS_LAST_ONE` 422).
- "Set as default" sets this address `is_default = true` and all others `is_default = false` (atomic server-side).
- Minimum 1 address required to place an order; no deletion that leaves 0 addresses.

## API Endpoints
```
GET /api/v1/users/me/addresses
DELETE /api/v1/users/me/addresses/{id}
  Error 422 ADDRESS_LAST_ONE
PATCH /api/v1/users/me/addresses/{id}/set-default
```

## Loading State
`SkeletonLoader` (list-item variant, × 3).

## Empty State
```
Illustration: map pin
Headline: "No delivery addresses saved"
Description: "Add an address to start ordering."
CTA: "+ Add your first address"
```

## Error State
- Delete 422 ADDRESS_LAST_ONE: `Toast` (error): "This is your only address. Add another address before deleting this one."
- Network: `Toast` (error + Retry).

## Permissions
`CUSTOMER`.

## Responsive Notes
Full-width cards on mobile. `max-width: 640px` on desktop.

## Accessibility
- Delete button: `aria-label="Delete {label} address"`.
- Default badge: `aria-label="Default delivery address"`.
