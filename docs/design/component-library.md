# Component Library

All reusable UI components for Foodya. Check this document before building any new component. If an equivalent exists here, use it; do not create a parallel implementation.

---

## Component Index

| Category | Components |
|---|---|
| Layout & Shell | `AuthShell`, `CustomerShell`, `OwnerShell`, `DriverShell`, `AdminShell`, `PageContainer`, `TwoColumnLayout` |
| Navigation | `TopNav`, `SideNavDrawer`, `SideNavRail`, `BottomNav`, `TabBar`, `PageBreadcrumb`, `AvatarMenu` |
| Auth & Onboarding | `RoleSelector`, `RegisterForm`, `StepIndicator`, `PendingApprovalGate`, `RejectionScreen`, `ApprovalScreen` |
| Dialogs & Overlays | `ConfirmDialog`, `ReasonDialog`, `QuickConfirmInline`, `FormDialog` |
| Feedback | `Toast`, `AlertBanner`, `StatusBadge`, `CountdownTimer`, `LiveStatusBanner`, `EmptyState`, `SkeletonLoader`, `Spinner`, `LinearProgressBar`, `OverlaySpinner` |
| Order | `OrderCard`, `OrderStatusTimeline`, `OrderItemRow`, `OrderSummaryPanel`, `PaymentInfoBadge` |
| Restaurant & Menu | `RestaurantCard`, `MenuCategoryTabs`, `MenuItemCard`, `OpenHoursEditor`, `RatingDisplay`, `ReviewCard` |
| Cart & Checkout | `CartItemRow`, `CartPanel`, `AddressSelector`, `PaymentMethodSelector` |
| Forms | `FormField`, `PasswordField`, `SearchInput`, `PriceInput`, `PhoneInput`, `SelectField`, `Textarea`, `Toggle`, `Checkbox`, `RadioGroup`, `StarRatingInput`, `MapPicker`, `ImageUpload`, `DateRangePicker` |
| Cards | `Card` (base), `MetricCard`, `JobCard`, `NotificationItem`, `AddressCard`, `DisputeCard`, `AuditLogRow` |
| Driver | `JobCard`, `ActiveDeliveryPanel`, `LocationStatusBanner` |
| Admin | `ApprovalTable`, `EntityDetailHeader`, `AuditLogTable`, `AdminNoteField` |
| Utility | `Avatar`, `Badge`, `Divider`, `Tooltip`, `Accordion`, `DraggableList`, `QuantityStepper`, `AvailabilityToggle` |

---

## Layout & Shell

### `AuthShell`

**Purpose:** Minimal wrapper for all auth and onboarding screens (login, register, pending, etc.).

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `children` | `ReactNode` | Yes | Page content |
| `showLogo` | `boolean` | No | Default `true` |

**Layout:** Logo centred at top; single centred content card (`max-width: 480px`); no navigation chrome.

---

### `TwoColumnLayout`

**Purpose:** Left scrollable content + right sticky panel (Checkout, Restaurant Detail, Admin Dispute detail).

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `left` | `ReactNode` | Yes | Primary content |
| `right` | `ReactNode` | Yes | Sticky summary panel |
| `rightWidth` | `string` | No | Default `320px` |

**Responsive:** Stacks vertically (right panel moves below left) at `< 1024px`.

---

## Navigation

### `TopNav`

**Purpose:** Primary navigation bar for Customer and Guest.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `showSearch` | `boolean` | No | Shows search bar (default `true` on Home) |
| `showAddressSelector` | `boolean` | No | Shows location picker (default `true` on Home) |

**States:** Flat (`elevation-0`) at page top; elevated (`elevation-1` + `border-bottom`) on scroll.
**Accessibility:** `role="banner"`, `aria-label="Main navigation"`.

---

### `SideNavDrawer`

**Purpose:** Full sidebar for Owner and Admin.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `items` | `NavItem[]` | Yes | Array of `{ label, icon, href, badge? }` |
| `sections` | `NavSection[]` | No | Grouped dividers with section labels |
| `logoHref` | `string` | Yes | Logo click destination |

