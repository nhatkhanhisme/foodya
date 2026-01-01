# Menu Items API (Customer)

API cho khách hàng xem menu và món ăn của nhà hàng.

**Base Path**: `/api/v1/restaurants/{restaurantId}/menu-items`

**Authentication**: Optional

---

## 📋 Endpoints

### 1. Lấy danh sách món ăn với phân trang

Lấy danh sách món ăn của nhà hàng với phân trang và sắp xếp.

- **URL**: `GET /api/v1/restaurants/{restaurantId}/menu-items`
- **Authentication**: Optional
- **Method**: GET

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| restaurantId | UUID | ✅ | ID của nhà hàng |

#### Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| page | Integer | ❌ | `0` | Số trang (bắt đầu từ 0) |
| size | Integer | ❌ | `20` | Số lượng món mỗi trang |
| sortBy | String | ❌ | `name` | Sắp xếp theo: name, price, orderCount |
| sortDirection | String | ❌ | `asc` | Chiều sắp xếp: asc, desc |

#### Response (200 OK)

```json
{
  "content": [
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
      "orderCount": 250,
      "rating": 4.7,
      "reviewCount": 85,
      "discountPercent": 0.0,
      "finalPrice": 120000.0,
      "createdAt": "2025-01-15T10:00:00",
      "updatedAt": "2025-12-30T14:20:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 3,
  "totalElements": 45,
  "last": false,
  "first": true,
  "size": 20,
  "number": 0,
  "numberOfElements": 20,
  "empty": false
}
```

---

### 2. Lấy tất cả món ăn (không phân trang)

Lấy toàn bộ món ăn đang hoạt động của nhà hàng.

- **URL**: `GET /api/v1/restaurants/{restaurantId}/menu-items/all`
- **Authentication**: Optional
- **Method**: GET

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
    "isVegetarian": true
  }
]
```

---

### 3. Lấy chi tiết món ăn

Lấy thông tin đầy đủ về một món ăn cụ thể.

- **URL**: `GET /api/v1/restaurants/{restaurantId}/menu-items/{menuItemId}`
- **Authentication**: Optional
- **Method**: GET

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| restaurantId | UUID | ✅ | ID của nhà hàng |
| menuItemId | UUID | ✅ | ID của món ăn |

#### Response (200 OK)

```json
{
  "id": "a1b2c3d4-1234-5678-9abc-def123456789",
  "restaurantId": "8f8e8334-9347-4933-9333-875865538050",
  "name": "Margherita Pizza",
  "description": "Classic Italian pizza with tomato sauce, mozzarella cheese, and fresh basil leaves",
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
  "orderCount": 250,
  "rating": 4.7,
  "reviewCount": 85,
  "discountPercent": 0.0,
  "finalPrice": 120000.0,
  "createdAt": "2025-01-15T10:00:00",
  "updatedAt": "2025-12-30T14:20:00"
}
```

#### Error Responses

| Status | Description |
|--------|-------------|
| 404 | Không tìm thấy món ăn |

---

### 4. Tìm kiếm món ăn

Tìm kiếm món ăn theo từ khóa (tên, mô tả).

- **URL**: `GET /api/v1/restaurants/{restaurantId}/menu-items/search`
- **Authentication**: Optional
- **Method**: GET

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| restaurantId | UUID | ✅ | ID của nhà hàng |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| keyword | String | ✅ | Từ khóa tìm kiếm |

#### Response (200 OK)

```json
[
  {
    "id": "a1b2c3d4-1234-5678-9abc-def123456789",
    "name": "Margherita Pizza",
    "description": "Classic Italian pizza",
    "price": 120000.0,
    "category": "Main Course",
    "imageUrl": "https://example.com/images/margherita.jpg",
    "isAvailable": true
  },
  {
    "id": "b2c3d4e5-2345-6789-abcd-ef1234567890",
    "name": "Pepperoni Pizza",
    "description": "Spicy pepperoni pizza",
    "price": 135000.0,
    "category": "Main Course",
    "imageUrl": "https://example.com/images/pepperoni.jpg",
    "isAvailable": true
  }
]
```

#### Example Request

```bash
GET /api/v1/restaurants/{restaurantId}/menu-items/search?keyword=pizza
```

---

### 5. Lọc món ăn theo danh mục

Lấy món ăn theo danh mục cụ thể.

- **URL**: `GET /api/v1/restaurants/{restaurantId}/menu-items/category/{category}`
- **Authentication**: Optional
- **Method**: GET

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| restaurantId | UUID | ✅ | ID của nhà hàng |
| category | String | ✅ | Tên danh mục |

#### Response (200 OK)

```json
[
  {
    "id": "a1b2c3d4-1234-5678-9abc-def123456789",
    "name": "Margherita Pizza",
    "category": "Main Course",
    "price": 120000.0,
    "imageUrl": "https://example.com/images/margherita.jpg",
    "isAvailable": true
  }
]
```

#### Example Request

```bash
GET /api/v1/restaurants/{restaurantId}/menu-items/category/Main%20Course
```

---

### 6. Lấy món ăn phổ biến

Lấy các món ăn được đặt nhiều nhất của nhà hàng.

- **URL**: `GET /api/v1/restaurants/{restaurantId}/menu-items/popular`
- **Authentication**: Optional
- **Method**: GET

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| restaurantId | UUID | ✅ | ID của nhà hàng |

#### Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| limit | Integer | ❌ | `10` | Số lượng món trả về |

#### Response (200 OK)

```json
[
  {
    "id": "a1b2c3d4-1234-5678-9abc-def123456789",
    "name": "Margherita Pizza",
    "price": 120000.0,
    "imageUrl": "https://example.com/images/margherita.jpg",
    "orderCount": 250,
    "rating": 4.7,
    "isAvailable": true
  }
]
```

---

### 7. Lọc món ăn theo sở thích ăn uống

Lọc món ăn theo các tiêu chí: chay, thuần chay, không gluten.

- **URL**: `GET /api/v1/restaurants/{restaurantId}/menu-items/dietary`
- **Authentication**: Optional
- **Method**: GET

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| restaurantId | UUID | ✅ | ID của nhà hàng |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| vegetarian | Boolean | ❌ | Chỉ món chay |
| vegan | Boolean | ❌ | Chỉ món thuần chay |
| glutenFree | Boolean | ❌ | Chỉ món không chứa gluten |

#### Response (200 OK)

```json
[
  {
    "id": "a1b2c3d4-1234-5678-9abc-def123456789",
    "name": "Veggie Delight",
    "description": "Healthy vegetarian dish",
    "price": 95000.0,
    "isVegetarian": true,
    "isVegan": true,
    "isGlutenFree": false,
    "imageUrl": "https://example.com/images/veggie.jpg",
    "isAvailable": true
  }
]
```

#### Example Requests

```bash
# Lấy món chay
GET /api/v1/restaurants/{restaurantId}/menu-items/dietary?vegetarian=true

