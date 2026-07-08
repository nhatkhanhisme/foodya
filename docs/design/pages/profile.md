# Profile

## Purpose
View and update personal account information. Shared across Customer, Owner, and Driver with role-specific extensions.

## User Story
As an authenticated user, I want to update my name, phone, and avatar so my account information is current.

## Entry Points
- BottomNav "Profile" tab (Customer, Driver)
- SideNav "Profile" item (Owner, Admin)
- AvatarMenu dropdown (all roles)

## Exit Points
- "Change password" link → `/auth/change-password` or modal
- "Manage addresses" link → `/profile/addresses` (Customer only)
- "Save changes" → stays on Profile (success toast)
- "Log out" → `/auth/login`

## Layout
Role-appropriate shell. Single-column form card, `max-width: 640px`.

## Components

### All roles
- `AvatarUpload` (80px circle, click to upload)
- `FormField` (full name)
- `FormField` (email — read-only)
- `PhoneInput` (+84 prefix, editable)
- `FormField` (username — display only, read-only per BR-01)
- Account info (role badge, member since date)
- Divider
- Link — "Change password"
- Divider
- `Button` (Outlined) — "Discard"
- `Button` (Filled) — "Save changes"
- `Button` (Text, Destructive) — "Log out"

### Customer only (additional)
- Link — "Manage delivery addresses →"

### Driver only (additional)
- Section: **Vehicle Information** (read-only after approval)
  - Vehicle type
  - License plate
  - Note: "To update vehicle info, contact support@foodya.vn"

## User Actions
1. Upload new avatar.
2. Edit full name, phone.
3. Click "Save changes".
4. Navigate to Change Password.
5. (Customer) Navigate to Address Management.
6. Log out.

## Business Rules
- Email is not editable (login identifier — UC-C02).
- Username is display-only and not used for authentication (SRS §6.1).
- Role is not changeable after registration (BR-01).
- Driver vehicle information is read-only after ShipperProfile.status = APPROVED.

## Validation Rules
| Field | Rule |
|---|---|
| Full name | Required, min 2 chars |
| Phone | Valid Vietnamese format |

## API Endpoints
```
GET /api/v1/users/me
Response: UserProfile { id, email, username, fullName, phone, role, status, profileImageUrl, createdAt }

PUT /api/v1/users/me
Body: { fullName, phone, profileImageUrl? }
Response 200: updated UserProfile
```

Image upload: separate pre-signed URL or multipart endpoint (implementation detail).

## Loading State
`SkeletonLoader` (text variant) on full name and phone while fetching.

## Empty State
N/A.

## Error State
| Error | Display |
|---|---|
| PUT fails (400) | Inline field errors |
| PUT fails (network) | `Toast` (error + Retry) |
| Upload fails | `Toast` (error): "Could not upload image. Try again." |

## Permissions
`AUTH` (any authenticated user).

## Responsive Notes
Single column at all breakpoints. Avatar upload centred on mobile.

## Accessibility
- `AvatarUpload`: `<input type="file" accept="image/*">` hidden behind a visible button; `aria-label="Upload profile photo"`.
- Read-only fields: `aria-readonly="true"`, `tabindex="-1"` (or display as text, not input).

## Developer Notes
- "Discard" reverts form to last saved state (reset React Hook Form).
- On successful PUT: show `Toast` (success): "Profile updated."
- Avatar upload: validate file type (JPEG/PNG only) and size (max 5MB) client-side before upload.
- Do not expose a "delete account" option in MVP.