**States:** Expanded (default desktop), overlay (mobile — triggered by hamburger).
**Accessibility:** `role="navigation"`, `aria-label="Sidebar"`. Active item has `aria-current="page"`.

---

### `BottomNav`

**Purpose:** Mobile bottom tab bar for Customer and Driver.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `tabs` | `Tab[]` | Yes | Array of `{ label, icon, href, badge? }` |

**Accessibility:** `role="tablist"`. Each tab: `role="tab"`, `aria-selected`, `aria-label`.

---

### `TabBar`

**Purpose:** In-page horizontal tab navigation (menu categories, order history filters, etc.).

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `tabs` | `string[]` | Yes | Tab labels |
| `activeIndex` | `number` | Yes | Controlled active tab |
| `onChange` | `(index: number) => void` | Yes | |
| `scrollable` | `boolean` | No | Default `true` |

**Accessibility:** `role="tablist"`. Each tab: `role="tab"`, `aria-selected`.

---

## Auth & Onboarding

### `RoleSelector`

**Purpose:** Three-card layout for role selection at registration.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `onSelect` | `(role: 'CUSTOMER' \| 'RESTAURANT_OWNER' \| 'SHIPPER') => void` | Yes | |

**States:** Default, hover, selected (primary-container background).

---

### `PendingApprovalGate`

**Purpose:** Full-page gate shown to Owner and Driver while awaiting admin approval.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `role` | `'OWNER' \| 'DRIVER'` | Yes | Determines copy |
| `illustrationSlot` | `ReactNode` | No | Custom illustration |
| `stepsSlot` | `ReactNode` | No | "What we're checking" steps |

**Polling:** Component polls `GET /users/me` every 60s; navigates automatically on status change.

---

### `StepIndicator`

**Purpose:** Multi-step form progress indicator.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `steps` | `number` | Yes | Total steps |
| `current` | `number` | Yes | 1-indexed current step |
| `labels` | `string[]` | No | Optional step labels |

---

## Dialogs & Overlays

### `ConfirmDialog`

**Purpose:** Reusable confirmation modal for destructive actions.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `open` | `boolean` | Yes | |
| `title` | `string` | Yes | Dialog heading |
| `body` | `ReactNode` | No | Supporting text |
| `confirmLabel` | `string` | No | Default `"Confirm"` |
| `cancelLabel` | `string` | No | Default `"Cancel"` |
| `variant` | `'default' \| 'destructive'` | No | Destructive makes confirm button `color-error` |
| `onConfirm` | `() => void` | Yes | |
| `onCancel` | `() => void` | Yes | |
| `loading` | `boolean` | No | Confirm button shows spinner |

**Accessibility:** `role="alertdialog"`, `aria-modal="true"`, focus trapped, Escape closes.

---

### `ReasonDialog`

**Purpose:** Structured reason selection + required freetext for consequential actions (reject, ban, exception, suspend).

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `open` | `boolean` | Yes | |
| `title` | `string` | Yes | |
| `reasons` | `ReasonOption[]` | Yes | `{ id, label, prefillNote? }` |
| `noteLabel` | `string` | No | Label for freetext field. Default: `"Additional note"` |
| `noteInternal` | `boolean` | No | If `true`, shows "Internal only — not sent to user" hint |
| `noteRequired` | `boolean` | No | Default `true` |
| `warningText` | `string` | No | Shown in warning `AlertBanner` inside dialog |
| `confirmLabel` | `string` | No | |
| `onConfirm` | `(reasonId: string, note: string) => void` | Yes | |
| `onCancel` | `() => void` | Yes | |
| `loading` | `boolean` | No | |

**States:** Submit disabled until reason selected + note filled (when `noteRequired`).
**Variants:** Internally consistent across all 6+ usage contexts — no per-context forks.

---

### `QuickConfirmInline`

**Purpose:** Lightweight non-blocking inline confirmation for low-stakes status advances (Mark Ready, Mark Picked Up).

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `message` | `string` | Yes | Confirmation message |
| `onConfirm` | `() => void` | Yes | |
| `onCancel` | `() => void` | Yes | |
| `confirmLabel` | `string` | No | |

**Layout:** Compact banner below the triggering button; not a modal. Does not block the rest of the screen.

---

## Feedback