# Lấy món thuần chay không gluten
GET /api/v1/restaurants/{restaurantId}/menu-items/dietary?vegan=true&glutenFree=true
```

---

## 📊 Response Fields Description

### Menu Item Object

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | ID duy nhất của món ăn |
| restaurantId | UUID | ID nhà hàng |
| name | String | Tên món ăn |
| description | String | Mô tả chi tiết |
| price | Double | Giá gốc (VND) |
| category | String | Danh mục món ăn |
| imageUrl | String | URL hình ảnh |
| isAvailable | Boolean | Còn hàng |
| isActive | Boolean | Đang hoạt động |
| isVegetarian | Boolean | Món chay |
| isVegan | Boolean | Món thuần chay |
| isGlutenFree | Boolean | Không chứa gluten |
| isSpicy | Boolean | Món cay |
| spicyLevel | Integer | Độ cay (0-5) |
| calories | Integer | Lượng calo |
| preparationTime | Integer | Thời gian chuẩn bị (phút) |
| orderCount | Integer | Số lần được đặt |
| rating | Double | Đánh giá trung bình |
| reviewCount | Integer | Số lượng đánh giá |
| discountPercent | Double | Phần trăm giảm giá |
| finalPrice | Double | Giá sau giảm (VND) |
| createdAt | DateTime | Thời gian tạo |
| updatedAt | DateTime | Thời gian cập nhật |

---

## 📝 Ví dụ sử dụng

### cURL

```bash
# Lấy menu với phân trang
curl -X GET "http://localhost:8080/api/v1/restaurants/8f8e8334-9347-4933-9333-875865538050/menu-items?page=0&size=20&sortBy=price&sortDirection=asc"

# Tìm kiếm món ăn
curl -X GET "http://localhost:8080/api/v1/restaurants/8f8e8334-9347-4933-9333-875865538050/menu-items/search?keyword=pizza"

# Lấy món phổ biến
curl -X GET "http://localhost:8080/api/v1/restaurants/8f8e8334-9347-4933-9333-875865538050/menu-items/popular?limit=5"

# Lọc món chay
curl -X GET "http://localhost:8080/api/v1/restaurants/8f8e8334-9347-4933-9333-875865538050/menu-items/dietary?vegetarian=true"
```

### JavaScript

```javascript
// Lấy menu với phân trang
const getMenuItems = async (restaurantId, page = 0, size = 20) => {
  const response = await fetch(
    `http://localhost:8080/api/v1/restaurants/${restaurantId}/menu-items?page=${page}&size=${size}`
  );
  return await response.json();
};

// Tìm kiếm món ăn
const searchMenuItems = async (restaurantId, keyword) => {
  const response = await fetch(
    `http://localhost:8080/api/v1/restaurants/${restaurantId}/menu-items/search?keyword=${encodeURIComponent(keyword)}`
  );
  return await response.json();
};

// Lấy món phổ biến
const getPopularItems = async (restaurantId, limit = 10) => {
  const response = await fetch(
    `http://localhost:8080/api/v1/restaurants/${restaurantId}/menu-items/popular?limit=${limit}`
  );
  return await response.json();
};

// Lọc theo sở thích ăn uống
const filterByDietary = async (restaurantId, filters) => {
  const params = new URLSearchParams();
  if (filters.vegetarian) params.append('vegetarian', 'true');
  if (filters.vegan) params.append('vegan', 'true');
  if (filters.glutenFree) params.append('glutenFree', 'true');

  const response = await fetch(
    `http://localhost:8080/api/v1/restaurants/${restaurantId}/menu-items/dietary?${params}`
  );
  return await response.json();
};
```

---

## 💡 Tips

1. **Pagination**: Sử dụng phân trang khi hiển thị menu lớn
2. **Sorting**: Sắp xếp theo `price` để hiển thị món từ rẻ đến đắt hoặc ngược lại
3. **Popular Items**: Hiển thị món phổ biến ở đầu trang để tăng conversion
4. **Dietary Filters**: Hữu ích cho khách hàng có nhu cầu ăn uống đặc biệt
5. **Availability**: Luôn kiểm tra `isAvailable` trước khi cho phép đặt món
