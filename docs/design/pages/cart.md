# Cart

## Purpose
Review and edit the current cart before proceeding to checkout. Surfaces unavailable item warnings that would block checkout.

## User Story
As a customer, I want to review my cart items, adjust quantities, and remove items before I place my order.

## Entry Points
- Cart panel "View full cart" CTA on Restaurant Detail (mobile)
- Direct URL: `/cart`
- BottomNav cart icon (if implemented as separate tab)

## Exit Points
- "Proceed to Checkout" → `/checkout`
- Restaurant name/link at top → `/restaurants/:restaurantId`
- "Continue shopping" → browser back / Home
- "Browse restaurants" (empty state) → `/`

## Layout
`CustomerShell`. **`TwoColumnLayout`** on desktop: left = item list, right = `OrderSummaryPanel` (sticky). Single column on mobile.

## Components
- Restaurant name header + link
- `AlertBanner` (warning) — unavailable items present
- `CartItemRow` list (item photo, name, note preview, quantity stepper, price, trash icon)
- Unavailable item row variant (warning border, muted name, "Remove" chip only)
- `Textarea` — order note (optional, for the whole order)
- `OrderSummaryPanel` (subtotal only; delivery fee is "Calculated at checkout")
- `Button` (Filled, Large, full-width) — "Proceed to Checkout" (disabled if unavailable items exist)
- `Button` (Text) — "Continue shopping"

## User Actions
1. Adjust quantities via `QuantityStepper` per item.
2. Remove item via trash icon (ConfirmDialog only if last item in cart).
3. Remove unavailable item (only action available on unavailable rows).
4. Edit order note.
5. Click "Proceed to Checkout".

## Business Rules
- Unavailable items block checkout ("Proceed to Checkout" disabled) (UC-C05 alt 1b).
- Unavailable items are flagged, not silently removed.
- Removing the last item shows a ConfirmDialog: "Your cart will be empty. Continue?" (BR-05 — cart cleared).
- Cart is scoped to one restaurant (BR-05); adding from another restaurant requires clearing this cart.
- Order note is passed at checkout, not stored per cart item in this UI.

## Validation Rules
- Quantity: integer, min 1, max 99. Stepper enforces this.
- Note: max 500 characters.

## API Endpoints
```
GET /api/v1/cart
Response: { restaurantId, restaurantName, items: CartItem[] }

PATCH /api/v1/cart/items/{id}
Body: { quantity: number }

DELETE /api/v1/cart/items/{id}
```

## Loading State
`SkeletonLoader` (list-item variant, × 3) while fetching cart.

## Empty State
```
Illustration: empty bowl
Headline: "Your cart is empty"
Description: "Browse restaurants and add items to get started."
CTA: "Browse restaurants"
```

## Error State
| Error | Display |
|---|---|
| Cart fetch fails | `AlertBanner` (error) with Retry |
| PATCH/DELETE fails | `Toast` (error): "Could not update cart. Try again." UI reverts to previous state. |

## Permissions
`CUSTOMER`.

## Responsive Notes
- Desktop: TwoColumnLayout (items left, summary right 320px sticky).
- Mobile: single column; summary panel below item list; "Proceed to Checkout" full-width button at bottom.
- Checkout button sticks to bottom on mobile when cart has items.

## Accessibility
- Each `CartItemRow`: `role="listitem"`, accessible name from item name.
- Trash button: `aria-label="Remove {itemName} from cart"`.
- Quantity stepper: `aria-label="Quantity for {itemName}"`.
- Unavailable banner: `role="alert"` when it first appears.

## Developer Notes
- Cart state managed in React Query (`['cart']`). All mutations invalidate this query.
- Optimistic update for quantity changes: update UI immediately, revert on API error.
- "Unavailable" detection: `item.isAvailable === false` in cart response.
- Order note stored in component state; not sent to API until POST /orders at checkout.
- Deep-link back to restaurant: use `cart.restaurantId` to construct the URL.