### `Toast`

**Purpose:** Transient notification feedback.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `variant` | `'success' \| 'error' \| 'warning' \| 'info' \| 'action'` | Yes | |
| `message` | `string` | Yes | |
| `action` | `{ label: string; onClick: () => void }` | No | Action variant only |
| `duration` | `number` | No | Ms before auto-dismiss. Default: success/info=4000, error/warning=6000, action=no auto-dismiss |

**Position:** Bottom-right (desktop), bottom-centre (mobile). Max 3 stacked.
**Accessibility:** `role="status"` (success/info) or `role="alert"` (error/warning).

---

### `AlertBanner`

**Purpose:** Persistent in-page contextual warning or info. Not a toast — stays visible until dismissed or condition resolves.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `variant` | `'info' \| 'warning' \| 'error' \| 'success'` | Yes | |
| `message` | `ReactNode` | Yes | |
| `closeable` | `boolean` | No | Shows × button |
| `action` | `{ label: string; onClick: () => void }` | No | |

**Usage examples:** Cart unavailable items, restaurant closed, GPS off, account suspended.

---

### `StatusBadge`

**Purpose:** Coloured pill displaying entity status. Non-interactive.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `status` | `OrderStatus \| RestaurantStatus \| ShipperStatus \| PaymentStatus \| UserStatus` | Yes | |
| `size` | `'sm' \| 'md'` | No | Default `'md'` |

**Colour map:**

| Status | Text Colour | Background |
|---|---|---|
| `AWAITING_PAYMENT` | `color-info` | `color-info-container` |
| `PENDING` | `color-warning` | `color-warning-container` |
| `CONFIRMED` | `#7C3AED` | `#EDE9FE` |
| `READY_FOR_PICKUP` | `#0891B2` | `#CFFAFE` |
| `PICKED_UP` | `#0284C7` | `#E0F2FE` |
| `DELIVERED` / `APPROVED` / `SUCCESS` | `color-success` | `color-success-container` |
| `REJECTED` / `FAILED` / `BANNED` | `color-error` | `color-error-container` |
| `CANCELLED` / `SUSPENDED` | `color-on-surface-disabled` | `color-surface-container` |
| `PENDING_APPROVAL` | `color-warning` | `color-warning-container` |
| `REFUNDED` | `color-info` | `color-info-container` |

---

### `CountdownTimer`

**Purpose:** Counts down from a given duration; changes visual state as urgency increases.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `totalSeconds` | `number` | Yes | |
| `warningThreshold` | `number` | No | Seconds remaining when warning state activates. Default `60` |
| `onExpire` | `() => void` | No | Callback when 0 is reached |
| `variant` | `'inline' \| 'badge' \| 'ring'` | No | Default `'inline'` |

**States:** Normal → Warning (amber, `≤ warningThreshold` seconds) → Expired (error, 0:00).

---

### `EmptyState`

**Purpose:** Consistent empty content placeholder.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `illustration` | `ReactNode` | Yes | SVG/image |
| `headline` | `string` | Yes | |
| `description` | `string` | No | |
| `primaryAction` | `{ label: string; onClick: () => void }` | No | |
| `secondaryAction` | `{ label: string; onClick: () => void }` | No | |

---

### `SkeletonLoader`

**Purpose:** Content-shaped placeholder during initial load.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `variant` | `'card' \| 'list-item' \| 'text' \| 'avatar' \| 'chart'` | Yes | |
| `count` | `number` | No | Number of repeated skeletons. Default `1` |

**Rule:** Skeleton shape must exactly match the component it replaces (same height, same radius).

---

## Order Components

### `OrderCard`

**Purpose:** Compact summary row in order lists.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `order` | `OrderSummary` | Yes | |
| `variant` | `'customer' \| 'owner' \| 'admin'` | Yes | |
| `onAction` | `(action: string) => void` | No | CTA callback |

**Customer variant:** Restaurant name, items count, total, status badge, CTA (Track / View / Rate / Order again).
**Owner variant:** Customer name, items count, total, status badge, `CountdownTimer` (PENDING only), Confirm/Reject/Ready action buttons.
**Admin variant:** Compact read-only summary for dispute context.

