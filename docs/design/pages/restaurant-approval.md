# Restaurant Approval

## Purpose
Two-screen workflow: queue listing of pending restaurants, and individual detail review with approve/reject/suspend actions.

## User Story
As an admin, I want to review pending restaurant applications and approve or reject them so only quality restaurants go live.

---

## Screen A — Restaurant Approval Queue

### Entry Points
- SideNav "Restaurants" item
- Dashboard "Review all →" CTA

### Exit Points
- Row "Review →" → Restaurant Detail (Admin)

### Layout
`AdminShell`. Full-width DataTable with filter tabs.

### Components
- `TabBar` — [Pending (N)] [Approved] [Rejected] [Suspended] [All]
- Search input
- Sort dropdown (Newest first / Oldest first)
- `ApprovalTable` columns: Submitted, Restaurant, Owner email, Address, Status, Actions
- `AlertBanner` (warning) — stale items (> 48h pending)
- `SkeletonLoader`
- `EmptyState`

### API Endpoints
```
GET /api/v1/admin/restaurants?status=PENDING&search=&page=&size=
```

---

## Screen B — Restaurant Detail (Admin)

### Entry Points
- Queue row "Review →" click

### Exit Points
- `← Back` → Queue
- On approve/reject/suspend: redirect to Queue with Toast

### Layout
`AdminShell` with breadcrumb (Restaurants / [Restaurant Name]). `TwoColumnLayout`: left = entity info, right = action panel (sticky).

### Components
- `EntityDetailHeader` (status badge, submitted date, action buttons)
- `PageBreadcrumb`
- Restaurant details section (name, description, phone)
- Address + `MapPicker` (readOnly=true) — view pin on map
- Opening hours table (read-only)
- Owner information section
- Review history section (prior submissions if any)
- `AdminNoteField` (internal only)
- `Button` (Filled) — "Approve ✓"
- `Button` (Destructive Outlined) — "Reject ✕"
- `Button` (warning variant) — "Suspend" (APPROVED restaurants only)
- `Button` (Filled Tonal) — "Lift suspension" (SUSPENDED restaurants only)
- `AlertBanner` (info) — warning if owner account is BANNED

## Business Rules (both screens)
- Only PENDING → APPROVED or REJECTED transitions available for new restaurants.
- APPROVED → SUSPENDED for policy violations (UC-A06).
- SUSPENDED → APPROVED to restore.
- All actions write `AuditLog` (BR-20).
- Rejection requires a reason (sent to owner via notification).
- Approval sends push notification to owner.
- Concurrent admin actions on same entity → 422 on the second action.

## API Endpoints
```
GET /api/v1/admin/restaurants?status=&search=&page=&size=
GET /api/v1/admin/restaurants/{id}

PATCH /api/v1/admin/restaurants/{id}/approve
PATCH /api/v1/admin/restaurants/{id}/reject
Body: { reason: string, codes: string[] }

PATCH /api/v1/admin/restaurants/{id}/suspend    [GAP — not in SRS §7.7]
Body: { reason: string }
```

## Loading State
Queue: `SkeletonLoader` (table rows × 5). Detail: `SkeletonLoader` (section blocks).

## Empty State
Queue empty (all tabs): "All restaurant applications reviewed."

## Error State
- Action fails (concurrent): `AlertBanner` (warning): "This restaurant was already reviewed by another admin."
- Network: `Toast` (error + Retry).

## Permissions
`ADMIN`.

## Responsive Notes
- Queue table: horizontally scrollable on mobile.
- Detail: single column on mobile.

## Accessibility
- Action buttons: `aria-label="Approve {restaurantName}"`, `"Reject {restaurantName}"`.
- Stale alert: `role="alert"`.

## Developer Notes
- `ReasonDialog` for rejection: `reasons` = ["Incomplete address", "Address unverifiable", "Hours missing", "Duplicate listing", "Suspected fraud", "Other"]. `noteInternal={false}` (sent to owner).
- Suspend `ReasonDialog`: `noteInternal={false}` (reason sent to owner) + `warningText="The restaurant will be hidden from customers immediately."`.
- `AdminNoteField`: auto-save on blur (PATCH to a notes endpoint or store in AuditLog metadata).
