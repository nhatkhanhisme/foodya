# Write Review (Modal)

## Purpose
Collect a 1–5 star rating and optional comment after a delivered order. Rendered as a modal, not a separate route.

## User Story
As a customer whose order was delivered, I want to rate my experience so other customers can benefit from my feedback.

## Entry Points
- Order Tracking (DELIVERED state) → "Rate your order" CTA
- Order History card → "Rate this order" CTA
- Order Detail → "Rate this order" CTA

## Exit Points
- Submit success → modal closes; calling screen updates to show submitted rating
- "Skip" → modal closes; no review created

## Layout
**Desktop:** Centred modal (`max-width: 480px`). **Mobile:** Bottom sheet.

## Components
- Restaurant name + order date (context header)
- `StarRatingInput` (1–5, required)
- `Textarea` (optional comment, max 500 chars)
- `Button` (Text) — "Skip"
- `Button` (Filled) — "Submit review"

## Business Rules
- Only available when `Order.status = DELIVERED` (BR-15).
- One review per order; second attempt returns 422 REVIEW_ALREADY_EXISTS (BR-15).
- Star rating recalculates `Restaurant.ratingAvg` on submission (BR-16).
- Skipping leaves no review; the CTA remains visible for future sessions until a review is submitted.

## Validation Rules
| Field | Rule |
|---|---|
| Rating | Required, integer 1–5 |
| Comment | Optional, max 500 chars |

## API Endpoints
```
POST /api/v1/orders/{id}/review
Body: { rating: number, comment?: string }
Response 201: Review
Error 422 REVIEW_ALREADY_EXISTS
```

## Loading State
Submit button shows spinner and is disabled.

## Empty State
N/A.

## Error State
- 422 REVIEW_ALREADY_EXISTS: `Toast` (error): "You've already reviewed this order." Modal closes.
- Network: `Toast` (error + Retry). Modal stays open.

## Permissions
`CUSTOMER`. Order must be DELIVERED and belong to authenticated user.

## Accessibility
- `role="dialog"`, `aria-modal="true"`, `aria-label="Review your order"`.
- Focus trapped. Escape key triggers "Skip".
- `StarRatingInput`: `role="radiogroup"`, each star `role="radio"`, `aria-label="{N} stars"`.

## Developer Notes
- Modal is not a route. Use a React portal and useState at the calling screen.
- On submit success: close modal; update the calling component to replace the "Rate this order" CTA with a star rating display showing the submitted rating.
- "Skip": close modal without API call.
