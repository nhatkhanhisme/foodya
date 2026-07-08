# Menu Item Form

## Purpose
Create or update a single menu item. Used for both Add and Edit paths.

## User Story
As a restaurant owner, I want to add or edit a menu item with a photo, price, and category so customers can see it on my menu.

## Entry Points
- Menu Items "+ Add item" → new item form
- Menu Items row "Edit" → edit form (pre-populated)

## Exit Points
- Save success → `/owner/menu/items`
- Cancel → `/owner/menu/items`

## Layout
`OwnerShell`. Single-column form, `max-width: 640px`.

## Components
- `ImageUpload` (drag-drop or click; 1:1 aspect ratio preview; 5MB max; JPEG/PNG)
- `FormField` (item name, required)
- `SelectField` (category, required — fetches from owner's categories)
- `PriceInput` (₫, integer, required)
- `Textarea` (description, optional, 500 char max)
- `Toggle` (Available — default ON for new items)
- `Button` (Outlined) — "Cancel"
- `Button` (Filled) — "Save item"

## Business Rules
- Price must be a positive integer in VND (BR-08).
- Category must belong to the same restaurant.
- Editing an item currently in active orders: allowed; snapshots already captured (BR-18); no warning needed.
- Editing an unavailable item: form pre-populates; availability toggle shows OFF.

## Validation Rules
| Field | Rule |
|---|---|
| Item name | Required, min 2 chars, max 100 chars |
| Category | Required — must select from list |
| Price | Required, integer > 0, max 10,000,000 (₫10M) |
| Description | Optional, max 500 chars |
| Image | Optional; if provided: JPEG/PNG only, max 5MB |

## API Endpoints
```
GET /api/v1/restaurants/{id}/categories  (populates category select)

POST /api/v1/restaurants/{id}/menu-items
Body: { name, categoryId, price, description?, imageUrl?, isAvailable }

PUT /api/v1/restaurants/{id}/menu-items/{itemId}
Body: same shape
```

## Loading State
- Category select: `SkeletonLoader` while fetching categories.
- Form fields populated via GET on edit mode: `SkeletonLoader` (text variant) per field.
- Save button: spinner when request in flight.

## Empty State
N/A.

## Error State
- 400 VALIDATION_ERROR: inline field errors.
- Image upload error: `Toast` (error): "Could not upload image. Try again."
- Network error: `Toast` (error + Retry). Form retains all data.

## Permissions
`OWNER`.

## Responsive Notes
Single column at all breakpoints.

## Accessibility
- `ImageUpload`: visually hidden `<input type="file">` with accessible label.
- Price field: `aria-label="Price in Vietnamese Dong"`.
- Availability toggle: `aria-label="Item is {available|unavailable}"`.

## Developer Notes
- "Edit" mode: fetch item data via GET /restaurants/:id/menu-items/:id (or use cached React Query data). Pre-populate all fields.
- Image upload: upload file first (separate endpoint or pre-signed URL), get back `imageUrl`, then include in form submission.
- Unsaved changes guard: `useBeforeUnload` and React Router `useBlocker` — show "You have unsaved changes. Leave?" on navigation away.
- `PriceInput`: strip non-numeric characters on keydown; store as integer.
