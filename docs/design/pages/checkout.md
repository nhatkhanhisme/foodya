# Checkout

## Purpose
Final step before order creation. Customer selects delivery address, payment method, and confirms the order. COD orders go directly to Order Tracking; online orders go to Payment Redirect.

## User Story
As a customer, I want to confirm my delivery address, choose how to pay, and place my order.

## Entry Points
- Cart → "Proceed to Checkout"

## Exit Points
- COD order → `/orders/:orderId` (Order Tracking)
- Online order → `/payment/redirect/:orderId`
- Back button → `/cart`
- "Add new address" (modal) → stays on Checkout after save

## Layout
`CustomerShell` (no BottomNav on this screen — focus mode). **`TwoColumnLayout`**: form left, `OrderSummaryPanel` right (sticky 320px).

## Components
- **Section 1: Delivery Address**
  - `AddressSelector` (radio card list of saved addresses + "Add new address" button)
  - `AlertBanner` (info) if no address exists
  - Address Form in `FormDialog` (triggered by "Add new address")
- **Section 2: Payment Method**
  - `RadioGroup` — COD / Online
  - Provider logo buttons (VNPay, Momo) — visible when Online selected
- **Section 3: Review Items (accordion)**
  - Collapsed by default; shows restaurant + subtotal
  - Expands to `OrderItemRow` list
- **Order Summary Panel** (right column / bottom on mobile)
  - Restaurant name
  - Item rows with quantities
  - Subtotal, delivery fee (computed from selected address), total
  - `Button` (Filled, Large, full-width) — "Place order"
  - Terms note below button

## User Actions
1. Select delivery address.
2. (Optional) Add new address via form dialog.
3. Select payment method: COD or Online.
4. If Online: select provider (VNPay or Momo).
5. Review order summary.
6. Click "Place order".

## Business Rules
- Cannot proceed without at least one saved address.
- Restaurant must be open at time of submit (BR-06). If closed, API returns 422 RESTAURANT_CLOSED.
- All items must be available at submit (BR-06). If not, API returns 422 ITEMS_UNAVAILABLE.
- Delivery fee = base fee + (road_distance_km × per_km_rate) (BR-07). Computed server-side; never client-supplied.
- Total displayed is informational. Server always recomputes and sets the authoritative total (BR-08).
- COD: Order created with `status = PENDING`; cart cleared (BR-23).
- Online: Order created with `status = AWAITING_PAYMENT`; cart cleared; redirect URL returned.

## Validation Rules
| Condition | Result |
|---|---|
| No address selected | "Place order" button disabled |
| No payment method selected | "Place order" button disabled |
| Online selected, no provider selected | "Place order" button disabled |

## API Endpoints
```
GET /api/v1/users/me/addresses
Response: Address[]

POST /api/v1/users/me/addresses
Body: AddressInput
(used by "Add new address" modal on this screen)

POST /api/v1/orders
Body (COD):    { addressId, paymentMethod: 'COD', note? }
Body (Online): { addressId, paymentMethod: 'ONLINE', provider: 'VNPAY'|'MOMO', note? }

Response 201 (COD):    { id, status: 'PENDING', subtotal, shippingFee, total, payment }
Response 201 (Online): { id, status: 'AWAITING_PAYMENT', ..., payment: { redirectUrl } }

Error 422 RESTAURANT_CLOSED:     Show AlertBanner
Error 422 ITEMS_UNAVAILABLE:     Show AlertBanner + list of items; redirect to /cart
```

## Loading State
- Address list: `SkeletonLoader` (list-item × 3) while fetching.
- "Place order" button: spinner + disabled while API call is in flight.
- Delivery fee: shows "Calculating…" until address is selected and fee is fetched.

## Empty State
No saved addresses:
```
AlertBanner (info): "You need a delivery address to place an order."
Button (Filled): "+ Add your first address"
```
"Place order" is disabled until an address exists.

## Error State
| Error | Display |
|---|---|
| 422 RESTAURANT_CLOSED | `AlertBanner` (error): "This restaurant is currently closed." |
| 422 ITEMS_UNAVAILABLE | `AlertBanner` (error) listing affected items + "Return to cart" CTA |
| Network error | `Toast` (error + Retry): "Could not place order. Check your connection." |

## Permissions
`CUSTOMER`. Route guard also validates cart is non-empty (redirect to `/cart` if empty).

## Responsive Notes
- Desktop: `TwoColumnLayout`, sticky summary on right.
- Mobile: single column; address selector collapses to a radio list; summary + CTA at bottom.
- "Place order" button full-width and fixed at bottom on mobile (above safe area).

## Accessibility
- Address selector: `role="radiogroup"`, each address card `role="radio"`, `aria-checked`.
- Payment method: `role="radiogroup"`.
- Provider buttons: `role="radio"` within the Online group.
- "Place order" button: `aria-busy="true"` when loading.

## Developer Notes
- Delivery fee: re-fetch fee whenever selected address changes (the distance changes). Consider debouncing.
- "Add new address" opens `AddressForm` inside a `FormDialog`. On success: close dialog, refresh address list, auto-select the new address.
- On POST /orders success (COD): navigate to `/orders/:orderId`.
- On POST /orders success (Online): navigate to `/payment/redirect/:orderId?url={encodeURIComponent(redirectUrl)}`.
- The order note from `/cart` page (if any) should be pre-populated in a hidden note field here and sent with the order.
- Do not re-enable the back button on mobile during the POST /orders request (prevents duplicate order creation).
