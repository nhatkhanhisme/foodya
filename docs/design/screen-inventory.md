# Screen Inventory

All 44 screens across all roles. Grouped by role, ordered by flow.

---

## Permissions Key

| Code | Meaning |
|---|---|
| `PUBLIC` | No authentication required |
| `AUTH` | Any authenticated user |
| `CUSTOMER` | `role = CUSTOMER` |
| `OWNER` | `role = RESTAURANT_OWNER` + `Restaurant.status = APPROVED` |
| `OWNER_PENDING` | `role = RESTAURANT_OWNER` (any restaurant status) |
| `DRIVER` | `role = SHIPPER` + `ShipperProfile.status = APPROVED` |
| `DRIVER_PENDING` | `role = SHIPPER` (any profile status) |
| `ADMIN` | `role = ADMIN` |

---

## Auth Screens (All Roles)

| # | Screen | File | Role | Purpose | Entry Points | Exit Points | Primary Components | Required APIs | Permission |
|---|---|---|---|---|---|---|---|---|---|
| 1 | Login | `pages/login.md` | All | Authenticate user | Direct URL, protected route redirect | Dashboard (role-based), Register | `AuthShell`, `EmailField`, `PasswordField` | `POST /auth/login` | `PUBLIC` |
| 2 | Role Selection | `pages/register.md` | Guest | Choose registration type | `/auth/register`, Login "Sign up" link | Customer Form, Owner Form, Shipper Form | `AuthShell`, `RoleSelector` | — | `PUBLIC` |
| 3 | Customer Registration | `pages/register.md` | Guest | Create Customer account | Role Selection → Customer | Login, Home | `RegisterForm` (1 step) | `POST /auth/register` | `PUBLIC` |
| 4 | Owner Registration | `pages/register.md` | Guest | Create Owner account + restaurant | Role Selection → Owner | Pending Approval | `RegisterForm` (2 steps), `OpenHoursEditor`, `MapPicker` | `POST /auth/register`, `POST /restaurants` | `PUBLIC` |
| 5 | Shipper Registration | `pages/register.md` | Guest | Create Shipper account + vehicle | Role Selection → Shipper | Pending Approval | `RegisterForm` (2 steps) | `POST /auth/register` | `PUBLIC` |
| 6 | Pending Approval | `pages/pending-approval.md` | Owner, Driver | Show approval-pending state; block dashboard | Auto-redirect post-registration; login while PENDING | — (blocked) / Dashboard on approval | `PendingApprovalGate` | `GET /users/me` (poll) | `OWNER_PENDING`, `DRIVER_PENDING` |
| 7 | Change Password | `pages/change-password.md` | Customer, Owner, Driver | Update account password | Profile → "Change password" | Profile | `ChangePasswordForm` | `POST /auth/change-password` | `AUTH` |

---

## Customer Screens

