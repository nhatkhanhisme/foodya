# Merchant Menu Items API

API cho chủ nhà hàng quản lý menu và món ăn.

**Base Path**: `/api/v1/merchant/restaurants/{restaurantId}/menu-items`

**Authentication**: ✅ Required (Bearer Token - Role: MERCHANT hoặc ADMIN)

---

## 🔐 Authorization

- **Required Roles**: `MERCHANT`, `ADMIN`
- **Ownership**: MERCHANT chỉ được quản lý menu của nhà hàng mình sở hữu
- **Header**: `Authorization: Bearer <access_token>`

---

## 📋 Endpoints

### 1. Lấy tất cả món ăn (bao gồm cả inactive)

Lấy toàn bộ món ăn của nhà hàng, kể cả món đã ẩn.

- **URL**: `GET /api/v1/merchant/restaurants/{restaurantId}/menu-items`
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
    "id": "a1b2c3d4-1234-5678-9abc-def123456789",
    "restaurantId": "8f8e8334-9347-4933-9333-875865538050",
    "name": "Margherita Pizza",
    "description": "Classic Italian pizza",
    "price": 120000.0,
    "category": "Main Course",
    "imageUrl": "https://example.com/images/margherita.jpg",
    "isAvailable": true,
    "isActive": true,
    "orderCount": 250
  },
  {
    "id": "b2c3d4e5-2345-6789-abcd-ef1234567890",
    "name": "Old Item (Hidden)",
    "price": 100000.0,
    "isActive": false
  }
]
```

#### Error Responses

| Status | Description |
|--------|-------------|
| 403 | Forbidden - Bạn không sở hữu nhà hàng này |
| 404 | Không tìm thấy nhà hàng |

---

### 2. Lấy món ăn đang hoạt động

Chỉ lấy các món đang active.

- **URL**: `GET /api/v1/merchant/restaurants/{restaurantId}/menu-items/active`
- **Authentication**: ✅ Required (MERCHANT/ADMIN)
- **Method**: GET

#### Response (200 OK)

```json
[
  {
    "id": "a1b2c3d4-1234-5678-9abc-def123456789",
    "name": "Margherita Pizza",
    "price": 120000.0,
    "isAvailable": true,
    "isActive": true
  }
]
```

---

### 3. Tạo món ăn mới

Thêm món ăn mới vào menu.

- **URL**: `POST /api/v1/merchant/restaurants/{restaurantId}/menu-items`
- **Authentication**: ✅ Required (MERCHANT/ADMIN)
- **Content-Type**: `application/json`

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| restaurantId | UUID | ✅ | ID nhà hàng |

#### Request Headers

```
Authorization: Bearer <access_token>
Content-Type: application/json
```

#### Request Body

```json
{
  "name": "Margherita Pizza",
  "description": "Classic Italian pizza with tomato sauce, mozzarella cheese, and fresh basil",
  "price": 120000.0,
  "category": "Main Course",
  "imageUrl": "https://example.com/images/margherita.jpg",
  "isAvailable": true,
  "isVegetarian": true,
  "isVegan": false,
  "isGlutenFree": false,
  "isSpicy": false,
  "spicyLevel": 0,
  "calories": 850,
  "preparationTime": 15,
  "discountPercent": 0.0
}
```

#### Request Fields

| Field | Type | Required | Description | Validation |
|-------|------|----------|-------------|------------|
| name | String | ✅ | Tên món ăn | 2-200 ký tự, unique trong nhà hàng |
| description | String | ❌ | Mô tả món | Max 2000 ký tự |
| price | Double | ✅ | Giá món | > 0 |
| category | String | ✅ | Danh mục | VD: Appetizer, Main Course, Dessert |
| imageUrl | String | ❌ | URL hình ảnh | Valid URL |
| isAvailable | Boolean | ❌ | Còn hàng | Default: true |
| isVegetarian | Boolean | ❌ | Món chay | Default: false |
| isVegan | Boolean | ❌ | Món thuần chay | Default: false |
| isGlutenFree | Boolean | ❌ | Không gluten | Default: false |
| isSpicy | Boolean | ❌ | Món cay | Default: false |
| spicyLevel | Integer | ❌ | Độ cay (0-5) | 0-5 |
| calories | Integer | ❌ | Lượng calo | >= 0 |
| preparationTime | Integer | ❌ | Thời gian chuẩn bị (phút) | > 0 |
| discountPercent | Double | ❌ | % giảm giá | 0-100 |

#### Response (201 Created)

```json
{
  "id": "a1b2c3d4-1234-5678-9abc-def123456789",
  "restaurantId": "8f8e8334-9347-4933-9333-875865538050",
  "name": "Margherita Pizza",
  "description": "Classic Italian pizza with tomato sauce, mozzarella cheese, and fresh basil",
  "price": 120000.0,
  "category": "Main Course",
  "imageUrl": "https://example.com/images/margherita.jpg",
  "isAvailable": true,
  "isActive": true,
  "isVegetarian": true,
  "isVegan": false,
  "isGlutenFree": false,
  "isSpicy": false,
  "spicyLevel": 0,
  "calories": 850,
  "preparationTime": 15,
  "orderCount": 0,
  "rating": 0.0,
  "reviewCount": 0,
  "discountPercent": 0.0,
  "finalPrice": 120000.0,
  "createdAt": "2025-12-31T16:00:00",
  "updatedAt": "2025-12-31T16:00:00"
}
```

#### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Dữ liệu không hợp lệ hoặc tên món đã tồn tại |
| 403 | Forbidden - Bạn không sở hữu nhà hàng này |
| 404 | Không tìm thấy nhà hàng |

---

### 4. Cập nhật món ăn

Cập nhật thông tin món ăn.

- **URL**: `PUT /api/v1/merchant/restaurants/{restaurantId}/menu-items/{menuItemId}`
- **Authentication**: ✅ Required (MERCHANT/ADMIN)
- **Content-Type**: `application/json`

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| restaurantId | UUID | ✅ | ID nhà hàng |
| menuItemId | UUID | ✅ | ID món ăn |

#### Request Body

Giống như request body của endpoint tạo món ăn.

#### Response (200 OK)

```json
{
  "id": "a1b2c3d4-1234-5678-9abc-def123456789",
  "name": "Margherita Pizza - Updated",
  "price": 135000.0,
  "updatedAt": "2025-12-31T16:10:00"
}
```

#### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Dữ liệu không hợp lệ |
| 403 | Forbidden - Bạn không sở hữu nhà hàng này |
| 404 | Không tìm thấy món ăn |

---

### 5. Xóa món ăn (Soft Delete)

Ẩn món ăn khỏi menu (không xóa vĩnh viễn).

- **URL**: `DELETE /api/v1/merchant/restaurants/{restaurantId}/menu-items/{menuItemId}`
- **Authentication**: ✅ Required (MERCHANT/ADMIN)
- **Method**: DELETE

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| restaurantId | UUID | ✅ | ID nhà hàng |
| menuItemId | UUID | ✅ | ID món ăn cần xóa |

#### Response (204 No Content)

Không có body response.

#### Error Responses

| Status | Description |
|--------|-------------|
| 403 | Forbidden - Bạn không sở hữu nhà hàng này |
| 404 | Không tìm thấy món ăn |

---

### 6. Bật/Tắt trạng thái món ăn

Đánh dấu món còn/hết hàng.

- **URL**: `PATCH /api/v1/merchant/restaurants/{restaurantId}/menu-items/{menuItemId}/toggle-availability`
- **Authentication**: ✅ Required (MERCHANT/ADMIN)
- **Method**: PATCH

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| restaurantId | UUID | ✅ | ID nhà hàng |
| menuItemId | UUID | ✅ | ID món ăn |

#### Response (200 OK)

```json
{
  "id": "a1b2c3d4-1234-5678-9abc-def123456789",
  "name": "Margherita Pizza",
  "isAvailable": false,
  "updatedAt": "2025-12-31T16:15:00"
}
```

#### Error Responses

| Status | Description |
|--------|-------------|
| 403 | Forbidden - Bạn không sở hữu nhà hàng này |
| 404 | Không tìm thấy món ăn |

---

## 📝 Ví dụ sử dụng

### cURL

```bash
# Lấy tất cả món (bao gồm inactive)
curl -X GET http://localhost:8080/api/v1/merchant/restaurants/8f8e8334-9347-4933-9333-875865538050/menu-items \
  -H "Authorization: Bearer <token>"

