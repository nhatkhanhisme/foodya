# Disputes

## Purpose
Two-screen workflow: queue of flagged orders and reviews, and individual dispute detail with resolution actions.

> **Note:** The disputes API endpoints (`GET /admin/disputes`, `POST /admin/disputes/:id/resolve`, `POST /orders/:id/report`, `POST /reviews/:id/report`) are not yet defined in the SRS. This screen cannot be implemented until those gaps are resolved.

## User Story
As an admin, I want to review customer and restaurant reports so I can take action to resolve platform disputes fairly.

---

## Screen A — Disputes Queue

### Entry Points
- SideNav "Disputes" item
- Dashboard "Review disputes →" CTA

### Exit Points
- Row "Review →" → Dispute Detail

### Layout
`AdminShell`. Full-width queue list.

### Components
- `TabBar` — [Open (N)] [In Review] [Resolved] [All]
- Type filter: All / Orders / Reviews
- `DisputeCard` list (reported date, type, subject summary, reporter, action button)
- `AlertBanner` (warning) — oldest open dispute age > 24h
- `SkeletonLoader`
- `EmptyState`

---

## Screen B — Dispute Detail

### Entry Points
- Dispute queue "Review →" click

### Exit Points
- `← Back` → Disputes Queue
- Resolution action → Queue with Toast

### Layout
`AdminShell`. `TwoColumnLayout`: left = evidence, right = action panel (sticky).

### Components (Order Dispute)
- `EntityDetailHeader` (dispute type, reported date, status)
- `PageBreadcrumb`
- Dispute overview: reporter, reason, date
- Order detail: OrderItemRow list, subtotal, payment info
- GPS trace table (last 5 location points, distance from address)
- `OrderStatusTimeline` (full history)
- Party contact links (Email customer / Email driver / Email restaurant)
- `AdminNoteField`
- Resolution action buttons:
  - `Button` (Filled) — "Issue refund" (online orders only)
  - `Button` (Outlined) — "Dismiss dispute"
  - `Button` (Destructive Outlined) — "Warn user"
  - `Button` (Destructive Filled) — "Ban user" → User Management Ban flow

### Components (Review Dispute)
- Review validity check panel (shows BR-15 compliance: order exists, DELIVERED, one review)
- Review card (content, rating)
- Customer activity stats (total reviews, 1-star rate, prior bans)
- `AdminNoteField`
- Resolution action buttons:
  - `Button` (Destructive Filled) — "Remove review"
  - `Button` (Outlined) — "Dismiss — review stays"
  - `Button` (Destructive Outlined) — "Warn reviewer"

## Business Rules
- Refund only available for `payment.method = ONLINE` and `payment.status = SUCCESS`.
- COD disputes: `AlertBanner` (info): "No automated refund available for COD orders."
- Refund is best-effort and asynchronous (BR-25). Failure state must be handled.
- Review removal recalculates `restaurant.ratingAvg` (BR-16).
- All resolution actions write `AuditLog` (BR-20).
- Concurrent admin resolution: second admin sees "Already resolved" on their action.

## API Endpoints
```
GET /api/v1/admin/disputes?status=&type=&page=&size=   [GAP]
GET /api/v1/admin/disputes/{id}                         [GAP]
POST /api/v1/admin/disputes/{id}/resolve                [GAP]
  Body: { action: 'REFUND'|'DISMISS'|'REMOVE_REVIEW'|'WARN', reason: string }

POST /api/v1/admin/payments/{id}/refund                 [GAP]
  Body: { amount: number, reason: string }
```

## Permissions
`ADMIN`.

## Developer Notes
- Refund `ConfirmDialog`: shows amount, payment provider, processing time estimate.
- Refund failure state: dispute stays open with "REFUND FAILED" badge. "Retry" CTA calls the refund endpoint again. After 3 failures, show escalation message.
- "Warn user": sends a system notification to the user. Does not change user status.
- "Ban user": navigates to User Detail with ban action pre-triggered (or opens `ReasonDialog` in-page).
