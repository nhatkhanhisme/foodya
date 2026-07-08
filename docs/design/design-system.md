# Design System

All design tokens and rules for Foodya. Every value here is a decision — do not override with hardcoded values. Reference tokens by name in all implementations.

---

## Brand Personality

| Attribute | Design Expression |
|---|---|
| Friendly | Warm palette, rounded corners, humanist typeface |
| Fast | High-contrast CTAs, minimal steps, clear hierarchy |
| Premium | Refined spacing, restrained colour use, quality type |
| Clean | Generous whitespace, no decorative elements, purpose-driven only |

**Inspiration:** Material Design 3 token architecture, adapted for food delivery.

---

## Colours

### Primitive Palette

**Orange (Primary)**

| Token | Value |
|---|---|
| `orange-50` | `#FFF3EE` |
| `orange-100` | `#FFE4D4` |
| `orange-200` | `#FFC9AA` |
| `orange-300` | `#FFA07A` |
| `orange-400` | `#FF7A4D` |
| `orange-500` | `#FF6B35` |
| `orange-600` | `#E8530D` |
| `orange-700` | `#C44006` |
| `orange-800` | `#9E3205` |
| `orange-900` | `#7A2503` |
| `orange-950` | `#3D1201` |

**Neutral Warm**

| Token | Value |
|---|---|
| `neutral-0` | `#FFFFFF` |
| `neutral-50` | `#FAF7F5` |
| `neutral-100` | `#F5F0ED` |
| `neutral-200` | `#EDE6E1` |
| `neutral-300` | `#DDD4CE` |
| `neutral-400` | `#C4B9B3` |
| `neutral-500` | `#9E918A` |
| `neutral-600` | `#786B63` |
| `neutral-700` | `#574D47` |
| `neutral-800` | `#38302C` |
| `neutral-900` | `#201A17` |
| `neutral-950` | `#120D0B` |

**Semantic Primitives**

| Token | Value |
|---|---|
| `green-50` | `#F0FDF4` |
| `green-100` | `#DCFCE7` |
| `green-600` | `#16A34A` |
| `red-50` | `#FEF2F2` |
| `red-100` | `#FEE2E2` |
| `red-600` | `#DC2626` |
| `blue-50` | `#EFF6FF` |
| `blue-100` | `#DBEAFE` |
| `blue-600` | `#2563EB` |
| `amber-100` | `#FEF3C7` |
| `amber-600` | `#D97706` |

---

### Semantic Colour Tokens (Light Theme)

**Primary**

| Token | Value | Use |
|---|---|---|
| `color-primary` | `#E8530D` | Primary CTA buttons, active nav state |
| `color-on-primary` | `#FFFFFF` | Text/icons on primary |
| `color-primary-container` | `#FFE4D4` | Tonal backgrounds, selected chips |
| `color-on-primary-container` | `#7A2503` | Text on primary container |
| `color-primary-hover` | `#C44006` | Primary button hover |
| `color-primary-pressed` | `#9E3205` | Primary button active/pressed |

**Secondary**

| Token | Value |
|---|---|
| `color-secondary` | `#D97706` |
| `color-on-secondary` | `#FFFFFF` |
| `color-secondary-container` | `#FEF3C7` |
| `color-on-secondary-container` | `#B45309` |

**Surface**

| Token | Value | Use |
|---|---|---|
| `color-background` | `#FAF7F5` | Page background |
| `color-surface` | `#FFFFFF` | Card surfaces, inputs |
| `color-surface-low` | `#FFF3EE` | Warm-tint areas |
| `color-surface-container` | `#F5F0ED` | Inactive inputs, tab backgrounds |
| `color-surface-container-high` | `#EDE6E1` | Dividers, subtle borders |
| `color-surface-inverse` | `#201A17` | Toast/snackbar backgrounds |
| `color-on-surface` | `#201A17` | Primary text |
| `color-on-surface-variant` | `#574D47` | Secondary text, placeholders |
| `color-on-surface-disabled` | `#9E918A` | Disabled text |
| `color-on-surface-inverse` | `#FAF7F5` | Text on dark surface |

