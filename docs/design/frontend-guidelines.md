# Frontend Guidelines

Implementation guidelines for all developers working on the Foodya frontend. Read this before writing any code.

---

## Tech Stack

| Layer | Technology | Notes |
|---|---|---|
| Language | TypeScript | Strict mode enabled |
| Framework | React 18+ | Server components where applicable |
| Styling | Tailwind CSS v4 + CSS custom properties for design tokens | Token values in `globals.css` |
| Icons | Material Symbols (variable font or SVG sprite) | Outlined weight default |
| Maps | Goong Maps JS SDK | Wrap in `MapPicker` component; never call SDK directly outside wrapper |
| HTTP Client | Axios with interceptors | See API Integration section |
| State | Zustand (global) + React Query (server state) | No Redux |
| Forms | React Hook Form + Zod | All forms; no uncontrolled inputs |
| Realtime | Native `EventSource` (SSE) | Wrapped in `useSSE` hook |
| Routing | React Router v6 (or Next.js App Router) | Route guards via layout components |

---

## Folder Structure

```
src/
├── app/                      # Routes (Next.js app dir) or pages/
│   ├── (auth)/               # AuthShell layout
│   ├── (customer)/           # CustomerShell layout
│   ├── owner/                # OwnerShell layout
│   ├── shipper/              # DriverShell layout
│   └── admin/                # AdminShell layout
│
├── components/
│   ├── ui/                   # Design-system primitives (Button, Input, Card, etc.)
│   ├── layout/               # Shell components (TopNav, SideNavDrawer, etc.)
│   ├── order/                # OrderCard, OrderStatusTimeline, etc.
│   ├── restaurant/           # RestaurantCard, MenuItemCard, etc.
│   ├── driver/               # JobCard, ActiveDeliveryPanel, etc.
│   ├── admin/                # ApprovalTable, AuditLogTable, etc.
│   └── shared/               # EmptyState, StatusBadge, CountdownTimer, etc.
│
├── hooks/
│   ├── useSSE.ts             # EventSource wrapper
│   ├── useGeolocation.ts     # GPS position with state machine
│   ├── useCart.ts            # Cart state (React Query + optimistic updates)
│   └── useAuth.ts            # JWT, refresh, role checks
│
├── lib/
│   ├── api/                  # Axios instance + per-module API functions
│   │   ├── auth.ts
│   │   ├── orders.ts
│   │   ├── restaurants.ts
│   │   └── ...
│   ├── tokens.ts             # Design token constants (mirrors CSS vars)
│   └── utils.ts              # formatPrice, formatDate, etc.
│
├── stores/                   # Zustand stores
│   ├── authStore.ts
│   └── notificationStore.ts
│
└── types/                    # TypeScript interfaces mirroring SRS domain model
    ├── order.ts
    ├── restaurant.ts
    ├── user.ts
    └── ...
```

---

## Naming Conventions

### Files

| Type | Convention | Example |
|---|---|---|
| React component | PascalCase | `OrderCard.tsx` |
| Hook | camelCase, `use` prefix | `useSSE.ts` |
| Utility | camelCase | `formatPrice.ts` |
| Type file | camelCase | `order.ts` |
| Page/route | kebab-case (Next.js convention) | `order-history/page.tsx` |
| CSS module | PascalCase.module.css | `OrderCard.module.css` |

### Components

```tsx
// Props interface: ComponentName + Props
interface OrderCardProps {
  order: OrderSummary;
  variant: 'customer' | 'owner' | 'admin';
  onAction?: (action: string) => void;
}

// Named export (no default exports for components)
export function OrderCard({ order, variant, onAction }: OrderCardProps) { ... }
```

### Constants

```ts
// ALL_CAPS for true constants
const MAX_CART_ITEMS = 99;
const PAYMENT_TIMEOUT_SECONDS = 15 * 60;

// PascalCase for enum-like objects
const OrderStatus = {
  PENDING: 'PENDING',
  CONFIRMED: 'CONFIRMED',
} as const;
```

