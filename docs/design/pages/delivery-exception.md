# Delivery Exception

## Purpose
Structured form for reporting a failed delivery. Confirms the reason, triggers order cancellation, and alerts admin for follow-up.

## User Story
As a driver, I want to report that I couldn't deliver an order (customer unreachable, refused, wrong address) so the order is cancelled properly and I'm cleared to take new jobs.

## Entry Points
- Active Delivery → "Delivery problem" button

## Exit Points
- Exception submitted → Delivery Complete (exception variant) → Available Jobs
- "← Back — try again" → Active Delivery (encourages one more attempt before reporting)

## Layout
`AuthShell` equivalent (focus mode, no distracting nav). Full-page form.

## Components
- `RadioGroup` — reason selection (required)
- `Textarea` — additional note (optional)
- `AlertBanner` (warning) — "This will cancel the order. Try reaching the customer first."
- `Button` (Outlined) — "← Back — try again"
- `Button` (Destructive Filled) — "Report problem"
- Final `ConfirmDialog` — "Cancel this delivery? This cannot be undone."

## Reason Options
| ID | Label | Displayed description |
|---|---|---|
| `UNREACHABLE` | Customer is unreachable | "I tried calling and waiting but no response." |
| `REFUSED` | Customer refused the order | "Customer was present but declined to accept." |
| `WRONG_ADDRESS` | Address incorrect or inaccessible | "I cannot locate the delivery address." |
| `OTHER` | Other | (freetext note required) |

## Business Rules
- Requires reason selection before "Report problem" is enabled.
- If "OTHER" selected, note field becomes required.
- Order → CANCELLED (BR-14); admin notified.
- Online orders: refund triggered automatically (BR-25).
- COD orders: Payment stays PENDING → void (cash never collected).
- No earnings recorded for exception deliveries.

## API Endpoints
```
PATCH /api/v1/shipper/orders/{id}/deliver
Body: {
  exception: true,
  reason: 'UNREACHABLE' | 'REFUSED' | 'WRONG_ADDRESS' | 'OTHER',
  note?: string
}
Response 200: Order status → CANCELLED
```

## Loading State
"Report problem" button shows spinner while request is in flight.

## Empty State
N/A.

## Error State
- Network: `Toast` (error): "Could not report problem. Check connection and try again." Screen stays open.

## Permissions
`DRIVER`. Order must be in PICKED_UP status assigned to this driver.

## Responsive Notes
Full-page centred form at all breakpoints.

## Accessibility
- RadioGroup: `role="radiogroup"`, `aria-required="true"`.
- ConfirmDialog: `role="alertdialog"`.

## Developer Notes
- "OTHER" reason: make note textarea required when OTHER is selected (controlled via `watch` in React Hook Form).
- On success: navigate to an intermediate "Exception reported" screen (same pattern as Delivery Complete), then to Available Jobs after 2s or on button tap.
- Block browser back after submission is confirmed.