---

### `OrderStatusTimeline`

**Purpose:** Vertical step-by-step order progress display.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `status` | `OrderStatus` | Yes | |
| `timestamps` | `Record<string, string>` | No | Timestamps per status |
| `role` | `'customer' \| 'owner'` | No | Determines which steps are shown |

**Steps shown:**
- Customer: Placed → Confirmed → Preparing → Ready → On the way → Delivered
- Owner: Placed → Confirmed → Ready → Shipper assigned → Delivered

---

### `PaymentInfoBadge`

**Purpose:** Displays payment method and status with role-appropriate detail.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `payment` | `PaymentSummary` | Yes | `{ method, provider, status, amount }` |
| `variant` | `'customer' \| 'owner' \| 'driver' \| 'admin'` | Yes | |

**Driver variant (COD):** Shows "Collect ₫X.000 cash" prominently.
**Driver variant (Online):** Shows "Payment already handled — no cash needed."
**Admin variant:** Shows full record including `paid_at` and `provider_txn_ref`.

---

## Restaurant & Menu Components

### `RestaurantCard`

**Purpose:** Summary card for restaurant listing.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `restaurant` | `RestaurantSummary` | Yes | |
| `onClick` | `() => void` | Yes | |

**Renders:** Cover image, open/closed badge, distance badge, name, rating + review count, cuisine category, estimated delivery time.
**States:** Default, hover (elevation-1 → elevation-2), closed (desaturated image overlay).

---

### `MenuItemCard`

**Purpose:** Single item in a restaurant's menu.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `item` | `MenuItem` | Yes | |
| `variant` | `'browse' \| 'manage'` | Yes | |
| `onAdd` | `() => void` | No | Browse variant only |
| `onEdit` | `() => void` | No | Manage variant only |
| `onToggle` | `(available: boolean) => void` | No | Manage variant only |
| `onDelete` | `() => void` | No | Manage variant only |

**Browse states:** Available (normal), Unavailable (muted + "Not available" text, add button hidden).

---

## Form Components

### `FormField`

**Purpose:** Labelled input with validation error display.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `label` | `string` | Yes | |
| `error` | `string` | No | Error message shown below input |
| `hint` | `string` | No | Helper text shown below input |
| `required` | `boolean` | No | |
| `children` | `ReactNode` | Yes | The actual `<input>` or control |

**States:** Default, Focused (primary border), Filled, Error (red border + message), Disabled, Read-only (dashed border).

---

### `PriceInput`

**Purpose:** Integer-only VND price field.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `value` | `number` | Yes | Integer in VND |
| `onChange` | `(value: number) => void` | Yes | |
| `label` | `string` | No | Default `"Price (₫)"` |

**Validation:** Integer only. Rejects decimal. Min 0. Right-aligned text. `JetBrains Mono` font. Leading `₫` adornment.

---

### `StarRatingInput`

**Purpose:** Interactive 1–5 star rating selector.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `value` | `number \| null` | Yes | |
| `onChange` | `(rating: number) => void` | Yes | |
| `readOnly` | `boolean` | No | Display-only (reviews list) |
| `size` | `'sm' \| 'md' \| 'lg'` | No | Default `'md'` (28px) |

**Hover:** Stars fill on hover-preview. Selected state is persistent.

---

### `MapPicker`

**Purpose:** Embedded map with draggable pin for lat/lng selection.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `lat` | `number \| null` | Yes | |
| `lng` | `number \| null` | Yes | |
| `onChange` | `(lat: number, lng: number) => void` | Yes | |
| `readOnly` | `boolean` | No | View-only map (Admin restaurant detail) |

**Provider:** Goong Maps (D1 in SRS). Coordinate validation enforced (BR-22): lat ∈ [-90, 90], lng ∈ [-180, 180].

---

### `QuantityStepper`

**Purpose:** Increment/decrement counter for cart item quantities.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `value` | `number` | Yes | |
| `onChange` | `(value: number) => void` | Yes | |
| `min` | `number` | No | Default `1` |
| `max` | `number` | No | Default `99` |

**Note:** Min is `1`, not `0`. Use the trash icon to remove items; stepper never reaches 0.

---