**Outline**

| Token | Value |
|---|---|
| `color-outline` | `#9E918A` |
| `color-outline-variant` | `#DDD4CE` |

**Status**

| Token | Value |
|---|---|
| `color-success` | `#16A34A` |
| `color-on-success` | `#FFFFFF` |
| `color-success-container` | `#DCFCE7` |
| `color-on-success-container` | `#14532D` |
| `color-warning` | `#D97706` |
| `color-on-warning` | `#FFFFFF` |
| `color-warning-container` | `#FEF3C7` |
| `color-on-warning-container` | `#78350F` |
| `color-error` | `#DC2626` |
| `color-on-error` | `#FFFFFF` |
| `color-error-container` | `#FEE2E2` |
| `color-on-error-container` | `#7F1D1D` |
| `color-info` | `#2563EB` |
| `color-info-container` | `#DBEAFE` |
| `color-on-info-container` | `#1E3A8A` |

---

### Colour Rules

- `color-primary` is used exclusively for the **single most important CTA** on a screen and active navigation states. Never decorative.
- Never use orange text on white backgrounds below `title-large` size — insufficient contrast.
- Maximum 3 distinct hues visible simultaneously on one screen.
- Status colours must always use their container background pair — never raw on white.
- WCAG AA minimum: all text must achieve 4.5:1 contrast against its background.

---

## Typography

### Typefaces

| Role | Family | Fallback |
|---|---|---|
| All UI text | `Plus Jakarta Sans` | `system-ui`, `-apple-system`, `sans-serif` |
| Prices, IDs, codes | `JetBrains Mono` | `Courier New`, `monospace` |

**Rationale:** Plus Jakarta Sans has rounded terminals (friendly), clean geometry (premium), and strong Vietnamese diacritic support.

### Type Scale

| Token | Size | Line Height | Weight | Letter Spacing | Use |
|---|---|---|---|---|---|
| `type-display-lg` | 57px | 64px | 700 | -0.25px | Hero only |
| `type-display-md` | 45px | 52px | 700 | 0 | Large hero |
| `type-display-sm` | 36px | 44px | 600 | 0 | Section heroes |
| `type-headline-lg` | 32px | 40px | 700 | 0 | Page titles |
| `type-headline-md` | 28px | 36px | 600 | 0 | Section headings |
| `type-headline-sm` | 24px | 32px | 600 | 0 | Card titles, modal headings |
| `type-title-lg` | 22px | 28px | 600 | 0 | Dialog titles |
| `type-title-md` | 16px | 24px | 600 | 0.15px | Component titles, table headers |
| `type-title-sm` | 14px | 20px | 500 | 0.1px | Subsection labels |
| `type-body-lg` | 16px | 24px | 400 | 0.5px | Primary body |
| `type-body-md` | 14px | 20px | 400 | 0.25px | Secondary body, descriptions |
| `type-body-sm` | 12px | 16px | 400 | 0.4px | Captions, timestamps |
| `type-label-lg` | 14px | 20px | 600 | 0.1px | Button labels, active nav |
| `type-label-md` | 12px | 16px | 500 | 0.5px | Chips, tabs, badge text |
| `type-label-sm` | 11px | 16px | 500 | 0.5px | Notification count badges |
| `type-price` | 16px | 24px | 700 | 0 | Item prices (JetBrains Mono, tabular) |
| `type-price-lg` | 20px | 28px | 700 | 0 | Cart total, order total |
| `type-code` | 13px | 20px | 400 | 0 | Order IDs, trace IDs |

### Typography Rules

