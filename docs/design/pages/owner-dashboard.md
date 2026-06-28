# Owner Dashboard

## Purpose
Central operations hub for restaurant owners. Shows revenue metrics, order counts, active order feed, and quick-access navigation.

## User Story
As a restaurant owner, I want to see today's performance and active orders at a glance so I can manage my restaurant efficiently.

## Entry Points
- Post-login redirect (approved Owner role)
- SideNav "Dashboard" item
- Logo click in OwnerShell

## Exit Points
- "View all incoming orders" → `/owner/orders`
- Active order "View →" → `/owner/orders/:orderId`
- Quick-action links → Menu Management, Settings

## Layout
`OwnerShell` (SideNavDrawer). Single-column content area.

## Components
- Restaurant name + open/close toggle (`Toggle` — immediate effect)
- `AlertBanner` (error) — suspension notice (if status = SUSPENDED)
- `DateRangePicker` (Today / This week / Custom)
- `MetricCard` × 3: Revenue (₫), Orders count, Avg rating
- Revenue bar chart
- Active orders mini-feed (top 3 PENDING/CONFIRMED, with "View all →" link)
- `OrderCard` (owner variant, compact) × 3
- Quick-action buttons: [Add menu item] [View reviews] [Update hours]

## Business Rules
- Only APPROVED restaurants access this dashboard.
- SUSPENDED restaurant: `AlertBanner` (error): "Your restaurant has been suspended. Contact support." All order confirmation actions disabled.
- Open/close toggle: immediately changes whether new orders can be placed (PATCH /restaurants/:id/toggle-status). Does not cancel existing active orders.
- Active orders panel shows only PENDING and CONFIRMED orders.

## Validation Rules
N/A.

## API Endpoints
```
GET /api/v1/owner/dashboard?from=&to=
(or equivalent: GET /admin/analytics/overview restricted to owner scope)
Response: { revenue, orderCount, ratingAvg, revenueChart: [] }

GET /api/v1/restaurant/orders?status=PENDING,CONFIRMED&page=1&size=3
Response: OrderSummary[]

PATCH /api/v1/restaurants/{id}/toggle-status
Body: { open: boolean }
```

## Loading State
Each `MetricCard` shows `SkeletonLoader` (text variant) independently. Revenue chart shows `SkeletonLoader` (chart variant).

## Empty State
- No active orders: "No orders in progress right now."
- All metric values 0: display "₫0" and "0" — not an empty state.

## Error State
- Analytics fetch error: individual `MetricCard`s show "—" with a refresh icon.
- Toggle error: `Toast` (error): "Could not update restaurant status. Try again." Toggle reverts.

## Permissions
`OWNER`.

## Responsive Notes
- Desktop: metric cards in a 3-column row.
- Mobile: metric cards stack vertically.
- Revenue chart: scrollable horizontally on mobile if date range is wide.

## Accessibility
- Open/close toggle: `aria-label="Restaurant is {open|closed}"`.
- MetricCard: `aria-live="polite"` on the value (updates on date range change).

## Developer Notes
- Default date range: "Today" (from=today, to=today).
- Open/close toggle: update UI optimistically; revert on error.
- Active orders panel auto-refreshes via 30s poll or SSE event (new order arrives → re-fetch top 3).
- SideNav "Incoming Orders" badge: driven by a separate `useQuery` for PENDING order count, not from this dashboard's data.
