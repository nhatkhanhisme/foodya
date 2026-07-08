# Payment Awaiting

## Purpose
Post-payment-redirect screen that waits for a webhook confirmation via SSE. Shows countdown timer, transitions to success or failure state automatically.

## User Story
As a customer who has just paid online, I want to see a clear confirmation when my payment is processed (or an explanation if it fails).

## Entry Points
- Redirect back from VNPay or Momo hosted checkout page
- Order History card with `status = AWAITING_PAYMENT` → "Complete payment" CTA

## Exit Points
- Payment success → `/orders/:orderId` (Order Tracking)
- Payment failed / timeout → Order History (via "Place new order" CTA → Home)
- "Cancel" (available during redirect phase) → PATCH /orders/:id/cancel → Order History

## Layout
`AuthShell` (focus mode — no TopNav, no BottomNav). Centred content.

## Components

### Waiting State (default)
- `Spinner` (xl, `color-primary`)
- Headline: "Confirming your payment…"
- Order ID + amount (`type-body-md`, muted)
- `CountdownTimer` (15 minutes total, BR-24; warning at ≤ 60s)
- Body: "Your order is waiting for payment confirmation. Do not close this tab."

### Success State
- Animated checkmark (SVG, `motion-success-check`, `color-success`)
- Headline: "Payment confirmed!"
- Body: "Your order has been sent to [RestaurantName]."
- `Button` (Filled) — "Track your order" → `/orders/:orderId`

### Failure State (failed webhook or timeout)
- Error icon (`color-error`)
- Headline: "Payment was not completed"
- Body: "Your order has been cancelled. Your cart items were not restored."
- `Button` (Filled) — "Order again" → `/restaurants/:restaurantId`
- `Button` (Text) — "View order history" → `/orders`

## Business Rules
- SSE subscribed immediately on page load.
- Countdown: 15 minutes from order creation (BR-24). When countdown hits 0 and no SSE event yet received, transition to failure state (UI-side).
- Cart is NOT restored on payment failure (BR-29).
- "Order again" creates a new order — it does not retry the failed one (BR-29).
- "Cancel" on redirect screen: PATCH /orders/:id/cancel before redirect completes (order is in AWAITING_PAYMENT, no payment to refund — UC-C09 alt 0a).

## Validation Rules
N/A.

## API Endpoints
```
SSE: GET /api/v1/orders/{id}/events
Events:
  ORDER_STATUS_CHANGED { status: 'PENDING' }   → success
  ORDER_STATUS_CHANGED { status: 'CANCELLED' } → failure

GET /api/v1/orders/{id}/payment  (polling fallback, every 5s)
Response: { status: PaymentStatus, method, provider }

PATCH /api/v1/orders/{id}/cancel  (cancel before redirect only)
```

## Loading State
The Waiting State IS the loading state for this screen. Spinner always visible in waiting state.

## Empty State
N/A.

## Error State
- SSE connection drops: fall back to polling `GET /orders/:id/payment` every 5s.
- Countdown reaches 0 before confirmation: transition to failure state locally.

## Permissions
`CUSTOMER`. Order must belong to the authenticated user and be in `AWAITING_PAYMENT` status.

## Responsive Notes
Full-screen centred content. Identical at all breakpoints.

## Accessibility
- Waiting state: `role="status"`, `aria-live="polite"` for status updates.
- Success/failure transitions: `aria-live="assertive"`.
- CountdownTimer: `aria-label="Payment expires in {minutes}:{seconds}"`, updated every second.

## Developer Notes
- Store `orderId` and `restaurantId` (for "Order again" CTA) in URL params or session storage before redirect.
- `CountdownTimer`: calculate from `order.createdAt` + 15min, not from page load time. The customer may have spent time on the provider page.
- On SSE `ORDER_STATUS_CHANGED` with `status = PENDING`: animate to success state, then auto-navigate to Order Tracking after 1.5s delay.
- On SSE `ORDER_STATUS_CHANGED` with `status = CANCELLED`: animate to failure state.
- On timeout: the backend scheduler independently cancels (BR-24). The SSE event for CANCELLED will arrive around the same time as or after the local countdown. Handle whichever comes first.
- Block browser back button on this page: use `history.pushState` or navigation guards to prevent going back to Checkout.
