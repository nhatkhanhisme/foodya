# Order Tracking

## Purpose
Live order status page with real-time updates via SSE. Shows order progress, cancellation option (while eligible), shipper location on a map, and triggers the review flow on delivery.

## User Story
As a customer, I want to see the live status of my order and where the driver is so I know when to expect my food.

## Entry Points
- Auto-redirect after COD checkout (status=PENDING)
- Auto-redirect from Payment Awaiting (status transitioned to PENDING)
- Order History row click (active orders)
- Push notification deep-link

## Exit Points
- `← Back` link (explicit, not browser back) → `/orders`
- "Rate your order" CTA → Write Review modal
- "Order again" CTA (cancelled/rejected) → `/restaurants/:restaurantId`
- "View order details" → `/orders/:orderId` (static detail)

## Layout
`CustomerShell` (TopNav). **`TwoColumnLayout`** on desktop: left = status panel, right = map (60%). On mobile: single column; map at top (240px), status panel below.

## Components
- `← Back` explicit link (not browser back)
- `OrderStatusTimeline` (customer variant)
- `LiveStatusBanner` (SSE-connected; pulsing dot when live)
- Estimated delivery time display
- Shipper info card (avatar, name, vehicle, phone — visible once PICKED_UP)
- `ShipperLocationMap` (right panel or top on mobile — visible once PICKED_UP)
- `PaymentInfoBadge` (customer variant)
- Order items accordion (collapsed by default)
- `Button` (Destructive Outlined) — "Cancel order" (conditional — see Business Rules)
- `Button` (Filled) — "Rate your order" (DELIVERED state only)
- `Button` (Filled) — "Order again" (CANCELLED / REJECTED state only)
- Review modal (`StarRatingInput`, `Textarea`) — opened in-page

## User Actions
1. Watch order status update in real-time.
2. Cancel order (if eligible).
3. Rate order (after DELIVERED).
4. Navigate back to Order History.

## Business Rules
- "Cancel order" visible and enabled only for: `AWAITING_PAYMENT`, `PENDING`, `CONFIRMED`.
- Cancel button hidden (not just disabled) when `status = READY_FOR_PICKUP` or later.
- Cancelling a CONFIRMED order: `ConfirmDialog` includes penalty warning (BR-09): "This will be noted against your account."
- Map and shipper info: shown only when `status = PICKED_UP` or `DELIVERED`.
- Live location updates: stop when status reaches terminal state.
- Review CTA: shown only when `status = DELIVERED` and no existing review for this order.

## Validation Rules
N/A (read-only tracking; review modal has star rating required).

## API Endpoints
```
GET /api/v1/orders/{id}
Response: OrderDetail with current status, shipper info, timeline timestamps

PATCH /api/v1/orders/{id}/cancel
Response 200: updated Order
Error 422 ORDER_NOT_CANCELLABLE

SSE: GET /api/v1/orders/{id}/events
Events: ORDER_STATUS_CHANGED, LOCATION_UPDATED

POST /api/v1/orders/{id}/review
Body: { rating: number, comment?: string }
```

## Loading State
`SkeletonLoader` (card variant) while initial order detail fetches. Map uses its own loading state from the Goong Maps SDK.

## Empty State
N/A (deep-linked to a specific order).

## Error State
| Error | Display |
|---|---|
| Order not found (404) | `EmptyState` (error variant): "Order not found." + "View orders" CTA |
| Order doesn't belong to user (403) | `EmptyState` (error variant): "You don't have access to this order." |
| Cancel fails (422 ORDER_NOT_CANCELLABLE) | `Toast` (error): "This order can no longer be cancelled." Cancel button hidden. |
| SSE disconnects | `AlertBanner` (warning): "Live updates paused." + "Reconnect" CTA |

## Permissions
`CUSTOMER`. Order must belong to the authenticated user.

## Responsive Notes
- Desktop: TwoColumnLayout, map on right 60%.
- Mobile: Map 240px tall at top; status panel scrolls below.
- Shipper phone "Call" button: `href="tel:+84..."` — native dialler on mobile.

## Accessibility
- `OrderStatusTimeline`: completed steps `aria-current="false"`, current step `aria-current="step"`.
- `LiveStatusBanner`: `role="status"`, `aria-live="polite"`.
- Map: `aria-label="Order delivery map"`, `role="img"` if not interactive.
- Cancel confirmation dialog: `role="alertdialog"`.

## Developer Notes
- **Use explicit `← Back` link to `/orders`**. Do not rely on browser back — it would re-trigger the SSE subscription on re-entry.
- SSE: `const source = new EventSource('/api/v1/orders/:id/events', { withCredentials: true })`.
- On `ORDER_STATUS_CHANGED` event: invalidate React Query `['order', id]` and update timeline.
- On `LOCATION_UPDATED` event: update map shipper pin position directly (do not re-fetch full order).
- On terminal state (DELIVERED, CANCELLED, REJECTED): call `source.close()`.
- Fallback polling: if `EventSource` is not supported or errors, use React Query `refetchInterval: 5000`.
- Review modal: if review already submitted, replace "Rate your order" CTA with star display of submitted rating.
