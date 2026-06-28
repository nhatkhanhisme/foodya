# Payment Redirect

## Purpose
Transitional screen shown briefly while the app redirects the customer to the external payment provider's hosted checkout page (VNPay or Momo). Provides reassurance and a cancel option.

## User Story
As a customer paying online, I want to see a clear message before I'm taken to the payment page so I'm not confused by the navigation.

## Entry Points
- Checkout → Online payment → `POST /orders` success → `data.payment.redirectUrl` returned

## Exit Points
- Auto-redirect to provider hosted page after ~1.5s
- "Cancel and go back" → PATCH /orders/:id/cancel → `/orders` (Order History)

## Layout
`AuthShell` (full focus, no navigation). Centred content.

## Components
- Provider logo (VNPay or Momo, 64px)
- `LinearProgressBar` (280px wide, indeterminate)
- Headline: "Redirecting to [Provider]…"
- Security note: "You'll complete payment securely on [Provider]'s website. Foodya never sees your card details."
- `Button` (Text) — "Cancel and go back"

## Business Rules
- Auto-redirect using `window.location.href = redirectUrl` after 1.5s delay.
- Cancel: PATCH /orders/:id/cancel (order is in AWAITING_PAYMENT; no refund needed — UC-C09 alt 0a).
- After cancel: navigate to Order History (not Home; the order now exists in CANCELLED state).

## Validation Rules
N/A.

## API Endpoints
```
PATCH /api/v1/orders/{id}/cancel
(Cancel before redirect completes)

redirectUrl received from POST /orders response (not a separate API call)
```

## Loading State
The entire screen IS a loading/transitional state. `LinearProgressBar` always visible.

## Empty State
N/A.

## Error State
- Cancel fails: `Toast` (error): "Could not cancel. Contact support." The redirect still happens.
- If `redirectUrl` is missing or invalid: `AlertBanner` (error): "Could not initiate payment. Please try again." with "Return to checkout" CTA.

## Permissions
`CUSTOMER`. Order must be in AWAITING_PAYMENT and belong to authenticated user.

## Responsive Notes
Full-screen centred at all breakpoints.

## Accessibility
- `aria-live="polite"`: announces "Redirecting to [Provider]" for screen readers.
- Provider logo: `alt="[Provider] logo"`.

## Developer Notes
- Store `orderId` and `restaurantId` in session storage (or URL params) before redirect. These are needed on the Payment Awaiting screen.
- Redirect: use `window.location.href = redirectUrl` (not `<a target="_blank">`) to navigate in the same tab.
- 1.5s delay: gives the user time to see the screen and optionally cancel.
- `redirectUrl` is received in the `POST /orders` response body (`data.payment.redirectUrl`). Pass it to this screen via router state or URL param.
