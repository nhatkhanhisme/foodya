# Notifications

## Purpose
Read and act on in-app notifications. Shared across Customer, Owner, and Driver. Each role sees their own notification types.

## User Story
As a user, I want to see all my platform notifications so I can stay informed and navigate quickly to relevant screens.

## Entry Points
- BottomNav bell icon (Customer, Driver)
- SideNav bell icon (Owner, Admin)
- TopNav bell icon (Customer)
- Direct URL: `/notifications` (Customer), `/owner/notifications`, `/shipper/notifications`

## Exit Points
- Notification item tap → role-specific deep link target (see Navigation Map)
- Back → previous screen

## Layout
Role-appropriate shell. Single-column content list, `max-width: 720px` centred.

## Components
- "Mark all read" button (Text, top-right)
- Filter dropdown: All / Orders / Account / System
- `NotificationItem` list
- `PaginatedList` (or infinite scroll)
- `SkeletonLoader` (× 5)
- `EmptyState`

## User Actions
1. Tap notification → mark as read + navigate to deep link.
2. "Mark all read" → all notifications marked read.
3. Filter by type.
4. Paginate.

## Business Rules
- Unread notifications: shown with a blue dot indicator.
- Tapping marks the item read and navigates.
- "Mark all read" marks every notification for this user, across all pages.
- Notifications are not deletable in MVP.

## Notification Types by Role

**Customer:** New order confirmed/rejected, payment confirmed/failed, shipper assigned, order picked up, order delivered, order cancelled.

**Owner:** New order placed (COD or online-paid), order cancelled by customer, shipper picked up, order delivered, restaurant approved/rejected/suspended/restored.

**Driver:** New job available nearby, account approved/rejected, delivery confirmed.

## Validation Rules
N/A.

## API Endpoints
```
GET /api/v1/notifications?page=&size=&type=
Response: { data: Notification[], meta: { page, size, total } }

PATCH /api/v1/notifications/{id}/read
Response 200

PATCH /api/v1/notifications/read-all
Response 200
```

## Loading State
`SkeletonLoader` (list-item variant, × 5).

## Empty State
```
Illustration: bell (empty)
Headline: "You're all caught up"
Description: "New notifications will appear here."
```

## Error State
- Fetch error: `AlertBanner` (error) + Retry.
- Mark-read error: silent retry (optimistic update).

## Permissions
`AUTH`. Each role sees only their own notifications (enforced server-side).

## Responsive Notes
Full-width on mobile. `max-width: 720px` centred on desktop.

## Accessibility
- Unread dot: `aria-label="Unread"` on the indicator.
- Notification timestamp: `<time datetime="{ISO date}">{relative time}</time>`.

## Developer Notes
- Use relative timestamps: "2 min ago", "1 hr ago", "Yesterday", then absolute date for older.
- Optimistic update for "Mark all read": update UI immediately; revert on API error.
- On notification tap: call PATCH to mark read in the background (don't await); navigate immediately.
- Sidebar/BottomNav badge count: managed by a global `useNotificationCount` hook that polls or subscribes to unread count.
