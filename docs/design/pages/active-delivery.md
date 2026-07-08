# Active Delivery

## Purpose
Manages a single delivery from pickup through handoff. Two phases: pickup (heading to restaurant) and en-route (heading to customer). Background location posting is active throughout.

## User Story
As a driver, I want to see the pickup and delivery details clearly and mark the correct status at each step so the customer is kept informed.

## Entry Points
- Auto-navigate after accepting a job (from Available Jobs)
- BottomNav "Delivery" tab while job is active (replaces "Jobs" tab)
- App restart/login while order is in PICKED_UP state

## Exit Points
- "Mark as delivered" success → Delivery Complete screen → Available Jobs
- "Delivery problem" → `/shipper/jobs/:orderId/exception` (Delivery Exception)
- `← Back` with ConfirmDialog ("Leave active delivery? You are still assigned.") → Available Jobs

## Layout
**Layout 5 (Map + Panel):** Map takes full viewport; delivery panel is a bottom sheet (mobile) or left panel (desktop). `DriverShell` chrome minimal in this view.

## Components

### Phase: Pickup (heading to restaurant)
- `LocationStatusBanner` (GPS/network status)
- `ActiveDeliveryPanel` (phase=pickup):
  - Restaurant name + full address
  - Phone number + [Call] button
  - Order item summary
  - `PaymentInfoBadge` (driver variant — cash collection reminder if COD)
  - `Button` (Outlined) — "Open navigation" (external maps)
  - `Button` (Filled, Large) — "I'm at the restaurant — Mark as picked up"
- `ShipperLocationMap` (restaurant pin highlighted)

### Phase: En route (heading to customer)
- `LocationStatusBanner`
- `ActiveDeliveryPanel` (phase=enroute):
  - Customer name + district (NOT full street — privacy rule)
  - Phone number + [Call] button
  - `PaymentInfoBadge` (driver variant)
  - `Button` (Outlined) — "Open navigation"
  - `Button` (Filled, Large) — "Order delivered ✓"
  - `Button` (Destructive Text, small) — "Delivery problem"
- `ShipperLocationMap` (customer pin highlighted, shipper pin live)

### QuickConfirmInline for Mark Picked Up
- "Confirm pickup? Order #990 from Pho Hung"
- [No, wait] [Yes, I have the food ✓]

### ConfirmDialog for Mark Delivered (COD)
- "Confirm delivery?"
- Warning: "Did you collect ₫125.000 cash from the customer? Only confirm after receiving the cash."
- [Not yet] [Yes, delivered & cash collected ✓]

### ConfirmDialog for Mark Delivered (Online)
- "Confirm delivery?"
- Info: "Payment already handled — no cash to collect."
- [Not yet] [Yes, delivered ✓]

## User Actions
1. Navigate to restaurant via "Open navigation".
2. Confirm pickup via `QuickConfirmInline`.
3. Navigate to customer via "Open navigation".
4. Mark delivered via `ConfirmDialog`.
5. Report delivery problem → Delivery Exception screen.

## Business Rules
- Full customer address revealed only after `status = PICKED_UP`. Before pickup: district + city only.
- COD cash collection reminder shown in pickup phase and delivery confirm dialog.
- Location posting: `POST /shipper/location` every ~10s while this screen is active.
- Back navigation guarded: assignment persists even if driver leaves this screen.
- Active delivery state persists across app restart (detect on login from server state).

## Validation Rules
N/A.

## API Endpoints
```
PATCH /api/v1/shipper/orders/{id}/pickup
Response 200: Order status → PICKED_UP

PATCH /api/v1/shipper/orders/{id}/deliver
Body: { exception: false }
Response 200: Order status → DELIVERED; COD Payment → SUCCESS

POST /api/v1/shipper/location
Body: { lat: number, lng: number }
Response 200 (fire-and-forget)
```

## Loading State
- Status action buttons show spinner when request in flight.
- Map loads with SDK's own loading indicator.

## Empty State
N/A.

## Error State
| Error | Display |
|---|---|
| Mark pickup fails | `Toast` (error + Retry): "Could not update status. Try again." |
| Mark delivered fails | `Toast` (error + Retry): "Could not mark as delivered. Try again." |
| Location post fails | Silent queue; `LocationStatusBanner` may show network state |
| Network offline | `LocationStatusBanner` (offline) + action buttons disabled |

## Permissions
`DRIVER`. Order must be assigned to this driver.

## Responsive Notes
- Mobile: full-screen map at top; bottom sheet for delivery panel (pull-up).
- Desktop: left panel (details) + right map (full height).

## Accessibility
- "Open navigation" button: `aria-label="Open navigation to {destinationName}"`.
- Mark Picked Up: `aria-label="Mark order #{id} as picked up"`.
- `LocationStatusBanner`: `role="alert"` when GPS goes off.

## Developer Notes
- Location posting: `useGeolocation(active: order.status === 'PICKED_UP')` hook.
- Queue failed POST /shipper/location locally (array); flush on reconnect in order.
- Stop posting when order reaches DELIVERED or CANCELLED.
- Address privacy: render `delivery.district + ', ' + delivery.city` in pickup phase. After `status = PICKED_UP`, use full `delivery.street + ', ' + delivery.ward + ', ' + delivery.district`.
- Phase detection: `order.status === 'READY_FOR_PICKUP'` → pickup phase; `order.status === 'PICKED_UP'` → en-route phase.
- On successful PATCH /deliver: navigate to Delivery Complete intermediate screen, then to Available Jobs.
