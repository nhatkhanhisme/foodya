# Incoming Orders

## Purpose
Live feed of orders requiring the owner's response. Shows PENDING orders (needing confirm/reject) and CONFIRMED orders (awaiting ready-for-pickup). Primary daily workflow screen.

## User Story
As a restaurant owner, I want to see and respond to new orders in real-time so I can confirm or reject them before the 5-minute window closes.

## Entry Points
- SideNav "Incoming Orders" item
- Dashboard active orders panel "View all →" link
- Push notification deep-link for new orders

## Exit Points
- Order card "View details" → `/owner/orders/:orderId`
- "Dashboard" SideNav item → `/owner/dashboard`

## Layout
`OwnerShell`. Single-column content list.

## Components
- `TabBar` — [Needs action (N)] [Confirmed (N)] [All today]
- `LiveStatusBanner` (SSE-connected)
- `AlertBanner` (warning) — stale orders (> 3 min since placed with no response)
- `OrderCard` (owner variant) with `CountdownTimer`
- Auto-expired card variant (read-only, "Auto-cancelled" state)
- `Button` (Filled Tonal) — "Confirm ✓" per PENDING card
- `Button` (Destructive Outlined) — "Reject ✕" per PENDING card
- `Button` (Filled) — "Mark ready" per CONFIRMED card
- `SkeletonLoader` (× 3)
- `EmptyState`

## User Actions
1. Confirm order directly from card (without entering detail).
2. Reject order — opens `ReasonDialog`.
3. Mark order ready — opens `QuickConfirmInline`.
4. Click "View details" → navigate to Order Detail.
5. Switch between filter tabs.

## Business Rules
- PENDING orders have a 5-minute response window per card (BR-11).
- CountdownTimer per card: turns warning/amber at < 60s.
- On timeout (timer reaches 0): backend auto-cancels (BR-12); card updates to expired state via SSE.
- New orders appear at the top of the feed via SSE animation.
- Customer-cancelled orders disappear from feed via SSE.
- AWAITING_PAYMENT orders are never shown (BR-23).
- Rejection requires a reason (required field in `ReasonDialog`).
- "Mark ready" uses `QuickConfirmInline` (not a full `ConfirmDialog`) — lower stakes.

## Validation Rules
N/A for direct confirm/reject/ready actions. Rejection reason is required (see `ReasonDialog`).

## API Endpoints
```
GET /api/v1/restaurant/orders?status=PENDING,CONFIRMED
Response: OrderSummary[]

PATCH /api/v1/restaurant/orders/{id}/confirm
PATCH /api/v1/restaurant/orders/{id}/reject
Body: { reason: string, reasonCode: string }
PATCH /api/v1/restaurant/orders/{id}/ready

SSE: real-time new order events, cancellation events
```

## Loading State
`SkeletonLoader` (list-item variant, × 3) on initial load.

## Empty State
- "Needs action" tab empty:
```
Illustration: check / inbox clear
Headline: "You're all caught up"
Description: "New orders will appear here automatically."
```

## Error State
- Confirm/Reject/Ready API fails: `Toast` (error): "Could not update order. Try again." Card reverts to pre-action state.
- SSE disconnects: `AlertBanner` (warning): "Live updates paused." + "Reconnect" CTA. Fall back to 15s polling.

## Permissions
`OWNER`.

## Responsive Notes
- Full-width on all breakpoints.
- On mobile: Confirm/Reject buttons stack vertically within each card.
- SideNav collapses on mobile; orders screen full-screen.

## Accessibility
- `CountdownTimer`: `aria-live="polite"` for the remaining time display.
- On timer reaching 0: `aria-live="assertive"`: "Order expired and cancelled automatically."
- Confirm/Reject buttons: `aria-label="Confirm order #{id}"`, `"Reject order #{id}"`.

## Developer Notes
- SSE: subscribe to owner-scoped order events on mount; unsubscribe on unmount.
- On SSE `NEW_ORDER_PLACED`: prepend card to the PENDING list with `motion-card-appear` animation.
- On SSE `ORDER_CANCELLED`: remove card from list.
- On SSE `ORDER_AUTO_CANCELLED` (timeout): update card to expired state; do not remove (show in "All today").
- `CountdownTimer` per card: calculate from `order.createdAt + 5 min`. Use server time, not local.
- Reject `ReasonDialog`: `noteInternal={false}` (reason is sent to customer). Predefined reason options: "Ingredients unavailable", "Restaurant closing early", "Too many orders", "Other".
