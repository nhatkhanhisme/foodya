# Shipper Approval

## Purpose
Two-screen workflow: queue listing of pending shipper applications, and individual detail review with approve/reject actions.

> **Note:** The `PATCH /admin/shippers/{id}/reject` endpoint is not yet defined in the SRS §7.7. This is a known gap — screen cannot be fully implemented until the backend adds this endpoint.

## Screen A — Shipper Approval Queue

### Entry Points
- SideNav "Shippers" item
- Dashboard "Review all →" CTA

### Exit Points
- Row "Review →" → Shipper Detail

### Layout
`AdminShell`. Full-width `ApprovalTable`.

### Components
- `TabBar` — [Pending (N)] [Approved] [Rejected] [All]
- `ApprovalTable` columns: Submitted, Name, Email, Vehicle type, License plate, Status, Actions
- `SkeletonLoader`
- `EmptyState`

---

## Screen B — Shipper Detail (Admin)

### Entry Points
- Queue row "Review →" click

### Exit Points
- `← Back` → Shipper Queue
- On approve/reject: redirect to Queue with Toast

### Layout
`AdminShell`. Single-column, `max-width: 720px`.

### Components
- `EntityDetailHeader` (status badge, submitted date, action buttons)
- `PageBreadcrumb`
- Personal information section (name, email, phone, registered date)
- Vehicle information section (type, license plate)
- Prior application history (if any)
- `AdminNoteField` (internal)
- `Button` (Filled) — "Approve ✓"
- `Button` (Destructive Outlined) — "Reject ✕"

## Business Rules
- Approval: `ShipperProfile.status → APPROVED`. Shipper can now accept jobs.
- Rejection: `ShipperProfile.status → REJECTED`. Shipper cannot self-resubmit — must contact support.
- All actions write `AuditLog` (BR-20).
- Approval sends push notification to shipper.
- Rejection reason sent to shipper via notification.

## Validation Rules
- Reject reason: required.

## API Endpoints
```
GET /api/v1/admin/shippers?status=PENDING_APPROVAL&page=&size=
GET /api/v1/admin/shippers/{id}    [implied]

PATCH /api/v1/admin/shippers/{id}/approve
PATCH /api/v1/admin/shippers/{id}/reject    [GAP — not in SRS §7.7]
  Body: { reason: string }
```

## Loading State
Queue: table row skeletons. Detail: section block skeletons.

## Empty State
Queue: "All shipper applications reviewed."

## Error State
- Concurrent action: `AlertBanner`: "Already reviewed by another admin."
- Network: `Toast` (error + Retry).

## Permissions
`ADMIN`.

## Developer Notes
- `ReasonDialog` for rejection: `reasons` = ["License plate could not be verified", "Vehicle type not supported", "Identity inconsistent", "Duplicate account", "Other"]. `noteInternal={false}`.
- Until the reject endpoint is built, disable the "Reject" button with a tooltip: "Rejection endpoint pending — contact engineering."
