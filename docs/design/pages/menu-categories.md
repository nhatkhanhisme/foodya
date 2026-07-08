# Menu — Categories

## Purpose
CRUD interface for menu categories. Owner can add, edit, delete, and reorder categories.

## User Story
As a restaurant owner, I want to organise my menu into categories so customers can browse it easily.

## Entry Points
- SideNav Menu → Categories tab
- Quick-action "Add menu item" (navigates here first if no categories exist)

## Exit Points
- "Items" tab → `/owner/menu/items`
- `← Back` → Owner Dashboard

## Layout
`OwnerShell`. Single-column, `max-width: 720px`.

## Components
- `TabBar` — [Categories] [Items]
- "Add category" button (Filled Tonal)
- `DraggableList` of category rows (drag handle, name, item count, Edit, Delete)
- Inline edit form (name field + Save / Cancel) — appears inline in list row
- `Button` (Filled) — "Save order" (appears when reorder has happened)
- `ConfirmDialog` — delete (with item count check)
- `SkeletonLoader`
- `EmptyState`

## User Actions
1. Add category (inline form at top of list).
2. Edit category name (inline form in row).
3. Delete category (ConfirmDialog, blocked if items exist).
4. Drag rows to reorder.
5. Save reorder.

## Business Rules
- Delete blocked if category has items (enforced server-side — FK RESTRICT §6.4). UI warns: "Reassign or delete all items in this category first."
- Reorder: `PATCH /restaurants/:id/categories/reorder` with ordered array of IDs.
- Display order on the public menu matches this order.

## API Endpoints
```
GET /api/v1/restaurants/{id}/menu  (fetches categories with item counts)
POST /api/v1/restaurants/{id}/categories
  Body: { name: string }
PUT /api/v1/restaurants/{id}/categories/{catId}
  Body: { name: string }
DELETE /api/v1/restaurants/{id}/categories/{catId}
  Error 422 if items exist
PATCH /api/v1/restaurants/{id}/categories/reorder
  Body: { orderedIds: number[] }
```

## Loading State
`SkeletonLoader` (list-item × 4).

## Empty State
```
Headline: "No categories yet"
Description: "Add categories to organise your menu."
CTA: "Add your first category"
```

## Error State
- Delete blocked: `Toast` (error): "Remove all items in this category first."
- Save error: `Toast` (error + Retry).

## Permissions
`OWNER`.

## Accessibility
- Drag handle: `aria-grabbed`, `aria-dropeffect`. Keyboard-accessible drag via arrow keys.

## Developer Notes
- Duplicate category name check: server returns 409 DUPLICATE_RESOURCE. Show inline error: "A category with this name already exists."
- "Save order" button: appears only after drag; triggers PATCH; hides after save.
