# Foodya — Design Documentation

## Purpose

This directory is the **single source of truth** for all approved design decisions for the Foodya food delivery platform. It documents the product's information architecture, user flows, component system, and per-screen specifications.

These documents exist to:

- Give frontend engineers a complete, unambiguous implementation specification.
- Enable AI coding assistants to generate correct, consistent UI code without requiring full conversation context.
- Prevent design drift by centralising decisions that have already been made and approved.

---

## Document Map

| File | What It Contains |
|---|---|
| [`screen-inventory.md`](./screen-inventory.md) | All 44 screens: role, purpose, entry/exit points, APIs, permissions |
| [`user-flows.md`](./user-flows.md) | Every approved user flow per role with Mermaid diagrams |
| [`navigation-map.md`](./navigation-map.md) | Route hierarchy, nav shells, breadcrumbs, deep links |
| [`component-library.md`](./component-library.md) | Every reusable component: props, states, variants, accessibility |
| [`design-system.md`](./design-system.md) | Design tokens: colour, typography, spacing, radius, elevation, shadows |
| [`frontend-guidelines.md`](./frontend-guidelines.md) | Tech stack, folder structure, naming, state management, API integration |
| [`pages/`](./pages/) | One `.md` per screen with full layout, component, API, and state spec |

---

## How to Use These Documents

### For Human Engineers

Read `frontend-guidelines.md` first for project conventions. Then consult the relevant `pages/` file before implementing any screen. Cross-reference `component-library.md` before building any new component.

### For AI Coding Assistants

**Before generating any frontend code, read in this order:**

1. `design-system.md` — understand colour tokens, spacing scale, and typography before touching any styles.
2. `component-library.md` — check whether the component you are about to build already exists.
3. `navigation-map.md` — understand the shell the screen lives in (TopNav vs SideNav vs AuthShell).
4. `pages/<screen-name>.md` — read the full specification for the specific screen.
5. `frontend-guidelines.md` — follow naming conventions, folder structure, and API integration patterns.

**Do not invent design decisions.** If a value (colour, spacing, radius) is not in `design-system.md`, use the closest defined token and flag it as a question for the designer.

**Do not create new components** unless `component-library.md` confirms no equivalent exists.

---

## Scope

These documents cover the **MVP** as defined in `FOODYA_SRS.md`. Phase 2 features (AI recommendations, coupon engine, real-time chat) are explicitly out of scope and must not be referenced.

### Roles Covered

| Role | Auth | Approval Required |
|---|---|---|
| Guest | None | — |
| Customer | JWT | No |
| Restaurant Owner | JWT | Yes (admin) |
| Shipper (Driver) | JWT | Yes (admin) |
| Admin | JWT | No (provisioned directly) |

---

## Known Gaps (Pre-Implementation)

The following backend endpoints are referenced in page specs but are **not yet defined** in the SRS. They must be specced and approved before the dependent screens are built.

| Endpoint | Needed For |
|---|---|
| `PATCH /admin/users/{id}/unban` | User unban |
| `PATCH /admin/shippers/{id}/reject` | Shipper rejection |
| `PATCH /admin/restaurants/{id}/suspend` | Restaurant suspension |
| `GET /admin/audit-log` | Audit log screen |
| `GET /admin/disputes` | Disputes queue |
| `POST /admin/disputes/{id}/resolve` | Dispute resolution |
| `POST /admin/payments/{id}/refund` | Admin-triggered refund |
| `POST /orders/{id}/report` | Customer order flag |
| `POST /reviews/{id}/report` | Review flag |
| `GET /admin/config` | Platform config screen |
| `POST /notifications/register-device` | Push token registration |

---

## Versioning

These documents track the **approved MVP design**. Any change to a flow, component, or screen spec requires:

1. A pull request updating the relevant `.md` file(s).
2. A note in the PR description explaining what changed and why.
3. Approval from the lead designer or product owner.

Do not implement features that contradict these documents without updating them first.