- Prices: always `JetBrains Mono`, tabular figures, integer only. Format: `₫45.000`.
- Never centre-align body text longer than 2 lines.
- Maximum line length: 72 characters for body text.
- Never skip more than one size step downward within the same card or section.
- Truncate with `text-overflow: ellipsis` for restaurant names and menu item names. Never clip price or status text.

---

## Spacing

**Base unit: 4px.** All spacing values are multiples of 4.

| Token | Value | Common Use |
|---|---|---|
| `space-0` | `0px` | |
| `space-1` | `4px` | Icon micro gaps, badge padding |
| `space-2` | `8px` | Inline gaps, dense list padding |
| `space-3` | `12px` | Chip horizontal padding |
| `space-4` | `16px` | Standard component padding, card inner |
| `space-5` | `20px` | List item vertical padding |
| `space-6` | `24px` | Section header margin, modal padding |
| `space-8` | `32px` | Between card sections |
| `space-10` | `40px` | Section gaps on desktop |
| `space-12` | `48px` | Page horizontal padding (desktop) |
| `space-14` | `56px` | Top nav / bottom nav height |
| `space-16` | `64px` | Large section spacing |
| `space-20` | `80px` | Hero padding |

**Component spacing:**
- Button internal: `space-4` vertical, `space-6` horizontal (default size)
- Card padding: `space-6` all sides
- Form field label-to-input gap: `space-1`
- Consecutive form fields: `space-5` apart
- Page section spacing: `space-10`

---

## Border Radius

| Token | Value | Use |
|---|---|---|
| `radius-none` | `0px` | Full-bleed images, horizontal dividers |
| `radius-xs` | `4px` | Badges, status pills (compact) |
| `radius-sm` | `6px` | Input fields (inner elements) |
| `radius-md` | `10px` | Small buttons, chips |
| `radius-lg` | `14px` | Cards, standard buttons, outlined inputs |
| `radius-xl` | `20px` | Large cards, restaurant hero |
| `radius-2xl` | `28px` | Modals, bottom sheet top corners |
| `radius-3xl` | `36px` | Feature callout cards |
| `radius-full` | `9999px` | Avatars, FAB, pill chips, notification badges |

**Rules:**
- Interactive components (buttons, chips, inputs) share the same radius tier within a section.
- Image containers use the same radius as their parent card.
- Never use `radius-full` on rectangular content containers.

---

## Elevation (Shadows)

| Token | Box Shadow | Use |
|---|---|---|
| `elevation-0` | `none` | Page background, flat sections |
| `elevation-1` | `0 1px 3px rgba(0,0,0,.08), 0 1px 2px rgba(0,0,0,.06)` | Cards at rest, input fields |
| `elevation-2` | `0 3px 8px rgba(0,0,0,.10), 0 1px 3px rgba(0,0,0,.08)` | Raised cards, dropdown menus |
| `elevation-3` | `0 6px 16px rgba(0,0,0,.12), 0 2px 6px rgba(0,0,0,.08)` | Navigation drawers, side panels |
| `elevation-4` | `0 10px 24px rgba(0,0,0,.14), 0 4px 10px rgba(0,0,0,.10)` | Modals, dialogs |
| `elevation-5` | `0 20px 40px rgba(0,0,0,.16), 0 8px 16px rgba(0,0,0,.10)` | FABs, sticky toasts |

**Rules:**
- TopNav: `elevation-0` at top of page, `elevation-1` on scroll.
- Dialogs always `elevation-4`.
- Cards: `elevation-1` at rest, `elevation-2` on hover.
- Never apply elevation to coloured backgrounds — only to white/surface containers.

---

## Buttons

### Variants

| Variant | Background | Text | Border | Use |
|---|---|---|---|---|
| Filled | `color-primary` | `color-on-primary` | None | Primary CTA |
| Filled Tonal | `color-primary-container` | `color-on-primary-container` | None | Secondary CTA |
| Elevated | `color-surface` + `elevation-1` | `color-primary` | None | Floating action on coloured bg |
| Outlined | Transparent | `color-primary` | 1.5px `color-primary` | Neutral action |
| Text | Transparent | `color-primary` | None | Low-emphasis |
| Destructive Filled | `color-error` | `color-on-error` | None | Destructive primary |
| Destructive Outlined | Transparent | `color-error` | 1.5px `color-error` | Destructive secondary |

