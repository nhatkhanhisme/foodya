# Menu — Items

## Purpose
Manage all menu items: filter by category, toggle availability, add, edit, and delete.

## User Story
As a restaurant owner, I want to see all my menu items and quickly toggle their availability or edit them.

## Entry Points
- SideNav Menu → Items tab
- "Add menu item" quick action from Dashboard

## Exit Points
- "Add item" → `/owner/menu/items/new` (Menu Item Form)
- Row "Edit" → `/owner/menu/items/:id/edit` (Menu Item Form)
- "Categories" tab → `/owner/menu/categories`

## Layout
`OwnerShell`. Full-width `DataTable`.

## Components
- `TabBar` — [Categories] [Items]
- Category filter dropdown
- Status filter: All / Available / Unavailable
- `Button` (Filled) — "+ Add item"
- `DataTable` columns: Photo (thumbnail), Name, Category, Price, Available (toggle), Actions (Edit, Delete)
- `AvailabilityToggle` per row (inline PATCH)
- `Button` (XSmall, Outlined) — "Edit" per row
- `Button` (XSmall, Destructive Outlined) — "Delete" per row
- `ConfirmDialog` — delete (soft-delete warning for items with order history)
- `SkeletonLoader`
- `EmptyState`

## Business Rules
- Soft-delete only for items referenced by historical orders (BR-10). `ConfirmDialog` text differs: "This item has been ordered before. It will be hidden from your menu but order history is preserved."
- Toggling availability OFF for item currently in active customer carts: warn but allow ("Customers will see a warning at checkout").
- Availability toggle: immediate PATCH (no confirm needed — reversible instantly).

## API Endpoints
```
GET /api/v1/restaurants/{id}/menu-items?categoryId=&available=&page=&size=
(includes unavailable items — owner view)

PATCH /api/v1/restaurants/{id}/menu-items/{itemId}/toggle-availability
DELETE /api/v1/restaurants/{id}/menu-items/{itemId}
  (soft-delete if order history exists; hard delete otherwise)
```

## Loading State
`SkeletonLoader` (table rows × 8).

## Empty State
```
Headline: "No menu items yet"
Description: "Add items so customers can browse and order."
CTA: "+ Add your first item"
```

## Error State
- Toggle error: `Toast` (error + Retry). Toggle reverts.
- Delete error: `Toast` (error + Retry).

## Permissions
`OWNER`.

## Responsive Notes
Table horizontally scrollable on mobile.

## Developer Notes
- `AvailabilityToggle`: optimistic update; revert on API error.
- Photo thumbnail: 48×48px, `radius-sm`, `object-fit: cover`.
- Price column: `formatPrice(item.price)`, right-aligned, `JetBrains Mono`.