## Cards

### `MetricCard`

**Purpose:** Single KPI display for dashboards.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `label` | `string` | Yes | |
| `value` | `string \| number` | Yes | |
| `trend` | `{ direction: 'up' \| 'down' \| 'flat'; percent: number; label: string }` | No | |
| `icon` | `string` | No | Material Symbol name |
| `loading` | `boolean` | No | Shows skeleton |

---

### `JobCard`

**Purpose:** Available delivery job card in Driver dashboard.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `job` | `JobSummary` | Yes | `{ orderId, restaurantName, restaurantAddress, deliveryDistrict, distanceKm, routeDistanceKm, estimatedPayout, itemCount, minutesSincePlaced }` |
| `onAccept` | `() => void` | Yes | |
| `loading` | `boolean` | No | Accept button loading state |

---

### `NotificationItem`

**Purpose:** Single notification row.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `notification` | `Notification` | Yes | |
| `onClick` | `() => void` | Yes | Marks read + navigates |

**States:** Unread (blue dot), Read (no dot). Timestamp uses relative format ("2 min ago", "Yesterday").

---

## Driver Components

### `ActiveDeliveryPanel`

**Purpose:** Phase-aware delivery control panel shown over the map on the Active Delivery screen.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `order` | `ActiveOrderDetail` | Yes | |
| `phase` | `'pickup' \| 'enroute'` | Yes | |
| `onMarkPickedUp` | `() => void` | No | Pickup phase only |
| `onMarkDelivered` | `() => void` | No | Enroute phase only |
| `onException` | `() => void` | No | Enroute phase only |

**Phase pickup:** Shows restaurant address, order summary, payment reminder, open navigation CTA, Mark Picked Up button.
**Phase enroute:** Shows customer district, payment reminder, open navigation CTA, Mark Delivered and Exception buttons.

---

### `LocationStatusBanner`

**Purpose:** Non-blocking GPS/network status indicator for Driver.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `gpsState` | `'available' \| 'degraded' \| 'off'` | Yes | |
| `networkState` | `'online' \| 'offline'` | Yes | |

**Renders nothing** when `gpsState = 'available'` and `networkState = 'online'`. Appears as a compact warning banner otherwise.

---

## Admin Components

### `ApprovalTable`

**Purpose:** Filterable, searchable data table for admin approval queues (restaurants, shippers).

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `data` | `ApprovalItem[]` | Yes | |
| `columns` | `Column[]` | Yes | |
| `onReview` | `(id: string) => void` | Yes | Navigate to detail |
| `loading` | `boolean` | No | |
| `emptyState` | `ReactNode` | No | |

---

### `EntityDetailHeader`

**Purpose:** Consistent header for Admin entity detail pages (restaurant, shipper, user).

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `entityName` | `string` | Yes | |
| `status` | `string` | Yes | |
| `submittedAt` | `string` | No | |
| `actions` | `ActionButton[]` | Yes | `{ label, variant, onClick }` |

---

### `AdminNoteField`

**Purpose:** Internal-only admin note textarea on entity detail pages.

**Props:**
| Prop | Type | Required | Description |
|---|---|---|---|
| `value` | `string` | Yes | |
| `onChange` | `(value: string) => void` | Yes | |
| `onSave` | `() => void` | No | If present, shows a Save button |

**Visibility:** Clearly labelled "Internal note — not visible to this user." Never exposed in public API responses.

---

## Reusability Rules

1. **Never fork a component** for role-specific concerns — add a `variant` prop instead.
2. **`ReasonDialog` is the single implementation** for all 6+ reason-selection contexts. Do not create `BanReasonDialog`, `RejectReasonDialog`, etc.
3. **`StatusBadge` owns the colour map.** Do not hardcode status colours inline.
4. **`EmptyState` is always used** when a list or data set is empty. Do not render `null` or a plain text message.
5. **`SkeletonLoader` must match** the replaced component's dimensions exactly.
6. **`ConfirmDialog` levels:** Use `QuickConfirmInline` (Level 1), `ConfirmDialog` (Level 2), or `ReasonDialog` (Level 3). Do not mix levels — see `frontend-guidelines.md` §Confirmation Levels.