---

## Component Conventions

### 1. Single responsibility

Each component does one thing. If a component handles both data fetching and rendering, split into a container and a presentational component.

### 2. Variant over fork

Never create `BanReasonDialog.tsx` and `RejectReasonDialog.tsx`. Extend `ReasonDialog` with a `variant` or additional props.

### 3. Controlled inputs

All form inputs are controlled (value + onChange). No `defaultValue` except for uncontrolled edge cases.

### 4. No inline styles

All styling via Tailwind classes or CSS custom properties. The only exception is dynamically computed values (e.g., a progress bar width as a percentage).

### 5. Design token reference

```tsx
// ✅ Correct — uses design token
<div className="bg-[var(--color-primary)]">

// ❌ Wrong — hardcoded value
<div style={{ backgroundColor: '#E8530D' }}>
```

### 6. Confirmation levels

Before implementing any destructive action, choose the correct confirmation level:

| Level | Component | Use When |
|---|---|---|
| 1 | `QuickConfirmInline` | Reversible status advance (Mark Ready, Mark Picked Up) |
| 2 | `ConfirmDialog` | Irreversible but no accountability needed (Logout, Delete address) |
| 3 | `ReasonDialog` | Irreversible + accountability required (Ban, Reject, Exception, Suspend) |

---

## Accessibility

- All interactive elements must be keyboard-navigable and have focus styles.
- Use semantic HTML: `<button>` for actions, `<a>` for navigation, `<nav>`, `<main>`, `<header>`, `<section>`.
- ARIA roles: follow component-library.md spec per component.
- Images: `alt` text always. Decorative images: `alt=""`.
- Forms: every input has a visible `<label>` (never placeholder-only).
- Dialogs: `role="dialog"` or `role="alertdialog"`, `aria-modal="true"`, focus trapped, Escape to close.
- Colour: never convey meaning through colour alone — always include an icon or text label alongside `StatusBadge`.
- Minimum touch target: 44×44px on mobile.

---

## Responsive Strategy

**Desktop-first.** Design targets ≥ 1200px. Scale down with breakpoints.

| Breakpoint | CSS variable | Width |
|---|---|---|
| `xs` | — | < 480px |
| `sm` | `--bp-sm` | 480px |
| `md` | `--bp-md` | 768px |
| `lg` | `--bp-lg` | 1024px |
| `xl` | `--bp-xl` | 1200px |

### Key responsive behaviours

| Component | Desktop | Mobile |
|---|---|---|
| `SideNavDrawer` | Always visible (260px) | Hidden; hamburger opens as overlay |
| `BottomNav` | Hidden | Fixed bottom |
| Cart | Right-side sticky panel (320px) | Full-page at `/cart` |
| Checkout | Two-column | Single column, summary below form |
| `TwoColumnLayout` right panel | Sticky 320px | Moves below left column |
| Admin tables | Full column set | Horizontally scrollable |

---

## State Management

### Server state → React Query

Use `useQuery` and `useMutation` for all API data. Do not store fetched data in Zustand.

```ts
// ✅ Correct
const { data: restaurants } = useQuery({
  queryKey: ['restaurants', filters],
  queryFn: () => api.restaurants.list(filters),
});

// ❌ Wrong
const [restaurants, setRestaurants] = useState([]);
useEffect(() => { api.restaurants.list().then(setRestaurants); }, []);
```

### Global UI state → Zustand

Use Zustand only for UI state that crosses component boundaries: auth session, cart, notification count, active delivery phase.

```ts
// stores/authStore.ts
interface AuthStore {
  user: User | null;
  accessToken: string | null;
  setAuth: (user: User, token: string) => void;
  clearAuth: () => void;
}
```

### Local component state → useState / useReducer

Use local state for: form state (via React Hook Form), modal open/close, tab selection, toggle states.

