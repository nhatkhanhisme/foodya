# Merchant Order API

API cho chủ nhà hàng quản lý đơn hàng.

**Base Path**: `/api/v1/merchant/orders`

**Authentication**: ✅ Required (Bearer Token - Role: MERCHANT hoặc ADMIN)

---

## 🔐 Authorization

- **Required Roles**: `MERCHANT`, `ADMIN`
- **Header**: `Authorization: Bearer <access_token>`

---

## 📋 Endpoints

### 1. Lấy đơn hàng của nhà hàng

Lấy tất cả đơn hàng của một nhà hàng cụ thể.

- **URL**: `GET /api/v1/merchant/orders/restaurant/{restaurantId}`
- **Authentication**: ✅ Required (MERCHANT/ADMIN)
- **Method**: GET

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| restaurantId | UUID | ✅ | ID nhà hàng |

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
    "customerId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "customerName": "Nguyen Van A",
    "customerPhone": "+84987654321",
    "restaurantId": "8f8e8334-9347-4933-9333-875865538050",
    "restaurantName": "Pizza Paradise",
    "items": [
      {
        "id": "i1i2i3i4-1111-2222-3333-444444444444",
        "menuItemName": "Margherita Pizza",
        "quantity": 2,
        "unitPrice": 120000.0,
        "subtotal": 240000.0
      }
    ],
    "subtotal": 240000.0,
    "deliveryFee": 15000.0,
    "totalAmount": 255000.0,
    "status": "PENDING",
    "deliveryAddress": "KTX Khu B ĐHQG TP. HCM",
    "orderNotes": "Giao trước 12h",
    "orderDate": "2025-12-31T11:30:00",
    "createdAt": "2025-12-31T11:30:15",
    "updatedAt": "2025-12-31T11:30:15"
  }
]
```

---

### 2. Lấy chi tiết đơn hàng

Xem thông tin đầy đủ của một đơn hàng.

- **URL**: `GET /api/v1/merchant/orders/{id}`
- **Authentication**: ✅ Required (MERCHANT/ADMIN)
- **Method**: GET

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | UUID | ✅ | ID đơn hàng |

#### Response (200 OK)

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
    }
  ],
  "subtotal": 240000.0,
  "deliveryFee": 15000.0,
  "tax": 0.0,
  "discount": 0.0,
  "totalAmount": 255000.0,
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
| 404 | Không tìm thấy đơn hàng |

---

### 3. Cập nhật trạng thái đơn hàng

Cập nhật trạng thái đơn hàng theo flow: PENDING → PREPARING → SHIPPING → DELIVERED

- **URL**: `PATCH /api/v1/merchant/orders/{id}/status`
- **Authentication**: ✅ Required (MERCHANT/ADMIN)
- **Method**: PATCH

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | UUID | ✅ | ID đơn hàng |

#### Query Parameters

| Parameter | Type | Required | Description | Allowed Values |
|-----------|------|----------|-------------|----------------|
| status | String | ✅ | Trạng thái mới | PENDING, PREPARING, SHIPPING, DELIVERED, CANCELLED |

#### Request Headers

```
Authorization: Bearer <access_token>
```

#### Response (200 OK)

```json
{
  "id": "f1e2d3c4-5678-9abc-def1-234567890abc",
  "orderNumber": "ORD-20251231-001",
  "status": "PREPARING",
  "updatedAt": "2025-12-31T11:35:00"
}
```

#### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Trạng thái không hợp lệ hoặc không thể chuyển |
| 404 | Không tìm thấy đơn hàng |

#### Example Requests

```bash
# Chuyển sang PREPARING
PATCH /api/v1/merchant/orders/f1e2d3c4-5678-9abc-def1-234567890abc/status?status=PREPARING

# Chuyển sang SHIPPING
PATCH /api/v1/merchant/orders/f1e2d3c4-5678-9abc-def1-234567890abc/status?status=SHIPPING

# Chuyển sang DELIVERED
PATCH /api/v1/merchant/orders/f1e2d3c4-5678-9abc-def1-234567890abc/status?status=DELIVERED
```

---

## 📊 Order Status Flow

```
PENDING → PREPARING → SHIPPING → DELIVERED
   ↓          ↓           ↓
CANCELLED  CANCELLED  CANCELLED (nếu cần)
```

### Chi tiết trạng thái

| Status | Description | Actions |
|--------|-------------|---------|
| **PENDING** | Đơn hàng mới, chờ xác nhận | ✅ Chấp nhận: → PREPARING<br>❌ Từ chối: → CANCELLED |
| **PREPARING** | Đang chuẩn bị món | ✅ Hoàn thành: → SHIPPING<br>❌ Hủy (nếu cần): → CANCELLED |
| **SHIPPING** | Đang giao hàng | ✅ Giao thành công: → DELIVERED<br>❌ Giao thất bại: → CANCELLED |
| **DELIVERED** | Đã giao thành công | ✅ Hoàn tất (không thể thay đổi) |
| **CANCELLED** | Đã hủy | ❌ Đã hủy (không thể thay đổi) |

---

## 📝 Ví dụ sử dụng

### cURL

```bash
# Lấy đơn hàng của nhà hàng
curl -X GET http://localhost:8080/api/v1/merchant/orders/restaurant/8f8e8334-9347-4933-9333-875865538050 \
  -H "Authorization: Bearer <token>"

