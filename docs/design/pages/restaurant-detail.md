# Restaurant Detail + Menu

## Purpose
Display a restaurant's profile, full categorised menu, and allow customers to add items to their cart. The cart panel is always visible on desktop.

## User Story
As a customer, I want to see a restaurant's menu organised by category so I can select what I want to order.

## Entry Points
- Restaurant card click on Home
- Direct URL: `/restaurants/:restaurantId`
- "Order again" / "Browse restaurants" deep links

## Exit Points
- Menu item click → Food Detail modal (modal overlay; URL unchanged)
- Cart panel "Proceed to Checkout" → `/checkout`
- `← Back` → `/` (Home)
- Review section → no exit (static display)

## Layout
`CustomerShell` (TopNav). **`TwoColumnLayout`** on desktop: left column (menu, flex-1) + right sticky cart panel (320px). On mobile: single column; cart becomes sticky bottom bar.

## Components
- Restaurant hero image (full-width, 220px tall)
- Restaurant avatar (64px circle), name, cuisine, rating, distance, hours
- `AlertBanner` (warning) — restaurant closed or suspended
- `MenuCategoryTabs` (sticky on scroll, below hero)
- `MenuItemCard` (browse variant) — per item in each category
- `CartPanel` (right column desktop; sticky bottom bar mobile)
- Conflict dialog (`ConfirmDialog`) — different restaurant in cart
- `ReviewCard` list — at bottom of left column
- `RatingDisplay`

## User Actions
1. Scroll through menu categories; click tabs to jump.
2. Click menu item row → opens Food Detail modal.
3. Click `+ Add` on item (if no variants needed) → adds to cart directly.
4. Adjust quantities in cart panel via `QuantityStepper`.
5. Remove item from cart (trash icon).
6. Click "Proceed to Checkout" → navigate to Checkout.
7. Read restaurant reviews (scroll to bottom).

## Business Rules
- Closed restaurant: menu viewable but cart panel CTA disabled. `AlertBanner` shown (UC-C04).
- Suspended restaurant: same as closed.
- Unavailable item: "Not available" label shown; `+ Add` button hidden.
- Different restaurant in cart: `ConfirmDialog` ("Start a new order? Your cart from X will be cleared.") before adding (BR-05).
- Guest adding to cart: redirected to Login with `?next=/restaurants/:id`.

## Validation Rules
N/A (cart operations validated via API).

## API Endpoints
```
GET /api/v1/restaurants/{id}
Response: RestaurantDetail { id, name, description, phone, status, openingHours, ratingAvg, reviewCount, latitude, longitude }

GET /api/v1/restaurants/{id}/menu
Response: { restaurantId, categories: [{ id, name, items: MenuItem[] }] }

GET /api/v1/cart
Response: Cart { restaurantId, items: CartItem[] }

POST /api/v1/cart/items
Body: { menuItemId, quantity, note? }

PATCH /api/v1/cart/items/{id}
Body: { quantity }

DELETE /api/v1/cart/items/{id}

GET /api/v1/restaurants/{id}/reviews?page=&size=
```

## Loading State
- Restaurant hero + tabs: `SkeletonLoader` (card variant, full-width) while fetching restaurant detail.
- Menu categories: `SkeletonLoader` (list-item variant, × 6) while fetching menu.
- Cart panel: `SkeletonLoader` (compact) while fetching cart.

## Empty State
- Empty menu: `EmptyState` — "This restaurant hasn't added any menu items yet."
- No reviews: "No reviews yet. Be the first to order and review!"

## Error State
- Restaurant not found (404): `EmptyState` (error variant) — "Restaurant not found." + "Browse restaurants" CTA.
- Menu fetch error: `AlertBanner` (error) with Retry inside left column.
- Cart operation error: `Toast` (error): "Could not update your cart. Try again."

## Permissions
`PUBLIC` for viewing. Cart operations require `CUSTOMER`.

## Responsive Notes
- Desktop: `TwoColumnLayout` (menu left, cart right 320px sticky).
- Mobile: single column. Cart collapses to sticky bottom bar showing item count, subtotal, and "Proceed to Checkout" button. Full cart accessible via `/cart`.
- `MenuCategoryTabs`: horizontally scrollable on mobile.

## Accessibility
- Hero image: `alt="{restaurantName} cover photo"`.
- Category sections: `<section aria-labelledby="category-{id}">`.
- `MenuCategoryTabs`: `role="tablist"`.
- Cart panel: `aria-live="polite"` on subtotal — announces updates when items change.

## Developer Notes
- `MenuCategoryTabs` sticky offset must account for TopNav height (64px).
- On click of a category tab: `element.scrollIntoView({ behavior: 'smooth', block: 'start' })` with 64px + tab bar height offset.
- Conflict dialog: check `cart.restaurantId !== restaurant.id` before POST /cart/items.
- Cart panel quantity stepper: debounce PATCH calls at 500ms to avoid rapid successive requests.
- `reviews` section: lazy-loaded after menu is rendered (below the fold).
