# Food Detail (Modal)

## Purpose
Show a menu item's full details, allow quantity and note selection, and add it to the cart. Rendered as a modal overlay on top of Restaurant Detail — not a separate route.

## User Story
As a customer, I want to see a menu item's full description, set my quantity, add a special note, and add it to my cart.

## Entry Points
- Click on any `MenuItemCard` row on Restaurant Detail (not the `+ Add` button — that bypasses the modal)

## Exit Points
- Close button (×) → modal closes, Restaurant Detail unchanged
- "Add to cart" success → modal closes, cart panel updates
- "Update cart" success → modal closes, cart panel updates

## Layout
**Desktop:** Centred modal (`max-width: 560px`, `elevation-4`, `radius-2xl`), scrim behind.
**Mobile:** Bottom sheet (slides up from bottom, full-width, rounded top corners `radius-2xl`).

## Components
- Item photo (320px tall, full-width, `radius-xl` top corners)
- Close button (×) — top-right corner
- Item name (`type-headline-sm`)
- Price (`type-price-lg`, `JetBrains Mono`)
- Description (`type-body-md`, `color-on-surface-variant`)
- `Textarea` — "Any special requests?" (optional, max 200 chars)
- `QuantityStepper` (min 1, max 99)
- `Button` (Filled, Large, full-width) — "Add to cart · ₫X.000" / "Update cart · ₫X.000"
- `AlertBanner` (warning) — unavailable state

## User Actions
1. Read item description.
2. Optionally add a note (per-item note → stored in `CartItem.note`).
3. Adjust quantity with stepper (default: 1 new item; or existing qty if already in cart).
4. Click "Add to cart" or "Update cart".
5. Close modal with × or scrim click.

## Business Rules
- If item already in cart: stepper pre-filled with current quantity; button label = "Update cart".
- Unavailable items: modal opens in read-only mode. No stepper. Button replaced with disabled "Currently unavailable" (Outlined, disabled).
- Price label on button updates in real-time as quantity changes: `quantity × price`.
- Note is optional. If blank, `CartItem.note` is omitted from the API call.

## Validation Rules
- Quantity: integer, min 1, max 99. Enforced by `QuantityStepper` component.
- Note: max 200 characters (enforced by Textarea `maxLength`).

## API Endpoints
```
POST /api/v1/cart/items
Body: { menuItemId: number, quantity: number, note?: string }
Response 201: updated CartItem

PATCH /api/v1/cart/items/{cartItemId}
Body: { quantity: number, note?: string }
Response 200: updated CartItem
```

## Loading State
"Add to cart" button shows spinner and is disabled while request is in flight.

## Empty State
N/A.

## Error State
| Error | Display |
|---|---|
| Network error | `Toast` (error): "Could not add to cart. Try again." Modal stays open. |
| Item no longer available (API 422) | `AlertBanner` (warning) inside modal: "This item is no longer available." Button disabled. |

## Permissions
`CUSTOMER` for cart write operations. Guest: modal opens read-only; "Add to cart" redirects to Login.

## Responsive Notes
- Desktop: centred modal with scrim.
- Mobile: bottom sheet. Photo is 240px tall on mobile.
- Textarea scrollable if content overflows; modal itself scrolls if total content exceeds viewport.

## Accessibility
- `role="dialog"`, `aria-modal="true"`, `aria-labelledby="{item-name-id}"`.
- Focus trapped inside modal when open.
- Escape key closes modal.
- Close button: `aria-label="Close item detail"`.
- `QuantityStepper` buttons: `aria-label="Increase quantity"` / `"Decrease quantity"`.

## Developer Notes
- This is a modal overlay — do not change the URL when opening. Use a `useState` or a React portal.
- "Add to cart" vs "Update cart": check if `cart.items.some(i => i.menuItemId === item.id)`. If yes, use PATCH with the existing `cartItemId`; if no, use POST.
- Real-time price label: `(quantity * item.price)` as integer; formatted with `formatPrice()`.
- On success: close modal, invalidate `['cart']` query, let `CartPanel` re-render.
- For unavailable items: still open modal (do not hide the row); show unavailable state.
