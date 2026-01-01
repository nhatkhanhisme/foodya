# Order API (Customer)

API cho khách hàng tạo và quản lý đơn hàng.

**Base Path**: `/api/v1/customers/orders`

**Authentication**: ✅ Required (Bearer Token)

---

## 📋 Endpoints

### 1. Tạo đơn hàng mới

Khách hàng tạo đơn hàng mới.

- **URL**: `POST /api/v1/customers/orders`
- **Authentication**: ✅ Required
- **Content-Type**: `application/json`

#### Request Headers

```
Authorization: Bearer <access_token>
Content-Type: application/json
```

#### Request Body

```json
{
  "customerId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "restaurantId": "8f8e8334-9347-4933-9333-875865538050",
  "items": [
    {
      "menuItemId": "a1b2c3d4-1234-5678-9abc-def123456789",
      "quantity": 2,
      "specialInstructions": "No onions please"
    },
    {
      "menuItemId": "b2c3d4e5-2345-6789-abcd-ef1234567890",
      "quantity": 1,
      "specialInstructions": ""
    }
  ],
  "deliveryAddress": "KTX Khu B ĐHQG TP. HCM, Phường Linh Trung, Thủ Đức",
  "deliveryFee": 15000.0,
  "orderNotes": "Giao trước 12h trưa",
  "orderDate": "2025-12-31T11:30:00"
}
```

#### Request Fields

| Field | Type | Required | Description | Validation |
|-------|------|----------|-------------|------------|
| customerId | UUID | ✅ | ID khách hàng | Must exist |
| restaurantId | UUID | ✅ | ID nhà hàng | Must exist |
| items | Array | ✅ | Danh sách món đặt | Tối thiểu 1 món |
| items[].menuItemId | UUID | ✅ | ID món ăn | Must exist |
| items[].quantity | Integer | ✅ | Số lượng | >= 1 |
| items[].specialInstructions | String | ❌ | Yêu cầu đặc biệt | Max 500 ký tự |
| deliveryAddress | String | ✅ | Địa chỉ giao hàng | Max 500 ký tự |
| deliveryFee | Double | ❌ | Phí giao hàng | >= 0 |
| orderNotes | String | ❌ | Ghi chú đơn hàng | Max 1000 ký tự |
| orderDate | DateTime | ❌ | Thời gian đặt | Mặc định: hiện tại |

#### Response (201 Created)

```json
{
  "id": "f1e2d3c4-5678-9abc-def1-234567890abc",
  "orderNumber": "ORD-20251231-001",
  "customerId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "customerName": "Nguyen Van A",
  "customerPhone": "+84987654321",
  "restaurantId": "8f8e8334-9347-4933-9333-875865538050",
  "restaurantName": "Pizza Paradise",
  "restaurantPhone": "+84901234567",
  "items": [
    {
      "id": "i1i2i3i4-1111-2222-3333-444444444444",
      "menuItemId": "a1b2c3d4-1234-5678-9abc-def123456789",
      "menuItemName": "Margherita Pizza",
      "quantity": 2,
      "unitPrice": 120000.0,
      "subtotal": 240000.0,
      "specialInstructions": "No onions please"
    },
    {
      "id": "i2i3i4i5-2222-3333-4444-555555555555",
      "menuItemId": "b2c3d4e5-2345-6789-abcd-ef1234567890",
      "menuItemName": "Pepperoni Pizza",
      "quantity": 1,
      "unitPrice": 135000.0,
      "subtotal": 135000.0,
      "specialInstructions": ""
    }
  ],
  "subtotal": 375000.0,
  "deliveryFee": 15000.0,
  "tax": 0.0,
  "discount": 0.0,
  "totalAmount": 390000.0,
  "status": "PENDING",
  "deliveryAddress": "KTX Khu B ĐHQG TP. HCM, Phường Linh Trung, Thủ Đức",
  "orderNotes": "Giao trước 12h trưa",
  "estimatedDeliveryTime": 30,
  "orderDate": "2025-12-31T11:30:00",
  "createdAt": "2025-12-31T11:30:15",
  "updatedAt": "2025-12-31T11:30:15"
}
```

#### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Dữ liệu không hợp lệ (món không tồn tại, số lượng < 1, v.v.) |
| 404 | Không tìm thấy khách hàng hoặc nhà hàng |

