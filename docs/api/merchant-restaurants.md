# Merchant Restaurant API

API cho chủ nhà hàng quản lý thông tin nhà hàng của mình.

**Base Path**: `/api/v1/merchant/restaurants`

**Authentication**: ✅ Required (Bearer Token - Role: MERCHANT hoặc ADMIN)

---

## 🔐 Authorization

- **Required Roles**: `MERCHANT`, `ADMIN`
- **Header**: `Authorization: Bearer <access_token>`

---

## 📋 Endpoints

### 1. Lấy danh sách nhà hàng của tôi

Lấy tất cả nhà hàng thuộc sở hữu của merchant hiện tại.

- **URL**: `GET /api/v1/merchant/restaurants/me`
- **Authentication**: ✅ Required (MERCHANT/ADMIN)
- **Method**: GET

#### Request Headers

```
Authorization: Bearer <access_token>
```

#### Response (200 OK)

```json
[
  {
    "id": "8f8e8334-9347-4933-9333-875865538050",
    "name": "Pizza Paradise",
    "address": "123 Nguyen Hue, District 1, HCMC",
    "phoneNumber": "+84901234567",
    "email": "contact@pizzaparadise.com",
    "cuisine": "Italian",
    "isOpen": true,
    "isActive": true,
    "rating": 4.5,
    "totalOrders": 1500,
    "ownerId": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
  }
]
```

---

### 2. Tạo nhà hàng mới

Merchant tạo nhà hàng mới.

- **URL**: `POST /api/v1/merchant/restaurants`
- **Authentication**: ✅ Required (MERCHANT/ADMIN)
- **Content-Type**: `application/json`

#### Request Headers

```
Authorization: Bearer <access_token>
Content-Type: application/json
```

#### Request Body

```json
{
  "name": "Pizza Paradise",
  "address": "123 Nguyen Hue, District 1, HCMC",
  "phoneNumber": "+84901234567",
  "email": "contact@pizzaparadise.com",
  "description": "Best Italian pizza in town with authentic recipes from Naples",
  "cuisine": "Italian",
  "imageUrl": "https://example.com/images/pizza-paradise.jpg",
  "coverImageUrl": "https://example.com/images/pizza-paradise-cover.jpg",
  "openingTime": "09:00",
  "closingTime": "22:00",
  "openingHours": "Mon-Sun: 9:00 AM - 10:00 PM",
  "deliveryFee": 15000.0,
  "minimumOrder": 50000.0,
  "freeDeliveryThreshold": 200000.0,
  "estimatedDeliveryTime": 30,
  "maxDeliveryDistance": 5.0,
  "latitude": 10.7769,
  "longitude": 106.7009,
  "promotionText": "Free delivery for orders over 200k",
  "hasPromotion": true,
  "acceptsCash": true,
  "acceptsCard": true
}
```

#### Request Fields

| Field | Type | Required | Description | Validation |
|-------|------|----------|-------------|------------|
| name | String | ✅ | Tên nhà hàng | 3-200 ký tự, unique |
| address | String | ✅ | Địa chỉ đầy đủ | Max 500 ký tự |
| phoneNumber | String | ✅ | Số điện thoại | Định dạng quốc tế, unique |
| email | String | ✅ | Email liên hệ | Email hợp lệ |
| description | String | ❌ | Mô tả nhà hàng | Max 2000 ký tự |
| cuisine | String | ✅ | Loại món ăn | VD: Italian, Vietnamese |
| imageUrl | String | ❌ | URL ảnh đại diện | Valid URL |
| coverImageUrl | String | ❌ | URL ảnh bìa | Valid URL |
| openingTime | String | ❌ | Giờ mở cửa | HH:mm format |
| closingTime | String | ❌ | Giờ đóng cửa | HH:mm format |
| openingHours | String | ❌ | Chi tiết giờ mở | Max 500 ký tự |
| deliveryFee | Double | ❌ | Phí giao hàng | >= 0 |
| minimumOrder | Double | ❌ | Đơn tối thiểu | >= 0 |
| freeDeliveryThreshold | Double | ❌ | Miễn phí ship từ | >= 0 |
| estimatedDeliveryTime | Integer | ❌ | Thời gian giao (phút) | > 0 |
| maxDeliveryDistance | Double | ❌ | Khoảng cách tối đa (km) | > 0 |
| latitude | Double | ❌ | Vĩ độ | -90 to 90 |
| longitude | Double | ❌ | Kinh độ | -180 to 180 |
| promotionText | String | ❌ | Nội dung khuyến mãi | Max 500 ký tự |
| hasPromotion | Boolean | ❌ | Có khuyến mãi | Default: false |
| acceptsCash | Boolean | ❌ | Nhận tiền mặt | Default: true |
| acceptsCard | Boolean | ❌ | Nhận thẻ | Default: false |

#### Response (201 Created)

