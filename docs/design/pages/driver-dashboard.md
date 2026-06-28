# Driver Dashboard — Available Jobs

## Purpose
List of nearby delivery jobs available for the driver to accept. Primary operational screen for approved drivers.

## User Story
As a driver, I want to see delivery jobs near me so I can pick one to accept and earn money.

## Entry Points
- Post-login redirect (approved Driver role)
- BottomNav "Jobs" tab
- "Find more jobs" CTA after delivery completion
- Post-exception "Find more jobs" CTA

## Exit Points
- Accept job successfully → `/shipper/jobs/:orderId/active` (Active Delivery)
- "List view" / "Map view" toggle — stays on same screen, changes layout

## Layout
`DriverShell` (SideNavRail desktop, BottomNav mobile). Full-page list or map toggle.

## Components
- `LocationStatusBanner` (visible only if GPS off/degraded)
- `AlertBanner` (info) — GPS permission prompt on first visit
- Radius selector: [5 km ▾] (2 / 5 / 10 km options)
- List/Map view toggle button
- **List view:** `JobCard` list, sorted by distance ascending
- **Map view:** Goong Maps embed with job pins + bottom sheet of top jobs
- `Button` (Filled) — "Accept delivery" per `JobCard`
- Pull-to-refresh / `Button` (Outlined) — "Refresh"
- `SkeletonLoader` (× 4)
- `EmptyState`

## User Actions
1. (On first visit) Allow GPS permission.
2. Browse available job cards.
3. Adjust radius filter.
4. Toggle list/map view.
5. Tap "Accept delivery" on a job.
6. Pull-to-refresh.

## Business Rules
- Only `ShipperProfile.status = APPROVED` drivers can view and accept jobs (BR-17, 422 SHIPPER_NOT_APPROVED).
- Only `READY_FOR_PICKUP` orders not yet assigned appear here (UC-S02).
- Accept uses optimistic locking (BR-13): first shipper wins.
- If GPS is off: jobs load using a fallback default location; sorting by distance unavailable; `LocationStatusBanner` shown.
- Active delivery guard: if this driver already has an order in `PICKED_UP` state, this screen should redirect to Active Delivery (single active delivery per driver).

## Validation Rules
N/A.

## API Endpoints
```
GET /api/v1/shipper/jobs?lat=&lng=&radiusKm=5
Response: JobSummary[]

PATCH /api/v1/shipper/jobs/{orderId}/accept
Response 200: { orderId, restaurantName, ... }
Error 409: JOB_ALREADY_TAKEN
```

## Loading State
`SkeletonLoader` (card variant, × 4) on initial load and on radius change.

## Empty State
```
Illustration: driver waiting on scooter
Headline: "No deliveries available right now"
Description: "Check back soon — jobs appear when restaurants mark orders ready."
CTA: "Refresh"
```

## Error State
- Fetch error: `AlertBanner` (error) + Retry.
- Accept → 409 JOB_ALREADY_TAKEN: `Toast` (error): "This delivery was already accepted by another driver." Card reverts.
- Accept → network error: `Toast` (error + Retry): "Could not accept job. Check your connection."

## Permissions
`DRIVER`.

## Responsive Notes
- List view: full-width cards at all breakpoints.
- Map view: full-height map with bottom sheet; bottom sheet is pull-up panel.

## Accessibility
- "Accept delivery" button: `aria-label="Accept delivery from {restaurantName}"`.
- Map view job pins: `aria-label="Job from {restaurantName}, {distanceKm} km away"`.

## Developer Notes
- On mount: request geolocation permission if not already granted.
- If permission denied: use HCMC centre as default lat/lng; show `LocationStatusBanner`.
- SSE or 15s auto-poll to keep job list fresh. Jobs claimed by others disappear; new jobs appear.
- On accept success (200): navigate to `/shipper/jobs/:orderId/active`.
- On accept (409): revert optimistic UI; show toast; job card remains (other driver may still cancel).
- Active delivery check on mount: `GET /users/me` or check stored active order state. If active delivery exists, redirect to Active Delivery screen.
