# Order Detail — Owner View

## Purpose
Full order view with context to confirm, reject, or mark ready. Phase-aware: shows appropriate actions based on current order status.

## User Story
As a restaurant owner, I want to see the full details of an order so I can make an informed confirm or reject decision.

## Entry Points
- Incoming Orders card "View details" click

## Exit Points
- Action taken → `/owner/orders` (Incoming Orders)
- `← Back` → `/owner/orders`

## Layout
`OwnerShell`. Single-column, `max-width: 720px`.

## Components
- `PageBreadcrumb` (Incoming Orders / Order #990)
- `StatusBadge` (large) + `CountdownTimer` (PENDING orders only)
- **Items section:** `OrderItemRow` list (name, quantity, subtotal)
- **Order summary:** subtotal, delivery fee, total
- **Customer note** (if present)
- **Payment info:** `PaymentInfoBadge` (owner variant — COD or "VNPay — Paid ✓")
- **Delivery address:** district + city only (privacy rule)
- `OrderStatusTimeline` (owner variant)
- **Actions (context-aware by status):**
  - PENDING: `Button` (Destructive Outlined) "Reject ✕" + `Button` (Filled) "Confirm ✓"
  - CONFIRMED: `Button` (Filled) "Mark as ready"
  - READY_FOR_PICKUP+: read-only, no actions
- `ReasonDialog` — triggered by Reject

## Business Rules
- `CountdownTimer` shown and active for PENDING orders only.
- On timer expiry: buttons disabled; `AlertBanner` (warning): "Response window expired. This order was auto-cancelled."
- Customer cancellation while viewing: order disappears or shows CANCELLED status via SSE.
- `AWAITING_PAYMENT` orders never visible (BR-23).
- Payment info: if `payment.method = ONLINE` and `payment.status = SUCCESS` → show "Paid via [provider] ✓". COD → "Cash on delivery — collected by shipper".
- Delivery address: show district + city only (not full street — owner doesn't need full address).

## API Endpoints
```
GET /api/v1/restaurant/orders/{id}   [implied]

PATCH /api/v1/restaurant/orders/{id}/confirm
PATCH /api/v1/restaurant/orders/{id}/reject
  Body: { reason: string, reasonCode: string }
PATCH /api/v1/restaurant/orders/{id}/ready

SSE: order cancellation event
```

## Loading State
`SkeletonLoader` (section blocks) while fetching.

## Error State
- Action fails: `Toast` (error + Retry). Buttons re-enable.
- Timer expired while page open: buttons disabled, AlertBanner shown.
- Customer cancelled: SSE event updates status display.

## Permissions
`OWNER`. Order must belong to this owner's restaurant.

## Responsive Notes
Single-column at all breakpoints.

## Developer Notes
- `CountdownTimer`: calculate from `order.createdAt + 5min`. Use server time.
- SSE subscription on this page: listen for `ORDER_CANCELLED` events. On receipt: update status display, disable action buttons, show `AlertBanner`.
- `ReasonDialog` reasons: ["Ingredients unavailable", "Restaurant closing early", "Too many orders", "Other"]. `noteInternal={false}`.
