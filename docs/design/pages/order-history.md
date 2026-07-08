# Order History

## Purpose
Paginated list of all past and active orders with status-contextual actions.

## User Story
As a customer, I want to see all my orders so I can track active ones, reorder past ones, and leave reviews.

## Entry Points
- BottomNav "Orders" tab
- "View all orders" links
- Notification deep-links (fallback when orderId is missing)

## Exit Points
- Active order card → `/orders/:orderId` (Order Tracking)
- Terminal order card → `/orders/:orderId` (Order Detail)
- "Track order" → `/orders/:orderId`
- "Complete payment" → `/payment/awaiting/:orderId`
- "Order again" → `/restaurants/:restaurantId`
- "Rate this order" → Write Review modal

## Layout
`CustomerShell`. Single-column content list, `max-width: 720px` centred.

## Components
- `TabBar` — [All] [Active] [Delivered] [Cancelled]
- `OrderCard` (customer variant) list
- Write Review modal (in-page; opened via "Rate this order")
- `PaginatedList`
- `SkeletonLoader` (× 5)
- `EmptyState`

## User Actions
1. Filter by status tab.
2. Tap order card → navigate to Tracking or Detail.
3. Tap "Rate this order" → opens Write Review modal.
4. Tap "Order again" → navigates to restaurant.
5. Tap "Complete payment" → navigates to Payment Awaiting.

## Business Rules
- Active orders (non-terminal) show a `●LIVE` badge + "Track order" CTA.
- DELIVERED + not reviewed: "Rate this order" CTA shown.
- DELIVERED + reviewed: star rating displayed in card.
- CANCELLED / REJECTED: "Order again" CTA shown.
- AWAITING_PAYMENT: "Complete payment" CTA shown (re-enters payment flow).
- "Order again": attempts to re-add same items to cart. If any items are soft-deleted, show `Toast` (warning): "Some items are no longer available."

## Validation Rules
N/A.

## API Endpoints
```
GET /api/v1/orders?page=&size=&status=
Response: { data: OrderSummary[], meta: { page, size, total } }

POST /api/v1/orders/{id}/review
Body: { rating: number, comment?: string }
```

## Loading State
`SkeletonLoader` (list-item variant, × 5) while fetching.

## Empty State
```
Variant by active tab:

All:
  Illustration: empty receipt
  Headline: "No orders yet"
  CTA: "Browse restaurants"

Active:
  Headline: "No active orders"
  Description: "Your orders in progress will appear here."

Delivered:
  Headline: "No delivered orders"

Cancelled:
  Headline: "No cancelled orders"
  Secondary CTA: "Clear filter" (Text button)
```

## Error State
- Fetch error: `AlertBanner` (error) + Retry.

## Permissions
`CUSTOMER`.

## Responsive Notes
- Full-width on mobile; `max-width: 720px` on desktop.
- Order cards full-width at all breakpoints.

## Accessibility
- `TabBar`: `role="tablist"`, each tab `role="tab"`, `aria-selected`.
- Active order `●LIVE` badge: `aria-label="Order is live"`.

## Developer Notes
- Tab filter maps to `?status=` query param: All → no param, Active → `PENDING,CONFIRMED,READY_FOR_PICKUP,PICKED_UP`, Delivered → `DELIVERED`, Cancelled → `CANCELLED,REJECTED`.
- "Order again" flow: POST /cart/items for each item in the historical order. Navigate to `/restaurants/:restaurantId` after. If cart already has items from a different restaurant, show conflict dialog first.
- Write Review modal: same component as on Order Tracking. On submit success, optimistically update the card to show submitted rating; do not re-fetch the full list.
