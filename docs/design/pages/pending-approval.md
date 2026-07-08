# Pending Approval

## Purpose
Block dashboard access and inform Owner or Driver that their account is under admin review. Automatically transitions when the admin acts.

## User Story
As a newly registered restaurant owner or driver, I want to see my application status so I know what to expect and when I can start using the platform.

## Entry Points
- Automatic redirect after Owner or Driver registration
- Login while `Restaurant.status = PENDING` or `ShipperProfile.status = PENDING_APPROVAL`
- Direct URL: `/pending`

## Exit Points
- Admin approves → Approval screen → role home (Owner: `/owner/dashboard`, Driver: `/shipper/jobs`)
- Admin rejects → Rejection screen (same URL, different state)
- "Log out" → `/auth/login`

## Layout
`AuthShell` (no sidebar, no nav — full focus on status).

## Components

### Pending State
- `PendingApprovalGate`
  - Illustration (hourglass / waiting)
  - Headline: "Your [restaurant/account] is under review"
  - Steps list: "What we're checking" (role-specific content)
  - Contact support link
  - `Button` (Text) — "Log out"

### Approved State (transition)
- Success illustration (animated checkmark)
- Headline: "You're approved!"
- Role-specific next steps list
- `Button` (Filled) — "Go to my Dashboard" / "Start finding jobs"

### Rejected State
- Alert illustration
- Headline: "Your application was not approved"
- Reason text (from admin) shown in a bordered box
- `Button` (Filled) — "Edit and resubmit" (Owner only)
- Link: "Contact support"
- `Button` (Text) — "Log out"

## User Actions
- Wait (no active actions in Pending state).
- Log out.
- On rejection: read reason, click "Edit and resubmit" (Owner) or contact support (Driver).

## Business Rules
- `RESTAURANT_OWNER` with `Restaurant.status = PENDING` → show pending gate.
- `RESTAURANT_OWNER` with `Restaurant.status = REJECTED` → show rejection state.
- `SHIPPER` with `ShipperProfile.status = PENDING_APPROVAL` → show pending gate.
- `SHIPPER` with `ShipperProfile.status = REJECTED` → show rejection state; no self-resubmit path.
- Any attempt to navigate to `/owner/*` or `/shipper/*` while pending redirects here.

## Validation Rules
N/A.

## API Endpoints
```
GET /api/v1/users/me
Used to poll approval status every 60 seconds.
```

For Owner rejection resubmit:
```
PUT /api/v1/restaurants/{id}
Body: updated restaurant fields
```

## Loading State
On auto-transition (status changed detected by poll): `LinearProgressBar` at top of screen while navigating.

## Empty State
N/A.

## Error State
- Poll fails: silent retry on next interval (do not show error for poll failure).
- Resubmit API fails: `AlertBanner` above resubmit form.

## Permissions
- `OWNER_PENDING` or `DRIVER_PENDING` (any RESTAURANT_OWNER or SHIPPER regardless of approval status).
- Redirect approved users to their dashboard immediately.
- Redirect ADMIN and CUSTOMER to their home if they somehow land here.

## Responsive Notes
Full-width layout at all sizes. Centred card. All breakpoints identical.

## Accessibility
- Pending state: `role="status"` with live region updates when status changes.
- Approved transition: `aria-live="assertive"` to announce "Your account has been approved".

## Developer Notes
- Poll `GET /users/me` every 60s using `setInterval`; clear on unmount.
- On status change detection: animate transition from Pending to Approved/Rejected state without page reload.
- Owner "Edit and resubmit": pre-populate the registration Step 2 form with existing restaurant data. Show the rejection reason at top of the form.
- Driver rejection: "Contact support" opens `mailto:support@foodya.vn?subject=Shipper%20Rejection%20Appeal&body=Account%20ID:%20{userId}`.