---

### 2. Lấy danh sách đơn hàng của tôi

Lấy tất cả đơn hàng của khách hàng hiện tại (mới nhất trước).

- **URL**: `GET /api/v1/customers/orders/me`
- **Authentication**: ✅ Required
- **Method**: GET

#### Request Headers

```
Authorization: Bearer <access_token>
```

#### Response (200 OK)

```json
[
  {
    "id": "f1e2d3c4-5678-9abc-def1-234567890abc",
    "orderNumber": "ORD-20251231-001",
    "restaurantName": "Pizza Paradise",
    "totalAmount": 390000.0,
    "status": "DELIVERED",
    "orderDate": "2025-12-31T11:30:00",
    "createdAt": "2025-12-31T11:30:15"
  },
  {
    "id": "e0d1c2b3-4567-89ab-cdef-123456789abc",
    "orderNumber": "ORD-20251230-045",
    "restaurantName": "Pho House",
    "totalAmount": 150000.0,
    "status": "CANCELLED",
    "orderDate": "2025-12-30T19:00:00",
    "createdAt": "2025-12-30T19:00:20"
  }
]
```

---

### 3. Lấy đơn hàng đang hoạt động

Lấy các đơn hàng có trạng thái PENDING, PREPARING, hoặc SHIPPING.

- **URL**: `GET /api/v1/customers/orders/me/active`
- **Authentication**: ✅ Required
- **Method**: GET

#### Request Headers

```
Authorization: Bearer <access_token>
```

#### Response (200 OK)

```json
[
  {
    "id": "f1e2d3c4-5678-9abc-def1-234567890abc",
    "orderNumber": "ORD-20251231-001",
    "restaurantName": "Pizza Paradise",
    "restaurantPhone": "+84901234567",
    "totalAmount": 390000.0,
    "status": "PREPARING",
    "deliveryAddress": "KTX Khu B ĐHQG TP. HCM",
    "estimatedDeliveryTime": 30,
    "orderDate": "2025-12-31T11:30:00",
    "createdAt": "2025-12-31T11:30:15",
    "updatedAt": "2025-12-31T11:35:00"
  }
]
```

---

### 4. Hủy đơn hàng

Hủy đơn hàng (chỉ được hủy khi trạng thái là PENDING hoặc PREPARING).

- **URL**: `PATCH /api/v1/customers/orders/{id}/cancel`
- **Authentication**: ✅ Required
- **Method**: PATCH

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | UUID | ✅ | ID đơn hàng |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| reason | String | ❌ | Lý do hủy đơn |

#### Request Headers

```
Authorization: Bearer <access_token>
```

#### Response (200 OK)

```json
{
  "id": "f1e2d3c4-5678-9abc-def1-234567890abc",
  "orderNumber": "ORD-20251231-001",
  "status": "CANCELLED",
  "cancelReason": "Changed my mind",
  "cancelledAt": "2025-12-31T11:40:00",
  "totalAmount": 390000.0
}
```

#### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Không thể hủy đơn (trạng thái không cho phép) |
| 404 | Không tìm thấy đơn hàng |

#### Example Requests

```bash
# Hủy đơn không có lý do
PATCH /api/v1/customers/orders/f1e2d3c4-5678-9abc-def1-234567890abc/cancel

# Hủy đơn có lý do
PATCH /api/v1/customers/orders/f1e2d3c4-5678-9abc-def1-234567890abc/cancel?reason=Changed%20my%20mind
```

---

## 📊 Order Status Flow

```
PENDING → PREPARING → SHIPPING → DELIVERED
   ↓          ↓
CANCELLED  CANCELLED
```

### Trạng thái đơn hàng

| Status | Description | Có thể hủy? |
|--------|-------------|-------------|
| PENDING | Đơn hàng mới, chờ xác nhận | ✅ Có |
| PREPARING | Nhà hàng đang chuẩn bị | ✅ Có |
| SHIPPING | Đang giao hàng | ❌ Không |
| DELIVERED | Đã giao thành công | ❌ Không |
| CANCELLED | Đã hủy | ❌ Không |

---

## 📊 Response Fields Description

