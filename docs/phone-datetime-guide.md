# Phone Number & DateTime Handling Guide

Guide for handling phone numbers and datetime in Foodya Backend API.

---

## 📱 Phone Number Handling

### Overview

Backend tự động **normalize** số điện thoại về định dạng quốc tế `+[country_code][number]`.

**Advantages:**
- ✅ User-friendly input (nhập dạng local: `0987654321`)
- ✅ Tự động thêm country code
- ✅ Consistent storage format
- ✅ Support multiple countries

---

### How It Works

**Input formats accepted:**
```
"0987654321"     → "+84987654321"  (local format)
"987654321"      → "+84987654321"  (without leading 0)
"+84987654321"   → "+84987654321"  (already normalized)
"098-765-4321"   → "+84987654321"  (with dashes)
"098 765 4321"   → "+84987654321"  (with spaces)
```

**Default country:** Vietnam (VN) - Country code: `+84`

---

### Supported Countries

| Country | Code | Country Code |
|---------|------|--------------|
| Vietnam | VN | +84 |
| United States | US | +1 |
| Taiwan | TW | +886 |
| China | CN | +86 |
| Japan | JP | +81 |
| South Korea | KR | +82 |
| Thailand | TH | +66 |
| Singapore | SG | +65 |

---

### API Usage Examples

#### Registration

**Request (any format works):**
```json
POST /api/v1/auth/register
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "SecurePass123!",
  "fullName": "John Doe",
  "phoneNumber": "0987654321",  // ← Local format
  "role": "CUSTOMER"
}
```

**Backend automatically converts to:**
```
phoneNumber: "+84987654321"
```

**Response:**
```json
{
  "accessToken": "...",
  "refreshToken": "...",
  "username": "johndoe"
}
```

---

#### Update Profile

**Request:**
```json
PATCH /api/v1/users/profile
{
  "phoneNumber": "098 765 4321"  // ← With spaces, no problem!
}
```

**Stored as:**
```
"+84987654321"
```

---

#### Restaurant Creation

**Request:**
```json
POST /api/v1/merchant/restaurants
{
  "name": "My Restaurant",
  "phoneNumber": "0901234567",
  "address": "123 Street",
  ...
}
```

**Stored as:**
```
"+84901234567"
```

---

### Validation Rules

**Valid formats:**
- ✅ `0987654321` (local with leading 0)
- ✅ `987654321` (local without 0)
- ✅ `+84987654321` (international)
- ✅ `098-765-4321` (with dashes)
- ✅ `098 765 4321` (with spaces)

**Invalid formats:**
- ❌ `123` (too short - must be 7-15 digits)
- ❌ `abcdefghij` (contains letters)
- ❌ `++84987654321` (invalid prefix)

**Error response:**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid phone number format: Phone number must be 7-15 digits."
}
```

---

### Frontend Best Practices

#### Option 1: Send raw input (Simplest)

```javascript
// User enters in local format
const phoneNumber = "0987654321";

// Just send as-is
await registerUser({
  ...userData,
  phoneNumber: phoneNumber  // Backend handles normalization
});
```

#### Option 2: Country code dropdown

```javascript
// UI với dropdown chọn quốc gia
const [countryCode, setCountryCode] = useState("+84");
const [phoneNumber, setPhoneNumber] = useState("");

// Combine before sending
const fullPhone = phoneNumber.startsWith("0")
  ? phoneNumber.substring(1)  // Remove leading 0
  : phoneNumber;

