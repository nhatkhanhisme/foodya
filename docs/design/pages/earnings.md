# Earnings

## Purpose
Show a driver's completed delivery history and income summary for a selected date range.

## User Story
As a driver, I want to see my earnings for today and past periods so I can track my income.

## Entry Points
- BottomNav "Earnings" tab
- SideNav "Earnings" item
- Delivery Complete screen "View my earnings" link

## Exit Points
Back to Available Jobs or previous screen.

## Layout
`DriverShell`. Single-column content, `max-width: 720px`.

## Components
- `DateRangePicker` (Today / This week / This month / Custom)
- `MetricCard` × 3: Total earned, Deliveries completed, Avg per delivery
- Delivery list: rows with date, route (restaurant → district), Order ID, payout
- `PaginatedList`
- `SkeletonLoader`
- `EmptyState`

## Business Rules
- Only `DELIVERED` orders appear (exceptions with failures have no payout).
- Payout = `order.shippingFee` (driver receives the shipping fee).

## API Endpoints
```
GET /api/v1/shipper/earnings?from=&to=
Response: {
  totalEarned: number,
  deliveryCount: number,
  avgPerDelivery: number,
  deliveries: [{ orderId, restaurantName, deliveryDistrict, distanceKm, payout, completedAt }]
}
```

## Loading State
`SkeletonLoader` (metric row × 3 + list × 5).

## Empty State
```
Illustration: empty wallet / calendar
Headline: "No deliveries in this period"
Description: "Completed deliveries will appear here."
```

## Error State
`AlertBanner` (error) + Retry.

## Permissions
`DRIVER`.

## Responsive Notes
Metric cards in a 3-column row on desktop; stack on mobile.

## Developer Notes
- Default date range: "Today".
- Payout values are `order.shippingFee` integers. Format with `formatPrice()`.
