# Platform Configuration

## Purpose
Read-only display of application-level configuration values. No editing from the UI — changes require a code deploy. Exists so admins can reference current values without asking engineering.

> **Note:** `GET /admin/config` endpoint not yet defined in SRS §7.7. This screen is pending that endpoint.

## User Story
As an admin, I want to see the current platform configuration values so I know what thresholds and fees are active without asking the engineering team.

## Entry Points
- SideNav "Configuration" item

## Exit Points
N/A (dead-end view screen).

## Layout
`AdminShell`. Single-column, `max-width: 720px`.

## Components
- `AlertBanner` (info): "These values are set in application.yml. To change them, update the configuration and redeploy. Changes are tracked in git history."
- Grouped key-value sections:
  - **Pricing:** Base shipping fee, Per-km rate, Formula display
  - **Order Management:** Restaurant response window, Payment timeout, Auto-cancel on timeout
  - **Moderation:** Cancellation threshold (N cancellations / 30 days), Login rate limit, General rate limit
  - **Auth:** Access token TTL, Refresh token TTL
  - **Environment:** Active payment providers, Mapping provider, Environment name
- "Last refreshed: {time}" + `Button` (Text) — "Refresh"
- `SkeletonLoader`

## Business Rules
- No edit controls on this screen. Every value is display-only.
- Values are fetched live from the running app config (not cached beyond the request).
- Monetary config values displayed with `formatPrice()` formatting.

## Validation Rules
N/A (read-only).

## API Endpoints
```
GET /api/v1/admin/config    [GAP — not in SRS §7.7]
Response: {
  baseShippingFee: number,
  perKmRate: number,
  restaurantResponseWindowMinutes: number,
  paymentTimeoutMinutes: number,
  autoCancelOnNoResponse: boolean,
  cancellationThreshold: number,
  cancellationWindowDays: number,
  loginRateLimitPerMinute: number,
  generalRateLimitPerMinute: number,
  accessTokenTtlHours: number,
  refreshTokenTtlDays: number,
  activePaymentProviders: string[],
  mappingProvider: string,
  environment: string
}
```

## Loading State
`SkeletonLoader` (text variant) per config value while fetching.

## Empty State
N/A.

## Error State
`AlertBanner` (error): "Could not load configuration. Retry."

## Permissions
`ADMIN`.

## Responsive Notes
Single-column at all breakpoints.

## Developer Notes
- Until the endpoint is built: render the screen with hardcoded placeholder values pulled from environment variables (VITE_BASE_SHIPPING_FEE, etc.) as a fallback. Clearly label them as "Local configuration — may differ from production."
- "Refresh" button: invalidate and refetch the config query.
