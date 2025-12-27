# Test Accounts for Frontend Testing

All test accounts use the same password: **`password123`**

## 📋 Account Format
- Username: `test.{role}.{number}`
- Email: `test.{role}.{number}@example.com`
- Password: `password123`

---

## 👨‍💼 ADMIN Accounts (2 accounts)

| Username | Email | Role | Status |
|----------|-------|------|--------|
| `test.admin.01` | test.admin.01@example.com | ADMIN | Active ✅ |
| `test.admin.02` | test.admin.02@example.com | ADMIN | Active ✅ |

**Permissions:**
- Full access to all endpoints
- Can manage users, restaurants, menu items
- Can delete any resource

---

## 🏪 MERCHANT Accounts (3 accounts)

| Username | Email | Role | Restaurants Owned |
|----------|-------|------|-------------------|
| `test.merchant.01` | test.merchant.01@example.com | MERCHANT | The Italian Corner, Pho Saigon |
| `test.merchant.02` | test.merchant.02@example.com | MERCHANT | Sushi Master, Burger Heaven |
| `test.merchant.03` | test.merchant.03@example.com | MERCHANT | Thai Spice |

**Permissions:**
- Can create/update/delete own restaurants
- Can manage menu items for own restaurants
- Cannot access other merchants' resources

---

## 👥 CUSTOMER Accounts (5 accounts)

| Username | Email | Role | Status |
|----------|-------|------|--------|
| `test.customer.01` | test.customer.01@example.com | CUSTOMER | Active ✅ |
| `test.customer.02` | test.customer.02@example.com | CUSTOMER | Active ✅ |
| `test.customer.03` | test.customer.03@example.com | CUSTOMER | Active ✅ |
| `test.customer.04` | test.customer.04@example.com | CUSTOMER | Active ✅ |
| `test.customer.05` | test.customer.05@example.com | CUSTOMER | Active ✅ |

**Permissions:**
- Can view restaurants and menu items
- Can place orders
- Can view/update own profile
- Cannot create/update/delete restaurants or menu items

---

## 🚚 DELIVERY Accounts (3 accounts)

| Username | Email | Role | Status |
|----------|-------|------|--------|
| `test.delivery.01` | test.delivery.01@example.com | DELIVERY | Active ✅ |
| `test.delivery.02` | test.delivery.02@example.com | DELIVERY | Active ✅ |
| `test.delivery.03` | test.delivery.03@example.com | DELIVERY | Active ✅ |

**Permissions:**
- Can view assigned deliveries
- Can update delivery status
- Cannot access customer/merchant specific features

---

## 🧪 Testing Scenarios

### 1. Admin Testing
```bash
# Login as admin
POST /api/v1/auth/login
{
  "username": "test.admin.01",
  "password": "password123"
}

# Test admin-only endpoints
DELETE /api/v1/restaurants/{id}
GET /api/v1/admin/users