# Lấy chi tiết đơn hàng
curl -X GET http://localhost:8080/api/v1/merchant/orders/f1e2d3c4-5678-9abc-def1-234567890abc \
  -H "Authorization: Bearer <token>"

# Cập nhật trạng thái đơn hàng
curl -X PATCH "http://localhost:8080/api/v1/merchant/orders/f1e2d3c4-5678-9abc-def1-234567890abc/status?status=PREPARING" \
  -H "Authorization: Bearer <token>"
```

### JavaScript

```javascript
// Lấy đơn hàng của nhà hàng
const getRestaurantOrders = async (restaurantId, token) => {
  const response = await fetch(
    `http://localhost:8080/api/v1/merchant/orders/restaurant/${restaurantId}`,
    {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    }
  );
  return await response.json();
};

// Lấy chi tiết đơn hàng
const getOrderDetails = async (orderId, token) => {
  const response = await fetch(
    `http://localhost:8080/api/v1/merchant/orders/${orderId}`,
    {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    }
  );
  return await response.json();
};

// Cập nhật trạng thái đơn hàng
const updateOrderStatus = async (orderId, status, token) => {
  const response = await fetch(
    `http://localhost:8080/api/v1/merchant/orders/${orderId}/status?status=${status}`,
    {
      method: 'PATCH',
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    }
  );
  return await response.json();
};

// Workflow xử lý đơn hàng
const processOrder = async (orderId, token) => {
  // 1. Chấp nhận đơn
  await updateOrderStatus(orderId, 'PREPARING', token);

  // 2. Sau khi chuẩn bị xong
  await updateOrderStatus(orderId, 'SHIPPING', token);

  // 3. Sau khi giao thành công
  await updateOrderStatus(orderId, 'DELIVERED', token);
};
```

---

## 🔔 Best Practices

### 1. Xử lý đơn hàng mới (PENDING)

```javascript
// Kiểm tra đơn hàng mới định kỳ
const checkNewOrders = async (restaurantId, token) => {
  const orders = await getRestaurantOrders(restaurantId, token);
  const pendingOrders = orders.filter(order => order.status === 'PENDING');

  // Hiển thị thông báo cho merchant
  if (pendingOrders.length > 0) {
    console.log(`Bạn có ${pendingOrders.length} đơn hàng mới!`);
  }
};

// Chạy mỗi 30 giây
setInterval(() => checkNewOrders(restaurantId, token), 30000);
```

### 2. Auto-update status

```javascript
// Tự động chuyển status sau khoảng thời gian
const autoUpdateStatus = async (orderId, token) => {
  // Chuyển sang PREPARING
  await updateOrderStatus(orderId, 'PREPARING', token);

  // Sau 15 phút → SHIPPING
  setTimeout(async () => {
    await updateOrderStatus(orderId, 'SHIPPING', token);
  }, 15 * 60 * 1000);

  // Sau 30 phút nữa → DELIVERED
  setTimeout(async () => {
    await updateOrderStatus(orderId, 'DELIVERED', token);
  }, 45 * 60 * 1000);
};
```

### 3. Filter đơn hàng theo status

```javascript
const filterOrdersByStatus = (orders, status) => {
  return orders.filter(order => order.status === status);
};

// Lấy đơn đang chuẩn bị
const preparingOrders = filterOrdersByStatus(orders, 'PREPARING');

// Lấy đơn đang giao
const shippingOrders = filterOrdersByStatus(orders, 'SHIPPING');
```

---

## 💡 Tips

1. **Real-time Updates**: Polling hoặc WebSocket để cập nhật đơn hàng real-time
2. **Status Validation**: API tự động validate status flow, không cho phép skip steps
3. **Notification**: Gửi thông báo cho khách hàng khi status thay đổi
4. **History**: Lưu lại lịch sử thay đổi status để tracking
5. **Performance**: Cache danh sách đơn hàng, chỉ refresh khi có thay đổi

---

## ⚠️ Important Notes

- Merchant chỉ có thể xem/cập nhật đơn hàng của nhà hàng mình sở hữu
- Admin có thể xem/cập nhật tất cả đơn hàng
- Không thể chuyển ngược lại status (VD: SHIPPING → PREPARING)
- Đơn hàng DELIVERED hoặc CANCELLED không thể thay đổi status