await registerUser({
  ...userData,
  phoneNumber: fullPhone  // Backend adds country code
});
```

#### Option 3: Display formatted

```javascript
// Hiển thị số điện thoại đã lưu
const displayPhone = (phone) => {
  // Backend returns: "+84987654321"
  // Display as: "+84 98 765 4321"
  if (phone.startsWith("+84")) {
    const num = phone.substring(3);
    return `+84 ${num.substring(0,2)} ${num.substring(2,5)} ${num.substring(5)}`;
  }
  return phone;
};
```

---

## 📅 DateTime Handling

### Overview

Backend sử dụng **ISO 8601** datetime format để tương thích tốt nhất với các client.

**Format:** `yyyy-MM-dd'T'HH:mm:ss`

**Timezone:** Asia/Ho_Chi_Minh (UTC+7)

---

### Supported Formats

#### Input (Request)

Backend accepts **ISO 8601 format**:

```
2025-12-31T18:44:08        ✅ Standard ISO 8601
2025-12-31T18:44:08.123    ✅ With milliseconds
2025-12-31T18:44:08Z       ✅ UTC timezone
2025-12-31T18:44:08+07:00  ✅ With timezone offset
```

#### Output (Response)

Backend returns:

```json
{
  "orderDate": "2025-12-31T18:44:08",
  "createdAt": "2025-12-31T15:30:00",
  "updatedAt": "2025-12-31T18:44:08"
}
```

---

### API Usage Examples

#### Create Order

**Request:**
```json
POST /api/v1/customers/orders
{
  "restaurantId": "...",
  "items": [...],
  "deliveryAddress": "123 Street",
  "orderDate": "2025-12-31T18:44:08"  // ← ISO 8601
}
```

**Response:**
```json
{
  "id": "...",
  "orderDate": "2025-12-31T18:44:08",
  "createdAt": "2025-12-31T18:44:10",
  ...
}
```

---

### Frontend Best Practices

#### JavaScript/TypeScript

```javascript
// Current datetime
const now = new Date().toISOString();
// Returns: "2025-12-31T11:44:08.123Z"

// Remove milliseconds and Z
const formatted = now.split('.')[0];
// Returns: "2025-12-31T11:44:08"

// Send to backend
const orderData = {
  ...data,
  orderDate: formatted
};
```

#### React

```javascript
// Date picker component
import { useState } from 'react';

function OrderForm() {
  const [orderDate, setOrderDate] = useState('');

  const handleDateChange = (e) => {
    // HTML5 datetime-local input returns: "2025-12-31T18:44"
    const value = e.target.value;
    setOrderDate(value + ":00");  // Add seconds
  };

  return (
    <input
      type="datetime-local"
      onChange={handleDateChange}
      value={orderDate.substring(0, 16)}  // Remove seconds for display
    />
  );
}
```

#### Display formatted

```javascript
// Display datetime in Vietnamese format
const formatDateTime = (isoString) => {
  const date = new Date(isoString);

  return date.toLocaleString('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    timeZone: 'Asia/Ho_Chi_Minh'
  });
};

// "2025-12-31T18:44:08" → "31/12/2025, 18:44:08"
```

---

### Validation Rules

**Valid formats:**
- ✅ `2025-12-31T18:44:08`
- ✅ `2025-12-31T18:44:08.123`
- ✅ `2025-12-31T18:44:08Z`
- ✅ `2025-12-31T18:44:08+07:00`

**Invalid formats:**
- ❌ `2025-12-31 18:44:08` (space instead of T)
- ❌ `31/12/2025 18:44:08` (DD/MM/YYYY format)
- ❌ `12-31-2025 18:44:08` (MM-DD-YYYY format)

**Error response:**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Failed to parse datetime: 2025-12-31 18:44:08"
}
```

---

## 🧪 Testing

### Phone Number Tests

```bash
# Valid inputs
curl -X POST /api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test1",
    "phoneNumber": "0987654321",
    ...
  }'
# → Stored as: "+84987654321"

curl -X POST /api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test2",
    "phoneNumber": "+84987654321",
    ...
  }'
# → Stored as: "+84987654321"

# Invalid input
curl -X POST /api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test3",
    "phoneNumber": "123",
    ...
  }'
# → Error: "Invalid phone number format: Must be 7-15 digits."
```

### DateTime Tests

```bash
# Valid inputs
curl -X POST /api/v1/customers/orders \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "orderDate": "2025-12-31T18:44:08",
    ...
  }'
# → Success

curl -X POST /api/v1/customers/orders \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "orderDate": "2025-12-31T18:44:08.123Z",
    ...
  }'
# → Success
```

---

## 💡 Migration Guide

### For Existing Data

If you have existing phone numbers in old format:

```sql
-- Backup first
CREATE TABLE users_backup AS SELECT * FROM users;

-- Check current format
SELECT phone_number FROM users LIMIT 10;

-- No migration needed!
-- Old data works fine with new endpoints
-- New entries will be normalized automatically
```

### For Existing Frontend

**Before (old format):**
```javascript
// Had to send exact format
phoneNumber: "+84987654321"
orderDate: "2025-12-31 18:44:08"
```

**After (new format):**
```javascript
// More flexible
phoneNumber: "0987654321"        // ← Simpler!
orderDate: "2025-12-31T18:44:08"  // ← ISO standard
```

---

## 📚 Related Documentation

- [API Documentation](./README.md)
- [Error Handling](./error-handling.md)
- [Configuration Guide](./configuration.md)