---

## API Integration

### Axios instance

```ts
// lib/api/client.ts
const client = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  headers: { 'Content-Type': 'application/json' },
});

// Request interceptor: attach Bearer token
client.interceptors.request.use((config) => {
  const token = useAuthStore.getState().accessToken;
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// Response interceptor: handle 401 → auto-refresh
client.interceptors.response.use(
  (res) => res,
  async (error) => {
    if (error.response?.status === 401) {
      // attempt token refresh; if fails, logout
    }
    return Promise.reject(error);
  }
);
```

### Response envelope

All API responses follow the `ApiResponse` envelope. Unwrap in the API layer, not in components.

```ts
// lib/api/restaurants.ts
export async function listRestaurants(params: RestaurantFilters) {
  const { data } = await client.get<ApiResponse<Restaurant[]>>('/restaurants', { params });
  return data.data; // unwrap here
}
```

### Error codes

Map `ApiResponse.code` to user-facing messages in a central error dictionary:

```ts
// lib/errorMessages.ts
export const ERROR_MESSAGES: Record<string, string> = {
  AUTH_INVALID_CREDENTIALS: 'Invalid email or password.',
  AUTH_ACCOUNT_BANNED: 'Your account has been suspended.',
  RESTAURANT_CLOSED: 'This restaurant is currently closed.',
  ITEMS_UNAVAILABLE: 'Some items are no longer available.',
  JOB_ALREADY_TAKEN: 'This delivery was already accepted by another driver.',
  // ...
};
```

### Money values

All monetary values from the API are **integers in VND** (BR-08). Never store as float.

```ts
// ✅ Correct
function formatPrice(amountVND: number): string {
  return `₫${amountVND.toLocaleString('vi-VN')}`;
}

// ❌ Wrong
const total = subtotal * 1.1; // never float arithmetic on money
```

---

## Error Handling

### Hierarchy

1. **Field-level:** handled by React Hook Form + Zod schema. Show below the relevant input.
2. **Form-level:** catch 409/422 from API; display `AlertBanner` above the form.
3. **Page-level:** catch unhandled errors in React Error Boundary; render `EmptyState` (error variant).
4. **Network:** Axios interceptor catches network errors; show `Toast` (error + Retry action).
5. **Auth expiry:** Interceptor handles 401 → refresh → retry. If refresh fails → logout.

### Never swallow errors silently

```ts
// ✅ Correct
try {
  await placeOrder(payload);
} catch (err) {
  const code = getErrorCode(err);
  showToast({ variant: 'error', message: ERROR_MESSAGES[code] ?? 'Something went wrong.' });
}

// ❌ Wrong
try {
  await placeOrder(payload);
} catch (_) {} // silent failure
```

---

## Loading Patterns

Follow the loading hierarchy from `design-system.md`:

| Situation | Implementation |
|---|---|
| First load of a list screen | Render `SkeletonLoader` (count=3 minimum) while `isLoading=true` |
| Refetching / background refresh | Show stale data; add `LinearProgressBar` at page top |
| Button async action | Set button `loading={true}` (spinner replaces label) |
| Blocking form submission | `OverlaySpinner` over the form |
| Route transition | `LinearProgressBar` at top of viewport |

---

## Empty States

Every list or data-dependent section must handle the empty case:

```tsx
if (restaurants.length === 0) {
  return (
    <EmptyState
      illustration={<SearchEmptyIllustration />}
      headline="No restaurants found"
      description="Try adjusting your filters or search term."
      primaryAction={{ label: 'Clear filters', onClick: clearFilters }}
    />
  );
}
```

Never render `null`, an empty `<div>`, or plain text for empty states.

---

## Form Validation

Use Zod schemas for all validation. Define schemas in `lib/validations/`.