| # | Screen | File | Role | Purpose | Entry Points | Exit Points | Primary Components | Required APIs | Permission |
|---|---|---|---|---|---|---|---|---|---|
| 8 | Landing | `pages/landing.md` | Guest | Marketing home; convert to register/login | `/` (root) | Home (post-auth), Login, Register | `TopNav` (minimal), `RestaurantCard` (preview) | `GET /restaurants` (preview) | `PUBLIC` |
| 9 | Home — Discovery | `pages/customer-home.md` | Customer, Guest | Browse and search restaurants | Post-login redirect, BottomNav Home | Restaurant Detail | `TopNav`, `FilterChips`, `RestaurantCard`, `PaginatedList` | `GET /restaurants` | `PUBLIC` (view), `CUSTOMER` (order) |
| 10 | Restaurant Detail + Menu | `pages/restaurant-detail.md` | Customer, Guest | View restaurant info, menu, add to cart | Home card click | Cart, Food Detail (modal), Home | `MenuCategoryTabs`, `MenuItemCard`, `CartPanel`, `AlertBanner` | `GET /restaurants/{id}`, `GET /restaurants/{id}/menu`, `GET /cart`, `POST /cart/items` | `PUBLIC` (view), `CUSTOMER` (cart) |
| 11 | Food Detail | `pages/food-detail.md` | Customer | View item detail, set quantity/note, add to cart | Menu item click on Restaurant Detail | Restaurant Detail (modal closes) | `FoodDetailModal`, `StarRatingInput`, `QuantityStepper` | `POST /cart/items`, `PATCH /cart/items/{id}` | `CUSTOMER` |
| 12 | Cart | `pages/cart.md` | Customer | Review and edit cart before checkout | Restaurant Detail CartPanel CTA, BottomNav cart | Checkout, Restaurant Detail | `CartItemRow`, `AlertBanner` (unavailable), `OrderSummaryPanel` | `GET /cart`, `PATCH /cart/items/{id}`, `DELETE /cart/items/{id}` | `CUSTOMER` |
| 13 | Checkout | `pages/checkout.md` | Customer | Select address, payment method, place order | Cart → Proceed to Checkout | Order Tracking (COD), Payment Redirect (Online), Cart (back) | `AddressSelector`, `PaymentMethodSelector`, `OrderSummaryPanel` | `GET /users/me/addresses`, `POST /orders` | `CUSTOMER` |
| 14 | Payment Redirect | `pages/payment-redirect.md` | Customer | Transitional screen before external payment | Checkout (online payment) | Provider hosted page → Payment Awaiting | `AuthShell`, spinner, cancel link | `GET /orders/{id}` | `CUSTOMER` |
| 15 | Payment Awaiting | `pages/payment-awaiting.md` | Customer | Wait for payment webhook; show timeout | Return from provider page | Order Tracking (success), Order History (failed) | `CountdownTimer`, `LiveStatusBanner`, SSE | `GET /orders/{id}/payment`, SSE | `CUSTOMER` |
| 16 | Order Tracking | `pages/order-tracking.md` | Customer | Live order status + shipper location | Post-COD-checkout, Payment Awaiting success, notification deep-link | Order History, Write Review (delivered) | `OrderStatusTimeline`, `ShipperLocationMap`, `LiveStatusBanner`, `CountdownTimer` | `GET /orders/{id}`, `PATCH /orders/{id}/cancel`, SSE | `CUSTOMER` |
| 17 | Order Detail | `pages/order-detail.md` | Customer | Static view of a terminal order | Order History row (terminal orders) | Order History, Write Review | `OrderStatusTimeline`, `OrderItemRow`, `OrderSummaryPanel`, `PaymentInfoBadge` | `GET /orders/{id}` | `CUSTOMER` |
| 18 | Order History | `pages/order-history.md` | Customer | Paginated list of all past orders | BottomNav Orders, Profile | Order Tracking, Order Detail, Write Review | `OrderCard`, `StatusBadge`, `TabBar` (filter), `PaginatedList` | `GET /orders` | `CUSTOMER` |
| 19 | Write Review | `pages/write-review.md` | Customer | Submit 1–5 star rating + comment | Order Tracking (delivered), Order History CTA | Order History | `StarRatingInput`, `Textarea`, modal wrapper | `POST /orders/{id}/review` | `CUSTOMER` |
| 20 | Notifications | `pages/notifications.md` | Customer, Owner, Driver | Read in-app notifications | BottomNav bell, TopNav bell | Deep-linked screen per notification | `NotificationItem`, `PaginatedList` | `GET /notifications`, `PATCH /notifications/{id}/read`, `PATCH /notifications/read-all` | `AUTH` |
| 21 | Profile | `pages/profile.md` | Customer, Owner, Driver | View and edit personal account info | BottomNav Profile, SideNav | Change Password, Address Management (Customer) | `ProfileForm`, `AvatarUpload` | `GET /users/me`, `PUT /users/me` | `AUTH` |
| 22 | Address Management | `pages/address-management.md` | Customer | List, manage saved delivery addresses | Profile link | Address Form (add/edit), Checkout | `AddressCard`, `FAB` | `GET /users/me/addresses`, `DELETE /users/me/addresses/{id}`, `PATCH /users/me/addresses/{id}/set-default` | `CUSTOMER` |
| 23 | Address Form | `pages/address-form.md` | Customer | Add or edit a delivery address | Address Management FAB / edit action, Checkout modal | Address Management | `FormField`, `MapPicker` | `POST /users/me/addresses`, `PUT /users/me/addresses/{id}` | `CUSTOMER` |

---

## Restaurant Owner Screens

