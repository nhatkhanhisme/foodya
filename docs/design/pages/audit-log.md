# Audit Log

## Purpose
Read-only, append-only chronological record of all admin moderation actions. Provides accountability and traceability. No actions can be taken from this screen.

## User Story
As an admin, I want to see a complete history of all moderation actions so I can audit decisions and resolve disputes about past actions.

## Entry Points
- SideNav "Audit Log" item
- Dashboard "View full audit log →" CTA

## Exit Points
- Back → previous screen

## Layout
`AdminShell`. Full-width table. `PageBreadcrumb` (Audit Log).

## Components
- Search input (admin email, target ID, keyword)
- Filter dropdowns: Action type (All / RESTAURANT_APPROVED / USER_BANNED / REFUND_ISSUED / etc.), Date range, Actor (All admins)
- `AuditLogTable` columns: Timestamp, Admin, Action, Target type + ID, Detail
- Row "View →" → expandable inline row detail
- Inline detail: full reason text, `traceId`, complete actor/target/action data
- `PaginatedList`
- `SkeletonLoader`
- `EmptyState`

## Business Rules
- Append-only: no edit, no delete at any level (BR-20).
- No moderation actions from this screen.
- `traceId` (BR-33) shown in row detail for support cross-reference.

## Validation Rules
N/A (read-only).

## API Endpoints
```
GET /api/v1/admin/audit-log?from=&to=&action=&actor=&search=&page=&size=   [GAP]
Response: {
  data: AuditLogEntry[],
  meta: { page, size, total }
}

AuditLogEntry: {
  id, actorUserId, actorEmail, action, targetType, targetId, reason,
  createdAt, traceId
}
```

## Action Type Enum (for filter dropdown)
`RESTAURANT_APPROVED`, `RESTAURANT_REJECTED`, `RESTAURANT_SUSPENDED`, `RESTAURANT_RESTORED`,
`SHIPPER_APPROVED`, `SHIPPER_REJECTED`,
`USER_BANNED`, `USER_UNBANNED`,
`REFUND_ISSUED`, `REFUND_FAILED`,
`REVIEW_REMOVED`,
`DISPUTE_RESOLVED`, `DISPUTE_DISMISSED`

## Loading State
`SkeletonLoader` (table rows × 10).

## Empty State
"No audit log entries for this period."

## Error State
Fetch error: `AlertBanner` (error) + Retry.

## Permissions
`ADMIN`.

## Responsive Notes
Table horizontally scrollable on mobile. Expandable row detail stacks vertically.

## Accessibility
- Table: `role="table"` with proper `<th scope="col">` headers.
- Expandable row: `aria-expanded` on trigger.
- Timestamp: `<time datetime="{ISO}">` element.

## Developer Notes
- Row expand: accordion-style — clicking "View →" in the row expands an inline detail panel below; no modal.
- `traceId` rendered in `type-code` (JetBrains Mono) with a "Copy" icon button.
- Date range defaults to "Last 7 days" on mount.
- No CSV export in MVP (marked as future enhancement in SRS §13).
