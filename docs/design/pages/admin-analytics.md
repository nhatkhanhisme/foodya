# Admin Analytics

## Purpose
Detailed platform performance metrics with charts and entity counts over a selectable date range.

## User Story
As an admin, I want to see GMV trends, order volume, and platform entity counts so I can understand platform health over time.

## Entry Points
- SideNav "Analytics" item

## Exit Points
- Entity count links → respective entity lists

## Layout
`AdminShell`. Full-width content area.

## Components
- `DateRangePicker` (Today / 7 days / 30 days / Custom)
- Compare toggle: compare to previous period
- **Top metrics row:** `MetricCard` × 4 (GMV, Orders, Completion Rate, Avg Rating)
- **Orders over time:** line/bar chart (toggle button) with date on X-axis
- **GMV over time:** line chart (separate tab)
- **Rating trend:** line chart (separate tab)
- `TabBar` — [Orders] [GMV] [Rating Trend]
- **Entity counts panel:** active restaurants, pending restaurants, approved shippers, pending shippers, total users, banned users
- **Order status breakdown:** donut chart (Delivered, Cancelled, Rejected, Pending/Active)
- **Cancellation reasons:** horizontal bar chart
- **Fallback distance rate:** single metric (orders using FALLBACK distance_source — BR-19 monitoring)

## Business Rules
- All monetary values in VND integers. Format with `formatPrice()`.
- Date ranges > 90 days: show loading indicator with "Large ranges may take longer" message.
- Completion rate = DELIVERED / (DELIVERED + CANCELLED + REJECTED) × 100.

## Validation Rules
- Custom date range: end ≥ start. Max range: 365 days.

## API Endpoints
```
GET /api/v1/admin/analytics/overview?from=&to=
Response: {
  gmv, orderCount, completionRate, avgRating,
  ordersChart: [{ date, count }],
  gmvChart: [{ date, amount }],
  ratingChart: [{ date, avgRating }],
  entityCounts: { activeRestaurants, pendingRestaurants, approvedShippers, pendingShippers, totalUsers, bannedUsers },
  statusBreakdown: { delivered, cancelled, rejected, active },
  cancellationReasons: [{ reason, count }],
  fallbackDistanceRate: number
}
```

## Loading State
Each chart and metric card loads independently with `SkeletonLoader`.

## Empty State
No data for period: charts show flat line; metrics show 0 or "—".

## Error State
Per-section `AlertBanner` (error) + Retry. Other sections continue rendering.

## Permissions
`ADMIN`.

## Responsive Notes
- Desktop: 4-column metric row; charts full-width.
- Mobile: metrics stack; charts horizontally scrollable.

## Developer Notes
- Use a chart library (e.g., Recharts or Chart.js). Wrap in a component that accepts `data: { date, value }[]`.
- All chart values: monetary fields use `formatPrice()`; counts use `toLocaleString()`.
- Entity count links: "5 pending" → `<a href="/admin/restaurants?status=PENDING">`.