| # | Screen | File | Role | Purpose | Entry Points | Exit Points | Primary Components | Required APIs | Permission |
|---|---|---|---|---|---|---|---|---|---|
| 24 | Owner Dashboard | `pages/owner-dashboard.md` | Owner | Revenue, orders, ratings overview; daily hub | Post-login redirect, SideNav | Incoming Orders, Menu, Settings | `MetricCard`, `RatingDisplay`, revenue chart, `OrderCard` (active) | `GET /owner/dashboard`, `GET /restaurant/orders` | `OWNER` |
| 25 | Incoming Orders | `pages/incoming-orders.md` | Owner | Live feed of orders needing confirmation | SideNav, Dashboard quick-link, notification | Order Detail (Owner) | `OrderCard` (Owner variant), `CountdownTimer`, `LiveStatusBanner`, SSE | `GET /restaurant/orders`, `PATCH /restaurant/orders/{id}/confirm`, `PATCH /restaurant/orders/{id}/reject` | `OWNER` |
| 26 | Order Detail (Owner) | `pages/order-detail-owner.md` | Owner | Full order view with confirm/reject/ready actions | Incoming Orders card click | Incoming Orders | `OrderItemRow`, `OrderStatusTimeline`, `CountdownTimer`, `ReasonDialog` | `PATCH /restaurant/orders/{id}/confirm`, `PATCH /restaurant/orders/{id}/reject`, `PATCH /restaurant/orders/{id}/ready` | `OWNER` |
| 27 | Menu — Categories | `pages/menu-categories.md` | Owner | CRUD and reorder menu categories | SideNav Menu | Menu Items | `DraggableList`, inline edit form | `POST /restaurants/{id}/categories`, `PUT /…`, `DELETE /…`, `PATCH /…/reorder` | `OWNER` |
| 28 | Menu — Items | `pages/menu-items.md` | Owner | List, filter, toggle, delete menu items | SideNav Menu → Items tab | Menu Item Form | `DataTable`, `AvailabilityToggle`, `StatusBadge` | `GET /restaurants/{id}/menu-items`, `PATCH /…/toggle-availability`, `DELETE /…` | `OWNER` |
| 29 | Menu Item Form | `pages/menu-item-form.md` | Owner | Add or edit a single menu item | Menu Items → Add CTA / Edit action | Menu Items | `FormField`, `PriceInput`, `CategorySelect`, `ImageUpload`, `Toggle` | `POST /restaurants/{id}/menu-items`, `PUT /restaurants/{id}/menu-items/{id}` | `OWNER` |
| 30 | Restaurant Settings | `pages/restaurant-settings.md` | Owner | Edit restaurant profile, hours, open/close toggle | SideNav Settings | — | `OpenHoursEditor`, `MapPicker`, `ImageUpload`, `Toggle` | `PUT /restaurants/{id}`, `PATCH /restaurants/{id}/toggle-status` | `OWNER` |
| 31 | Owner Notifications | Shared `pages/notifications.md` | Owner | Order alerts, approval/suspension notices | SideNav bell | Deep-linked screens | Same as Customer Notifications | Same as #20 | `OWNER` |

---

## Driver (Shipper) Screens

| # | Screen | File | Role | Purpose | Entry Points | Exit Points | Primary Components | Required APIs | Permission |
|---|---|---|---|---|---|---|---|---|---|
| 32 | Available Jobs | `pages/driver-dashboard.md` | Driver | Browse and accept nearby delivery jobs | Post-login, BottomNav Jobs | Active Delivery (on accept) | `JobCard`, `LocationStatusBanner`, `EmptyState` | `GET /shipper/jobs` | `DRIVER` |
| 33 | Active Delivery | `pages/active-delivery.md` | Driver | Manage current delivery from pickup to handoff | Accept job → auto-navigate here | Available Jobs (complete/exception) | `ActiveDeliveryPanel`, `ShipperLocationMap`, `QuickConfirmInline`, `PaymentInfoBadge` | `PATCH /shipper/jobs/{id}/accept`, `PATCH /shipper/orders/{id}/pickup`, `PATCH /shipper/orders/{id}/deliver`, `POST /shipper/location` | `DRIVER` |
| 34 | Delivery Exception | `pages/delivery-exception.md` | Driver | Report and confirm a failed delivery | Active Delivery → "Delivery problem" | Available Jobs | `ReasonDialog` (radio), `ConfirmDialog` | `PATCH /shipper/orders/{id}/deliver` (exception payload) | `DRIVER` |
| 35 | Earnings | `pages/earnings.md` | Driver | View completed deliveries and income | BottomNav / SideNav Earnings | — | `MetricCard`, `DateRangePicker`, delivery list | `GET /shipper/earnings` | `DRIVER` |
| 36 | Driver Notifications | Shared `pages/notifications.md` | Driver | Job alerts, approval, exception resolution | BottomNav / SideNav bell | Deep-linked screens | Same as Customer Notifications | Same as #20 | `DRIVER` |
| 37 | Driver Profile | Shared `pages/profile.md` | Driver | View personal info and vehicle (read-only post-approval) | BottomNav Profile | Change Password | `ProfileForm` (Driver variant — vehicle read-only) | `GET /users/me`, `PUT /users/me` | `DRIVER` |