```ts
// lib/validations/address.ts
export const addressSchema = z.object({
  label: z.string().min(1, 'Label is required'),
  recipientName: z.string().min(1, 'Name is required'),
  phone: z.string().regex(/^0[0-9]{9}$/, 'Invalid phone number'),
  street: z.string().min(1, 'Street is required'),
  ward: z.string().min(1, 'Ward is required'),
  district: z.string().min(1, 'District is required'),
  city: z.string().min(1, 'City is required'),
  latitude: z.number().min(-90).max(90),
  longitude: z.number().min(-180).max(180),
});
```

**Rules:**
- Validate on blur for UX; re-validate on submit for safety.
- Never trust client-side price/total fields. Server always recomputes (BR-07, BR-08).
- Show first error only per field (not all errors simultaneously).
- Required field asterisk in label: `<label>Name <span aria-hidden="true">*</span></label>`.

---

## SSE (Real-Time Updates)

Wrap `EventSource` in a reusable hook:

```ts
// hooks/useSSE.ts
export function useSSE<T>(url: string | null, onEvent: (event: T) => void) {
  useEffect(() => {
    if (!url) return;
    const source = new EventSource(url, { withCredentials: true });
    source.onmessage = (e) => onEvent(JSON.parse(e.data));
    source.onerror = () => source.close();
    return () => source.close();
  }, [url]);
}
```

**Rules:**
- Open SSE connection only when the screen is mounted; close on unmount.
- Use `url = null` to disable subscription (e.g., when order is in terminal state).
- Fallback: if SSE is not supported or fails, fall back to polling every 5s via React Query `refetchInterval`.
- The Order Tracking screen must use an explicit `← Back` link (not browser back) to ensure SSE cleanup.

---

## Location (Driver)

```ts
// hooks/useGeolocation.ts
type GpsState = 'available' | 'degraded' | 'off';

export function useGeolocation(active: boolean) {
  // Returns { lat, lng, gpsState }
  // Posts location to /shipper/location every 10s when active=true
  // Queues failed posts and retries on reconnect
}
```

**Rules:**
- Request permission on Driver first login (after approval screen).
- Never block navigation if permission is denied — show `LocationStatusBanner` instead.
- Stop posting when the active delivery order reaches a terminal state.

---

## Security

- Never log JWT tokens, passwords, or payment data to the console.
- Never store sensitive data in `localStorage` beyond JWT tokens. Clear on logout.
- Sanitise any user-generated content rendered as HTML. Never use `dangerouslySetInnerHTML` unless content is admin-controlled.
- Payment redirect: use `window.location.href` (not `<a target="_blank">`) to navigate to provider checkout — prevents tab proliferation.
- Address data privacy: use `PaymentInfoBadge` and `DeliveryAddress` components which enforce the role-based display rules. Never render full customer address in owner or pre-pickup driver views.

---

## Push Notifications

On first successful login for Customer, Owner, and Driver roles:

1. Request browser push permission.
2. If granted, obtain FCM/service-worker token.
3. `POST /notifications/register-device { token, platform: 'web' }`.
4. This is a silent background call — no UI indication beyond the browser's native permission prompt.

---

## Developer Checklist (per screen)

Before marking any screen as complete:

- [ ] Matches the spec in `pages/<screen>.md`.
- [ ] Uses design tokens only — no hardcoded colours, sizes, or shadows.
- [ ] All components sourced from `component-library.md` — no reinventions.
- [ ] Loading state implemented (`SkeletonLoader` or `Spinner`).
- [ ] Empty state implemented (`EmptyState`).
- [ ] Error state implemented (field, form, or page level as appropriate).
- [ ] Responsive at all three breakpoints (desktop, tablet, mobile).
- [ ] Keyboard navigable; focus states visible.
- [ ] ARIA roles and labels in place.
- [ ] API calls use React Query; no raw `useState` + `useEffect` for server data.
- [ ] Money values formatted with `formatPrice()`; never as floats.
- [ ] Route guard matches the `Permission` column in `screen-inventory.md`.
