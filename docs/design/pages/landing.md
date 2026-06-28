# Landing Page

## Purpose
Public marketing screen. Convert visitors to registered customers. Allows restaurant/menu browsing without login.

## User Story
As a visitor, I want to discover what Foodya is, browse nearby restaurants, and sign up quickly.

## Entry Points
- Direct URL: `/`
- If already authenticated → redirect to role home

## Exit Points
- "Find food" / cuisine chip → `/` (Home, authenticated or with filters)
- "Log in" → `/auth/login`
- "Sign up" → `/auth/register`
- Restaurant card click → `/restaurants/:id`
- Footer "Register your restaurant" → `/auth/register` (pre-selects Owner role)
- Footer "Deliver with us" → `/auth/register` (pre-selects Shipper role)

## Layout
Full-width marketing layout. No `CustomerShell`. Minimal `TopNav` (logo + Login + Sign up only).

## Components
- `TopNav` (minimal: logo left, `Button` Outlined "Log in" + `Button` Filled "Sign up")
- **Hero section:** headline, subtitle, address input + geolocation button, "Find food" CTA
- **Cuisine category row:** horizontally scrollable chip row
- **Social proof strip:** rating, order count, restaurant count, free signup
- **Restaurant preview grid:** 3 `RestaurantCard` (first 3 from API) + "View all →"
- **How it works:** 3-step process (Choose / Build / Receive)
- **Footer:** copyright, Terms, Privacy, restaurant + shipper CTAs

## Business Rules
- Guest browsing: restaurant list and menu are accessible. Cart actions redirect to Login.
- Address input uses geocoding to set lat/lng for filtering.

## API Endpoints
```
GET /api/v1/restaurants?page=1&size=3&sortBy=rating
(Used only for preview grid — 3 restaurants)
```

## Permissions
`PUBLIC`. Redirect authenticated users to role home on mount.

## Loading State
Preview grid: `SkeletonLoader` (card × 3).

## Empty State
Preview grid empty: hide the section (not a critical section for a landing page).

## Error State
Preview grid fetch error: hide the section silently.

## Responsive Notes
- Hero: two-column (text left, image right) on desktop; single column (text + image stacked) on mobile.
- Address input: full-width on mobile.

## Developer Notes
- Address geocoding: call Goong Maps geocode API client-side on form submit. Pass lat/lng as query params to Home.
- "Use my location": `navigator.geolocation.getCurrentPosition()` → same flow.
- If geolocation denied: address input still works manually.