```json
{
  "id": "8f8e8334-9347-4933-9333-875865538050",
  "name": "Pizza Paradise",
  "address": "123 Nguyen Hue, District 1, HCMC",
  "phoneNumber": "+84901234567",
  "email": "contact@pizzaparadise.com",
  "description": "Best Italian pizza in town with authentic recipes from Naples",
  "cuisine": "Italian",
  "imageUrl": "https://example.com/images/pizza-paradise.jpg",
  "coverImageUrl": "https://example.com/images/pizza-paradise-cover.jpg",
  "rating": 0.0,
  "totalReviews": 0,
  "isOpen": true,
  "isActive": true,
  "isVerified": false,
  "isFeatured": false,
  "openingTime": "09:00",
  "closingTime": "22:00",
  "openingHours": "Mon-Sun: 9:00 AM - 10:00 PM",
  "deliveryFee": 15000.0,
  "minimumOrder": 50000.0,
  "freeDeliveryThreshold": 200000.0,
  "estimatedDeliveryTime": 30,
  "maxDeliveryDistance": 5.0,
  "latitude": 10.7769,
  "longitude": 106.7009,
  "totalOrders": 0,
  "orderCount": 0,
  "averageOrderValue": 0.0,
  "promotionText": "Free delivery for orders over 200k",
  "hasPromotion": true,
  "acceptsCash": true,
  "acceptsCard": true,
  "ownerId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "createdAt": "2025-12-31T15:56:00",
  "updatedAt": "2025-12-31T15:56:00",
  "menuItemsCount": 0,
  "isCurrentlyOpen": true
}
```

#### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Dữ liệu không hợp lệ hoặc tên/SĐT đã tồn tại |

---

### 3. Cập nhật thông tin nhà hàng

Cập nhật thông tin nhà hàng (chỉ owner hoặc admin).

- **URL**: `PUT /api/v1/merchant/restaurants/{id}`
- **Authentication**: ✅ Required (MERCHANT/ADMIN)
- **Content-Type**: `application/json`

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | UUID | ✅ | ID nhà hàng cần cập nhật |

#### Request Headers

```
Authorization: Bearer <access_token>
Content-Type: application/json
```

#### Request Body

Giống như request body của endpoint tạo nhà hàng.

#### Response (200 OK)

```json
{
  "id": "8f8e8334-9347-4933-9333-875865538050",
  "name": "Pizza Paradise - Updated",
  "description": "Updated description",
  "deliveryFee": 20000.0,
  "updatedAt": "2025-12-31T16:00:00"
}
```

#### Error Responses

| Status | Description |
|--------|-------------|
| 403 | Forbidden - Bạn không phải owner của nhà hàng này |
| 404 | Không tìm thấy nhà hàng |

---

### 4. Bật/Tắt trạng thái mở cửa

Mở hoặc đóng cửa nhà hàng để nhận đơn hàng.

- **URL**: `PATCH /api/v1/merchant/restaurants/{id}/toggle-status`
- **Authentication**: ✅ Required (MERCHANT/ADMIN)
- **Method**: PATCH

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | UUID | ✅ | ID nhà hàng |

#### Request Headers

```
Authorization: Bearer <access_token>
```

#### Response (200 OK)

```json
{
  "id": "8f8e8334-9347-4933-9333-875865538050",
  "name": "Pizza Paradise",
  "isOpen": false,
  "updatedAt": "2025-12-31T16:05:00"
}
```

#### Error Responses

| Status | Description |
|--------|-------------|
| 403 | Forbidden - Bạn không phải owner của nhà hàng này |
| 404 | Không tìm thấy nhà hàng |

---

## 📝 Ví dụ sử dụng

### cURL

```bash
# Lấy nhà hàng của tôi
curl -X GET http://localhost:8080/api/v1/merchant/restaurants/me \
  -H "Authorization: Bearer <token>"

# Tạo nhà hàng mới
curl -X POST http://localhost:8080/api/v1/merchant/restaurants \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Pizza Paradise",
    "address": "123 Nguyen Hue, District 1, HCMC",
    "phoneNumber": "+84901234567",
    "email": "contact@pizzaparadise.com",
    "cuisine": "Italian",
    "deliveryFee": 15000.0,
    "minimumOrder": 50000.0
  }'

# Cập nhật nhà hàng
curl -X PUT http://localhost:8080/api/v1/merchant/restaurants/8f8e8334-9347-4933-9333-875865538050 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Pizza Paradise Updated",
    "deliveryFee": 20000.0
  }'

# Toggle trạng thái mở/đóng
curl -X PATCH http://localhost:8080/api/v1/merchant/restaurants/8f8e8334-9347-4933-9333-875865538050/toggle-status \
  -H "Authorization: Bearer <token>"
```

### JavaScript

```javascript
// Lấy nhà hàng của tôi
const getMyRestaurants = async (token) => {
  const response = await fetch('http://localhost:8080/api/v1/merchant/restaurants/me', {
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  });
  return await response.json();
};

// Tạo nhà hàng mới
const createRestaurant = async (data, token) => {
  const response = await fetch('http://localhost:8080/api/v1/merchant/restaurants', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  });
  return await response.json();
};

// Cập nhật nhà hàng
const updateRestaurant = async (id, data, token) => {
  const response = await fetch(`http://localhost:8080/api/v1/merchant/restaurants/${id}`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  });
  return await response.json();
};

// Toggle trạng thái
const toggleRestaurantStatus = async (id, token) => {
  const response = await fetch(`http://localhost:8080/api/v1/merchant/restaurants/${id}/toggle-status`, {
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

1. **Ownership**: Chỉ owner hoặc admin mới có quyền cập nhật nhà hàng
2. **Validation**: Tên và số điện thoại phải unique trong hệ thống
3. **Toggle Status**: Sử dụng để tạm đóng/mở nhà hàng nhanh chóng
4. **Location**: Cung cấp latitude/longitude để hỗ trợ tính năng delivery radius