# Lấy món đang hoạt động
curl -X GET http://localhost:8080/api/v1/merchant/restaurants/8f8e8334-9347-4933-9333-875865538050/menu-items/active \
  -H "Authorization: Bearer <token>"

# Tạo món mới
curl -X POST http://localhost:8080/api/v1/merchant/restaurants/8f8e8334-9347-4933-9333-875865538050/menu-items \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Margherita Pizza",
    "description": "Classic Italian pizza",
    "price": 120000.0,
    "category": "Main Course",
    "isVegetarian": true
  }'

# Cập nhật món
curl -X PUT http://localhost:8080/api/v1/merchant/restaurants/8f8e8334-9347-4933-9333-875865538050/menu-items/a1b2c3d4-1234-5678-9abc-def123456789 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Margherita Pizza Updated",
    "price": 135000.0
  }'

# Xóa món (soft delete)
curl -X DELETE http://localhost:8080/api/v1/merchant/restaurants/8f8e8334-9347-4933-9333-875865538050/menu-items/a1b2c3d4-1234-5678-9abc-def123456789 \
  -H "Authorization: Bearer <token>"

# Toggle availability
curl -X PATCH http://localhost:8080/api/v1/merchant/restaurants/8f8e8334-9347-4933-9333-875865538050/menu-items/a1b2c3d4-1234-5678-9abc-def123456789/toggle-availability \
  -H "Authorization: Bearer <token>"