### Sizes

| Size | Height | H-Padding | Font | Icon | Use |
|---|---|---|---|---|---|
| XSmall | 28px | `space-3` | `type-label-md` | 16px | Table row actions |
| Small | 36px | `space-4` | `type-label-md` | 18px | Secondary page actions |
| Medium (default) | 44px | `space-6` | `type-label-lg` | 20px | Standard |
| Large | 52px | `space-8` | `type-label-lg` | 22px | Hero CTAs, checkout confirm |

All sizes use `radius-lg` except XSmall uses `radius-md`.

### States

| State | Filled | Outlined | Text |
|---|---|---|---|
| Default | `color-primary` bg | `color-primary` border + text | `color-primary` text |
| Hover | `color-primary-hover` | `color-primary-container` bg | `color-primary-container` bg |
| Pressed | `color-primary-pressed` | Darken | Darken |
| Focused | Default + 3px `color-primary` ring, 2px offset | Same | Same |
| Disabled | `neutral-200` bg, `neutral-400` text | `neutral-300` border + text | `neutral-400` text |
| Loading | Spinner replaces label, disabled pointer | Same | Same |

---

## Inputs

### Variants

- **Outlined** (default): All standard form fields.
- **Filled**: Search bar, in-table filters.

Both use `radius-lg`. Height: 52px.

### States

| State | Border | Label |
|---|---|---|
| Default | 1.5px `color-outline-variant` | `color-on-surface-variant` |
| Hover | 1.5px `color-outline` | — |
| Focused | 2px `color-primary` | `color-primary`, floats above |
| Error | 2px `color-error` | `color-error` |
| Disabled | 1px `color-outline-variant` at 38% | `color-on-surface-disabled` |
| Read-only | 1px dashed `color-outline-variant` | Subdued |

**Validation:** Show error on blur or submit attempt. Never on first keystroke. Clear error when value changes.

---

## Cards

| Variant | Background | Shadow | Border |
|---|---|---|---|
| Elevated | `color-surface` | `elevation-1` | None |
| Filled | `color-surface-container` | None | None |
| Outlined | `color-surface` | None | 1px `color-outline-variant` |

Hover: Elevated → `elevation-2`; Filled → `elevation-1`; Outlined → `elevation-1` + border `color-outline`.

---

## Tables

- Header: `type-title-md`, `color-on-surface-variant`, `color-surface-container` background.
- Row height: 56px standard, 48px compact.
- Alternating row background: none (use `border-bottom: 1px color-outline-variant` instead).
- Hover: `color-surface-low` background on row.
- Sortable column: `unfold_more` icon; active sort shows direction arrow.
- Pagination: below table, right-aligned.

---

## Forms

- Labels: `type-label-md`, above the input.
- Field groups: separated by `space-5`.
- Required indicator: asterisk after label text, `color-error`.
- Error message: `type-body-sm`, `color-error`, with `error` icon (16px), appears below input on blur or submit.
- Form-level error: `AlertBanner` (error variant) above the first field.
- Submit button: always the last element, full-width on mobile.

---

## Dialogs

| Variant | Max Width | Use |
|---|---|---|
| Basic | 400px | Simple message + 2 buttons |
| Confirmation | 440px | Destructive action confirmation |
| Form | 560px | Short inline forms |
| Full | 720px | Complex forms / dispute resolution |

**Anatomy:** Scrim (`rgba(0,0,0,0.48)`) + container (`elevation-4`, `radius-2xl`) + padding `space-6` + right-aligned actions.
**Behaviour:** Focus trapped. Escape closes non-destructive dialogs. Destructive dialogs (`ConfirmDialog` variant=destructive) require button click to dismiss.