### Order Object (Full)

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | ID đơn hàng |
| orderNumber | String | Mã đơn hàng dạng văn bản |
| customerId | UUID | ID khách hàng |
| customerName | String | Tên khách hàng |
| customerPhone | String | SĐT khách hàng |
| restaurantId | UUID | ID nhà hàng |
| restaurantName | String | Tên nhà hàng |
| restaurantPhone | String | SĐT nhà hàng |
| items | Array | Danh sách món trong đơn |
| subtotal | Double | Tổng tiền món (VND) |
| deliveryFee | Double | Phí giao hàng (VND) |
| tax | Double | Thuế (VND) |
| discount | Double | Giảm giá (VND) |
| totalAmount | Double | Tổng cộng (VND) |
| status | String | Trạng thái đơn hàng |
| deliveryAddress | String | Địa chỉ giao hàng |
| orderNotes | String | Ghi chú |
| cancelReason | String | Lý do hủy (nếu có) |
| estimatedDeliveryTime | Integer | Thời gian giao dự kiến (phút) |
| orderDate | DateTime | Thời gian đặt |
| createdAt | DateTime | Thời gian tạo |
| updatedAt | DateTime | Thời gian cập nhật |
| cancelledAt | DateTime | Thời gian hủy (nếu có) |

### Order Item Object

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | ID order item |
| menuItemId | UUID | ID món ăn |
| menuItemName | String | Tên món ăn |
| quantity | Integer | Số lượng |
| unitPrice | Double | Đơn giá (VND) |
| subtotal | Double | Thành tiền (VND) |
| specialInstructions | String | Yêu cầu đặc biệt |

---

## 📝 Ví dụ sử dụng

### cURL

```bash
# Tạo đơn hàng
curl -X POST http://localhost:8080/api/v1/customers/orders \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "restaurantId": "8f8e8334-9347-4933-9333-875865538050",
    "items": [
      {
        "menuItemId": "a1b2c3d4-1234-5678-9abc-def123456789",
        "quantity": 2,
        "specialInstructions": "No onions"
      }
    ],
    "deliveryAddress": "KTX Khu B ĐHQG TP. HCM",
    "deliveryFee": 15000.0
  }'

# Lấy đơn hàng của tôi
curl -X GET http://localhost:8080/api/v1/customers/orders/me \
  -H "Authorization: Bearer <token>"

# Lấy đơn đang hoạt động
curl -X GET http://localhost:8080/api/v1/customers/orders/me/active \
  -H "Authorization: Bearer <token>"

# Hủy đơn hàng
curl -X PATCH "http://localhost:8080/api/v1/customers/orders/f1e2d3c4-5678-9abc-def1-234567890abc/cancel?reason=Changed%20my%20mind" \
  -H "Authorization: Bearer <token>"
```

### JavaScript

```javascript
// Tạo đơn hàng
const createOrder = async (orderData, token) => {
  const response = await fetch('http://localhost:8080/api/v1/customers/orders', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(orderData),
  });
  return await response.json();
};

// Lấy lịch sử đơn hàng
const getMyOrders = async (token) => {
  const response = await fetch('http://localhost:8080/api/v1/customers/orders/me', {
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  });
  return await response.json();
};

// Lấy đơn đang hoạt động
const getActiveOrders = async (token) => {
  const response = await fetch('http://localhost:8080/api/v1/customers/orders/me/active', {
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  });
  return await response.json();
};

// Hủy đơn hàng
const cancelOrder = async (orderId, reason, token) => {
  const url = reason
    ? `http://localhost:8080/api/v1/customers/orders/${orderId}/cancel?reason=${encodeURIComponent(reason)}`
    : `http://localhost:8080/api/v1/customers/orders/${orderId}/cancel`;

  const response = await fetch(url, {
    method: 'PATCH',
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  });
  return await response.json();
};
```

---

## 💡 Tips

1. **Validation**: Luôn validate số lượng món và địa chỉ trước khi gửi request
2. **Error Handling**: Xử lý lỗi 404 (món không tồn tại) và 400 (validation failed)
3. **Order Tracking**: Sử dụng endpoint `/me/active` để theo dõi đơn hàng real-time
4. **Cancel Policy**: Chỉ hủy đơn khi status là PENDING hoặc PREPARING
5. **Token**: Đảm bảo token còn hiệu lực trước khi gọi API