---

## Admin Screens

| # | Screen | File | Role | Purpose | Entry Points | Exit Points | Primary Components | Required APIs | Permission |
|---|---|---|---|---|---|---|---|---|---|
| 38 | Admin Dashboard | `pages/admin-dashboard.md` | Admin | Platform health: metrics, queue counts, recent activity | Post-login | All admin sections | `MetricCard`, `PendingQueuePanel`, `RecentActivityList` | `GET /admin/analytics/overview`, `GET /admin/restaurants?status=PENDING`, `GET /admin/shippers?status=PENDING_APPROVAL` | `ADMIN` |
| 39 | Analytics | `pages/admin-analytics.md` | Admin | GMV, orders, completion rate, entity counts over time | SideNav Analytics | — | charts, `DateRangePicker`, `MetricCard`, `DataTable` | `GET /admin/analytics/overview` | `ADMIN` |
| 40 | Restaurant Approval Queue | `pages/restaurant-approval.md` | Admin | Review and action pending restaurants | SideNav Restaurants, Dashboard link | Restaurant Detail (Admin) | `ApprovalTable`, `StatusBadge`, search/filter | `GET /admin/restaurants`, `PATCH /admin/restaurants/{id}/approve`, `PATCH /admin/restaurants/{id}/reject` | `ADMIN` |
| 41 | Shipper Approval Queue | `pages/shipper-approval.md` | Admin | Review and action pending shippers | SideNav Shippers, Dashboard link | Shipper Detail (Admin) | `ApprovalTable`, `StatusBadge` | `GET /admin/shippers`, `PATCH /admin/shippers/{id}/approve`, `PATCH /admin/shippers/{id}/reject` *(gap)* | `ADMIN` |
| 42 | User Management | `pages/user-management.md` | Admin | Search, filter, moderate all users | SideNav Users | User Detail | `DataTable`, search, role/status filters, inline ban | `GET /admin/users`, `PATCH /admin/users/{id}/ban` | `ADMIN` |
| 43 | Disputes | `pages/disputes.md` | Admin | Review and resolve flagged orders and reviews | SideNav Disputes, Dashboard panel | Flagged Order/Review detail | `DisputeCard`, `TabBar` (type filter) | `GET /admin/disputes` *(gap)*, `POST /admin/disputes/{id}/resolve` *(gap)* | `ADMIN` |
| 44 | Audit Log | `pages/audit-log.md` | Admin | Read-only, append-only moderation history | SideNav Audit Log, Dashboard "View full log" | — | `AuditLogTable`, filters, detail drawer | `GET /admin/audit-log` *(gap)* | `ADMIN` |
| 45 | Platform Configuration | `pages/platform-config.md` | Admin | View-only runtime config values | SideNav Configuration | — | key-value display, `AlertBanner` (read-only notice) | `GET /admin/config` *(gap)* | `ADMIN` |

---

## Screen Count Summary

| Role | Count |
|---|---|
| Auth (shared) | 7 |
| Customer | 16 |
| Restaurant Owner | 8 |
| Driver | 6 |
| Admin | 8 |
| **Total** | **45** |

> Screen 20 (Notifications) and 21 (Profile) are shared across Customer, Owner, and Driver via role-aware variants of the same component. Counted once in Customer above.

---

## Status Enums Reference

| Entity | Statuses |
|---|---|
| `Order.status` | `AWAITING_PAYMENT`, `PENDING`, `CONFIRMED`, `READY_FOR_PICKUP`, `PICKED_UP`, `DELIVERED`, `REJECTED`, `CANCELLED` |
| `Payment.status` | `PENDING`, `SUCCESS`, `FAILED`, `REFUNDED` |
| `Restaurant.status` | `PENDING`, `APPROVED`, `REJECTED`, `SUSPENDED` |
| `ShipperProfile.status` | `PENDING_APPROVAL`, `APPROVED`, `REJECTED` |
| `User.status` | `ACTIVE`, `BANNED` |