---

## Badges

- **Notification count badge:** `radius-full`, `color-primary` background, `color-on-primary` text, `type-label-sm`. Shows 1–99; shows "99+" beyond.
- **Status badge:** See `StatusBadge` in `component-library.md`.
- **Live indicator:** Pulsing `radio_button_checked` icon, `color-primary`.

---

## Empty States

**Anatomy:** Centred illustration (160×160px SVG) + `type-headline-sm` headline + `type-body-md` description (max 320px, max 2 lines, centred) + optional CTA buttons.

**Rules:**
- Always vertically centred in their container.
- Illustrations are warm-toned line art with brand orange accents.
- Never use stock photos in empty states.

---

## Loading States

| Pattern | When to Use |
|---|---|
| `SkeletonLoader` | Initial content load of any list or card-based screen |
| `Spinner` (inline, sm) | Inside buttons during async action |
| `Spinner` (md/lg) | Component-level loading (e.g., a chart refreshing) |
| `LinearProgressBar` | Page/route transitions (top of viewport) |
| `OverlaySpinner` | Blocking async operations (checkout submit, login) |

**Skeleton rule:** Show minimum 3 skeleton cards when loading a list. Match exact component dimensions.

---

## Error States

| Level | Component | Trigger |
|---|---|---|
| Field | Error text below input | Blur or submit with invalid value |
| Form | `AlertBanner` (error) above form | Server rejection (409, 422) affecting the whole form |
| Inline warning | `AlertBanner` (warning) | Non-blocking contextual warning (cart item unavailable) |
| Page | `EmptyState` (error variant) | Full page/data fetch failure |
| Network | `Toast` (error, action variant) | Network disconnection; shows "Retry" action |
| HTTP 403 | Standalone screen | Permission denied |
| HTTP 404 | Standalone screen | Resource not found |
| HTTP 500 | Standalone screen | Server error; shows `traceId` in `type-code` for support |

---

## Icons

**Library:** Material Symbols, Outlined weight, Grade 0, Optical Size 24px.
**Exception:** Use Filled variant for active navigation states and positive status indicators only.

### Sizes

| Token | Size | Use |
|---|---|---|
| `icon-xs` | 16px | Inline with `type-body-sm`, badge icons |
| `icon-sm` | 18px | Chips, small buttons |
| `icon-md` | 20px | Standard components, nav items |
| `icon-lg` | 24px | Primary icons, TopNav, FAB |
| `icon-xl` | 32px | Dialog icons |
| `icon-2xl` | 48px | Empty state feature icons |

---

## Animation Tokens

| Token | Value | Use |
|---|---|---|
| `motion-dialog-enter` | `scale(0.95)→scale(1)` + `opacity 0→1`, 200ms ease-out | Modal/dialog enter |
| `motion-sheet-enter` | `translateY(100%)→translateY(0)`, 300ms ease-out | Bottom sheet, mobile cart |
| `motion-toast-enter` | `translateY(16px)→translateY(0)` + `opacity 0→1`, 180ms ease-out | Toast stack |
| `motion-card-appear` | `translateY(8px)→translateY(0)` + `opacity 0→1`, 120ms ease-out | SSE new card in feed |
| `motion-page-transition` | `opacity 0→1`, 100ms linear | Route change |
| `motion-shimmer` | Background-position sweep, 1.4s ease-in-out infinite | Skeleton loader |
| `motion-success-check` | Stroke-dashoffset animation, 400ms ease-in-out | Delivery complete, payment success |

---

## Layout Grid

| Breakpoint | Columns | Gutter | Margin |
|---|---|---|---|
| Desktop (≥ 1200px) | 12 | 24px | 48px |
| Tablet (768–1199px) | 8 | 16px | 24px |
| Mobile (< 768px) | 4 | 16px | 16px |
