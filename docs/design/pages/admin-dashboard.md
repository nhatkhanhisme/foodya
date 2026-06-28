# Admin Dashboard

## Purpose
Central platform oversight screen. Shows key metrics, pending approval queue counts, recent audit activity, and open disputes at a glance.

## User Story
As an admin, I want to see the platform health and any pending items that need my attention so I can prioritise my work.

## Entry Points
- Post-login redirect (Admin role)
- SideNav logo click / "Dashboard" item

## Exit Points
- "Review all" (restaurants) → `/admin/restaurants`
- "Review all" (shippers) → `/admin/shippers`
- "Review disputes" → `/admin/disputes`
- "View full audit log" → `/admin/audit-log`
- Individual metric cards → linked entity lists

## Layout
`AdminShell` (SideNavDrawer). Single-column content area.

## Components
- Date range selector (Today / 7 days / 30 days / Custom)
- `MetricCard` × 4: GMV, Orders, Active Restaurants, Active Shippers
- **Pending Approval Queues panel:**
  - Restaurant row: count + "Review all →"
  - Shipper row: count + "Review all →"
- **Recent Activity panel** (last 5 AuditLog entries)
  - Timestamp, admin name, action, target
  - "View full audit log →"
- **Open Disputes panel:**
  - Count of unresolved disputes
  - Oldest dispute age
  - "Review disputes →"

## Business Rules
- Dashboard is informational only — no moderation actions from this screen.
- Sidebar badges for Restaurants, Shippers, Disputes kept in sync with queue counts.

## Validation Rules
N/A.

## API Endpoints
```
GET /api/v1/admin/analytics/overview?from=&to=
Response: { gmv, orderCount, activeRestaurants, activeShippers, chart: [] }

GET /api/v1/admin/restaurants?status=PENDING&size=1
Response: { meta: { total } }  (used for pending count only)

GET /api/v1/admin/shippers?status=PENDING_APPROVAL&size=1
Response: { meta: { total } }

GET /api/v1/admin/disputes?status=OPEN&size=1
Response: { meta: { total } }

GET /api/v1/admin/audit-log?size=5
Response: AuditLogEntry[]
```

## Loading State
Each panel and metric card loads independently. `SkeletonLoader` per section.

## Empty State
- All queues clear: "All queues clear ✓" in the approval panel.
- No disputes: "No open disputes."
- No recent activity: "No recent admin actions."

## Error State
- Fetch errors per card: show "—" with refresh icon. Panels show `AlertBanner` (error).

## Permissions
`ADMIN`.

## Responsive Notes
- Desktop: 4-column metric row; panels below as 2-column (Queues + Recent Activity / Disputes).
- Mobile: all sections stack vertically.

## Developer Notes
- Date range change triggers re-fetch of analytics only (queue counts and audit log are always "current").
- Pending count badges in SideNav share the same React Query cache as the dashboard panels.
