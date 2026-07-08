# Customer Home — Restaurant Discovery

## Purpose
Primary landing screen for authenticated customers. Browse, search, and filter restaurants to find somewhere to order from.

## User Story
As a customer, I want to see restaurants near me and filter by cuisine or rating so I can quickly find what I want to eat.

## Entry Points
- Post-login redirect (Customer role)
- BottomNav "Home" tab
- Logo click from any Customer screen
- "Browse restaurants" CTA from empty states and error screens

## Exit Points
- Restaurant card click → `/restaurants/:restaurantId`
- Search input → same page (filtered results)
- Notification bell → `/notifications`
- Avatar menu → Profile / Change Password / Logout

## Layout
`CustomerShell` (TopNav + BottomNav on mobile). Full-width content, `max-width: 1280px` centred. 3-column restaurant grid on desktop, 2-column tablet, 1-column mobile.

## Components
- `TopNav` (with search bar, address selector, notification bell, avatar menu)
- Filter chips row: [All] [Open now] [Under 30 min] [★ 4.0+] [Cuisine chips…]
- Sort dropdown: Relevance / Distance / Rating / Newest
- `RestaurantCard` grid (× N)
- `SkeletonLoader` (variant=card, count=9) — loading state
- `EmptyState` — no results
- `PaginatedList` with page controls

## User Actions
1. Search via TopNav search bar (debounced, 300ms).
2. Toggle filter chips to add/remove filters.
3. Change sort order via dropdown.
4. Change delivery address via address selector in TopNav.
5. Click restaurant card → Restaurant Detail.
6. Paginate results.

## Business Rules
- Only `Restaurant.status = APPROVED` restaurants are shown (BR-31).
- Sorting by distance requires a valid lat/lng — if location is unavailable, default to Relevance.
- Guest users can browse restaurants but cannot add to cart (redirect to Login on cart action).
- Filter chips are additive (AND logic) except cuisine chips which are exclusive (one at a time).

## Validation Rules
- Search: no minimum length — debounced query sent on any input.
- Radius: default 10km; configurable via address selector.

## API Endpoints
```
GET /api/v1/restaurants
Params: lat, lng, radiusKm, search, cuisine, minRating, sortBy, page, size

Response 200: {
  data: RestaurantSummary[],
  meta: { page, size, total }
}
```

## Loading State
On initial load and on filter/search change: render 9 `SkeletonLoader` (card variant) in the grid. Existing results remain visible with `LinearProgressBar` during refetch.

## Empty State
```
Illustration: magnifier + bowl
Headline: "No restaurants found"
Description: "Try adjusting your filters or search in a different area."
CTA: "Clear all filters"
```

## Error State
- Network error: `AlertBanner` (error): "Could not load restaurants." + "Retry" CTA.
- Location error: `AlertBanner` (info): "Could not detect your location. Showing results for Ho Chi Minh City."

## Permissions
`PUBLIC` for viewing. `CUSTOMER` for cart interactions. Route guard: if a Customer, show full screen. If Guest, show screen but cart actions redirect to Login.

## Responsive Notes
- Desktop (≥ 1200px): 3-column grid, TopNav search bar full-width.
- Tablet (768–1199px): 2-column grid.
- Mobile (< 768px): 1-column grid, search collapses to icon in TopNav (expands on tap), BottomNav visible.
- Filter chips: horizontally scrollable on all breakpoints.

## Accessibility
- `<main>` wraps the restaurant grid with `aria-label="Restaurant listings"`.
- Each `RestaurantCard` is a `<article>` with `aria-label="{restaurantName}"`.
- Filter chips: `role="checkbox"`, `aria-checked`.
- Sort dropdown: native `<select>` or custom with `role="combobox"`.
- Pagination: `<nav aria-label="Pagination">`.

## Developer Notes
- Persist active filters and search term in URL query params (`?search=pho&cuisine=Noodles&minRating=4&sortBy=distance`).
- On mount, read query params to pre-populate filters (enables shareable URLs and browser back restoring state).
- Address selector saves selected address to user session; lat/lng used in API call.
- Debounce search input at 300ms before triggering query param update.
- `react-query` key: `['restaurants', { search, cuisine, minRating, sortBy, lat, lng, page }]`.