```

### JavaScript

```javascript
// Lấy tất cả món
const getAllMenuItems = async (restaurantId, token) => {
  const response = await fetch(
    `http://localhost:8080/api/v1/merchant/restaurants/${restaurantId}/menu-items`,
    {
      headers: { 'Authorization': `Bearer ${token}` },
    }
  );
  return await response.json();
};

// Tạo món mới
const createMenuItem = async (restaurantId, data, token) => {
  const response = await fetch(
    `http://localhost:8080/api/v1/merchant/restaurants/${restaurantId}/menu-items`,
    {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    }
  );
  return await response.json();
};

// Cập nhật món
const updateMenuItem = async (restaurantId, menuItemId, data, token) => {
  const response = await fetch(
    `http://localhost:8080/api/v1/merchant/restaurants/${restaurantId}/menu-items/${menuItemId}`,
    {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    }
  );
  return await response.json();
};

// Xóa món
const deleteMenuItem = async (restaurantId, menuItemId, token) => {
  const response = await fetch(
    `http://localhost:8080/api/v1/merchant/restaurants/${restaurantId}/menu-items/${menuItemId}`,
    {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${token}` },
    }
  );
  return response.status === 204;
};

// Toggle availability
const toggleAvailability = async (restaurantId, menuItemId, token) => {
  const response = await fetch(
    `http://localhost:8080/api/v1/merchant/restaurants/${restaurantId}/menu-items/${menuItemId}/toggle-availability`,
    {
      method: 'PATCH',
      headers: { 'Authorization': `Bearer ${token}` },
    }
  );
  return await response.json();
};
```

---

## 💡 Tips

1. **Ownership Check**: API tự động kiểm tra quyền sở hữu nhà hàng
2. **Soft Delete**: Món bị xóa vẫn tồn tại trong DB, chỉ set `isActive = false`
3. **Toggle Availability**: Sử dụng khi món tạm hết hàng thay vì xóa
4. **Unique Name**: Tên món phải unique trong cùng một nhà hàng
5. **Categories**: Nên sử dụng categories chuẩn để dễ filter
