# Order Detail

## Purpose
Static, read-only view of a completed or terminal order. Not the live tracking screen — that is Order Tracking.

## User Story
As a customer, I want to view the full details of a past order including items, total, and delivery address.

## Entry Points
- Order History card click (for terminal orders: DELIVERED, CANCELLED, REJECTED)
- "View order details" link from Order Tracking (after delivery)
- Notification deep-link for terminal orders

## Exit Points
- `← Back` → Order History
- "Rate this order" → Write Review modal (if DELIVERED and not yet reviewed)
- "Order again" → `/restaurants/:restaurantId`

## Layout
`CustomerShell`. Single-column content, `max-width: 720px` centred.

## Components
- `StatusBadge` (large, prominent)
- `OrderStatusTimeline` (customer variant, all steps with timestamps)
- Restaurant info (name, link to restaurant page)
- `OrderItemRow` list (snapshots — `item_name_snapshot`, `item_price_snapshot`, quantity)
- `OrderSummaryPanel` (subtotal, shipping fee, total)
- Delivery address display (full)
- `PaymentInfoBadge` (customer variant)
- Cancellation reason display (if CANCELLED — `order.cancelReason`)
- `Button` (Filled) — "Rate this order" (DELIVERED + not reviewed)
- Star rating display (DELIVERED + reviewed)
- `Button` (Outlined) — "Order again" (all terminal states)

## Business Rules
- Item names and prices shown from snapshots, not live menu (BR-18).
- Review CTA shown only when `order.status = DELIVERED` and no existing review (BR-15).
- Cancel reason displayed when `order.status = CANCELLED`.
- `distanceSource = FALLBACK`: show note "Distance estimated (provider unavailable)" below shipping fee (BR-19).

## Validation Rules
N/A (read-only).

## API Endpoints
```
GET /api/v1/orders/{id}
Response: OrderDetail {
  id, status, restaurantId, restaurantName, items: OrderItem[],
  subtotal, shippingFee, distanceKm, distanceSource, total,
  deliveryAddress, payment, cancelReason,
  createdAt, confirmedAt, pickedUpAt, deliveredAt, cancelledAt
}
```

## Loading State
`SkeletonLoader` (card variant) while fetching.

## Empty State
N/A.

## Error State
- 404: `EmptyState` (error): "Order not found." + "View orders" CTA.
- 403: `EmptyState` (error): "You don't have access to this order."

## Permissions
`CUSTOMER`. Order must belong to authenticated user.

## Responsive Notes
Single column at all breakpoints.

## Developer Notes
- This screen is for terminal orders. Active orders (non-terminal) should redirect to Order Tracking.
- Detect on mount: if `order.status` is non-terminal, redirect to `/orders/:id` (Order Tracking).
