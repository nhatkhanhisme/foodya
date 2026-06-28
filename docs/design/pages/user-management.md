# User Management

## Purpose
Two-screen workflow: searchable user list, and individual user detail with ban/unban actions.

## User Story
As an admin, I want to search for users, review their activity, and ban or unban them to maintain platform safety.

---

## Screen A — User Management List

### Entry Points
- SideNav "Users" item

### Exit Points
- Row "View →" → User Detail

### Layout
`AdminShell`. Full-width `DataTable` with search and filters.

### Components
- Search input (name, email, or phone)
- Filter dropdowns: Role (All / Customer / Owner / Shipper), Status (All / Active / Banned)
- `DataTable` columns: ID, Name, Email, Role, Status, Joined, Actions
- `StatusBadge` (Active / Banned) per row
- `Button` (XSmall, Destructive Outlined) — "Ban" (quick action from row, Active users only)
- `Button` (XSmall, Filled Tonal) — "Unban" (quick action from row, Banned users only)
- `SkeletonLoader`
- `PaginatedList`

---

## Screen B — User Detail

### Entry Points
- User list row "View →" click

### Exit Points
- `← Back` → User Management
- "Ban" / "Unban" action → refreshes this screen

### Layout
`AdminShell` with breadcrumb (Users / [Full Name]).

### Components
- `EntityDetailHeader` (status badge, member since, action buttons)
- `PageBreadcrumb`
- Account information section
- Activity summary (order count, completion rate, cancel count, reviews, disputes)
- Recent orders table (last 5)
- Moderation history (AuditLog entries targeting this user)
- `Button` (Destructive Filled) — "Ban user" (ACTIVE users)
- `Button` (Filled Tonal) — "Unban user" (BANNED users)
- `AlertBanner` (warning) — if user has active orders in progress when banning

## Business Rules
- `User.status`: `ACTIVE` (default) or `BANNED` (BR-30).
- Ban blocks login immediately (next refresh token call fails — BR-03).
- Banning does not auto-cancel active orders.
- Banning a Restaurant Owner: warn that their restaurant may still appear live.
- Banning a Driver with active delivery: warn that the delivery is in progress.
- Unban endpoint not yet defined in SRS §7.7 — flagged as a gap.
- All ban/unban actions write `AuditLog` (BR-20).
- Ban reason is internal only (not sent to user).

## Validation Rules
- Ban reason: required freetext in `ReasonDialog`.
- Unban reason: required freetext in `ReasonDialog`.

## API Endpoints
```
GET /api/v1/admin/users?status=&role=&search=&page=&size=
GET /api/v1/admin/users/{id}     [implied]

PATCH /api/v1/admin/users/{id}/ban
Body: { reason: string }

PATCH /api/v1/admin/users/{id}/unban    [GAP — not in SRS §7.7]
Body: { reason: string }
```

## Loading State
List: `SkeletonLoader` (table rows × 10). Detail: `SkeletonLoader` (section blocks).

## Empty State
No results: "No users match your search."

## Error State
- Ban/Unban concurrent: `AlertBanner`: "User status was already changed by another admin."
- Network: `Toast` (error + Retry).

## Permissions
`ADMIN`.

## Responsive Notes
Table horizontally scrollable on mobile. Detail single column on mobile.

## Accessibility
- Ban button: `aria-label="Ban {fullName}"`. Unban button: `aria-label="Unban {fullName}"`.
- `StatusBadge`: `role="status"`.

## Developer Notes
- Ban `ReasonDialog`: `noteInternal={true}` — "Internal reason — not sent to user." Predefined: "Fraudulent reviews", "Abusive behaviour", "Payment fraud", "Bad-faith cancellations", "Suspected fake account", "Other".
- Self-ban guard: if admin is about to ban their own `userId`, show additional `ConfirmDialog`: "You are about to ban your own account. You will be logged out."
- `AlertBanner` for active orders/delivery: check `user.activeOrderCount > 0` or `user.activeDelivery` in the user detail response.
