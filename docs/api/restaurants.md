# Restaurant API (Customer)

API cho khách hàng tìm kiếm và xem thông tin nhà hàng.

**Base Path**: `/api/v1/restaurants`

**Authentication**: Bearer Token (optional cho một số endpoint)

---

## 📋 Endpoints

### 1. Tìm kiếm nhà hàng với bộ lọc

Tìm kiếm và lọc nhà hàng theo nhiều tiêu chí với phân trang.

- **URL**: `GET /api/v1/restaurants`
- **Authentication**: Optional
- **Method**: GET

#### Query Parameters

| Parameter | Type | Required | Default | Description | Example |
|-----------|------|----------|---------|-------------|---------|
| keyword | String | ❌ | - | Từ khóa tìm kiếm (tên, mô tả, loại món) | `Pizza` |
| cuisine | String | ❌ | - | Lọc theo loại món ăn | `Italian` |
| minRating | Double | ❌ | - | Rating tối thiểu (1.0 - 5.0) | `4.0` |
| sortBy | String | ❌ | `popular` | Sắp xếp theo: popular, rating, name | `rating` |
| page | Integer | ❌ | `0` | Số trang (bắt đầu từ 0) | `0` |
| size | Integer | ❌ | `20` | Số lượng kết quả mỗi trang | `20` |

#### Response (200 OK)

```json
{
  "content": [
    {
      "id": "8f8e8334-9347-4933-9333-875865538050",
      "name": "Pizza Paradise",
      "address": "123 Nguyen Hue, District 1, HCMC",
      "phoneNumber": "+84901234567",
      "email": "contact@pizzaparadise.com",
      "description": "Best Italian pizza in town",
      "cuisine": "Italian",
      "imageUrl": "https://example.com/images/pizza-paradise.jpg",
      "coverImageUrl": "https://example.com/images/pizza-paradise-cover.jpg",
      "rating": 4.5,
      "totalReviews": 250,
      "isOpen": true,
      "isActive": true,
      "isVerified": true,
      "isFeatured": true,
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
      "totalOrders": 1500,
      "orderCount": 1500,
      "averageOrderValue": 150000.0,
      "promotionText": "Free delivery for orders over 200k",
      "hasPromotion": true,
      "acceptsCash": true,
      "acceptsCard": true,
      "ownerId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
      "createdAt": "2025-01-01T10:00:00",
      "updatedAt": "2025-12-31T15:30:00",
      "menuItemsCount": 45,
      "isCurrentlyOpen": true
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "pageNumber": 0,
    "pageSize": 20,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 5,
  "totalElements": 95,
  "last": false,
  "first": true,
  "size": 20,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "numberOfElements": 20,
  "empty": false
}
```

#### Example Requests

```bash
# Tìm tất cả nhà hàng
GET /api/v1/restaurants?page=0&size=20

# Tìm nhà hàng pizza
GET /api/v1/restaurants?keyword=pizza&page=0&size=20

# Lọc theo món Ý, rating >= 4.0
GET /api/v1/restaurants?cuisine=Italian&minRating=4.0&page=0&size=20

# Tìm kiếm và sắp xếp theo rating
GET /api/v1/restaurants?keyword=burger&sortBy=rating&page=0&size=10
```

---

### 2. Lấy thông tin chi tiết nhà hàng

Lấy thông tin đầy đủ về một nhà hàng cụ thể.

- **URL**: `GET /api/v1/restaurants/{id}`
- **Authentication**: Optional
- **Method**: GET

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | UUID | ✅ | ID của nhà hàng |

#### Response (200 OK)

```json
{
  "id": "8f8e8334-9347-4933-9333-875865538050",
  "name": "Pizza Paradise",
  "address": "123 Nguyen Hue, District 1, HCMC",
  "phoneNumber": "+84901234567",
  "email": "contact@pizzaparadise.com",
  "description": "Best Italian pizza in town with authentic recipes",
  "cuisine": "Italian",
  "imageUrl": "https://example.com/images/pizza-paradise.jpg",
  "coverImageUrl": "https://example.com/images/pizza-paradise-cover.jpg",
  "rating": 4.5,
  "totalReviews": 250,
  "isOpen": true,
  "isActive": true,
  "isVerified": true,
  "isFeatured": true,
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
  "totalOrders": 1500,
  "orderCount": 1500,
  "averageOrderValue": 150000.0,
  "promotionText": "Free delivery for orders over 200k",
  "hasPromotion": true,
  "acceptsCash": true,
  "acceptsCard": true,
  "ownerId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "createdAt": "2025-01-01T10:00:00",
  "updatedAt": "2025-12-31T15:30:00",
  "menuItemsCount": 45,
  "isCurrentlyOpen": true
}
```

#### Error Responses

| Status | Description |
|--------|-------------|
| 404 | Không tìm thấy nhà hàng |

---

### 3. Lấy danh sách nhà hàng phổ biến

Lấy các nhà hàng được đánh giá cao và có nhiều đơn hàng nhất.

- **URL**: `GET /api/v1/restaurants/popular`
- **Authentication**: Optional
- **Method**: GET

#### Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| limit | Integer | ❌ | `10` | Số lượng nhà hàng trả về |

#### Response (200 OK)

```json
[
  {
    "id": "8f8e8334-9347-4933-9333-875865538050",
    "name": "Pizza Paradise",
    "rating": 4.8,
    "totalReviews": 500,
    "totalOrders": 2500,
    "cuisine": "Italian",
    "imageUrl": "https://example.com/images/pizza-paradise.jpg",
    "deliveryFee": 15000.0,
    "estimatedDeliveryTime": 30,
    "isOpen": true,
    "hasPromotion": true,
    "promotionText": "Free delivery for orders over 200k"
  },
  {
    "id": "7a7b7c7d-8347-4933-9333-876865538051",
    "name": "Pho House",
    "rating": 4.7,
    "totalReviews": 450,
    "totalOrders": 2200,
    "cuisine": "Vietnamese",
    "imageUrl": "https://example.com/images/pho-house.jpg",
    "deliveryFee": 10000.0,
    "estimatedDeliveryTime": 25,
    "isOpen": true,
    "hasPromotion": false
  }
]
```

#### Example Request

```bash
GET /api/v1/restaurants/popular?limit=15
```

---

## 📊 Response Fields Description

### Restaurant Object

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | ID duy nhất của nhà hàng |
| name | String | Tên nhà hàng |
| address | String | Địa chỉ đầy đủ |
| phoneNumber | String | Số điện thoại liên hệ |
| email | String | Email liên hệ |
| description | String | Mô tả về nhà hàng |
| cuisine | String | Loại món ăn (Italian, Vietnamese, Chinese,...) |
| imageUrl | String | URL ảnh đại diện |
| coverImageUrl | String | URL ảnh bìa |
| rating | Double | Đánh giá trung bình (1.0 - 5.0) |
| totalReviews | Integer | Tổng số đánh giá |
| isOpen | Boolean | Nhà hàng đang mở cửa |
| isActive | Boolean | Nhà hàng đang hoạt động |
| isVerified | Boolean | Đã được xác minh |
| isFeatured | Boolean | Nhà hàng nổi bật |
| openingTime | String | Giờ mở cửa (HH:mm) |
| closingTime | String | Giờ đóng cửa (HH:mm) |
| openingHours | String | Giờ mở cửa chi tiết |
| deliveryFee | Double | Phí giao hàng (VND) |
| minimumOrder | Double | Đơn hàng tối thiểu (VND) |
| freeDeliveryThreshold | Double | Miễn phí ship từ (VND) |
| estimatedDeliveryTime | Integer | Thời gian giao hàng ước tính (phút) |
| maxDeliveryDistance | Double | Khoảng cách giao hàng tối đa (km) |
| latitude | Double | Vĩ độ |
| longitude | Double | Kinh độ |
| totalOrders | Integer | Tổng số đơn hàng |
| orderCount | Integer | Số lượng đơn hàng |
| averageOrderValue | Double | Giá trị đơn hàng trung bình (VND) |
| promotionText | String | Nội dung khuyến mãi |
| hasPromotion | Boolean | Có khuyến mãi |
| acceptsCash | Boolean | Chấp nhận tiền mặt |
| acceptsCard | Boolean | Chấp nhận thẻ |
| ownerId | UUID | ID chủ nhà hàng |
| createdAt | DateTime | Thời gian tạo |
| updatedAt | DateTime | Thời gian cập nhật |
| menuItemsCount | Integer | Số lượng món ăn |
| isCurrentlyOpen | Boolean | Hiện tại đang mở cửa |

---

## 📝 Ví dụ sử dụng

### cURL

```bash
# Tìm nhà hàng với keyword
curl -X GET "http://localhost:8080/api/v1/restaurants?keyword=pizza&page=0&size=20"

# Lấy thông tin nhà hàng
curl -X GET "http://localhost:8080/api/v1/restaurants/8f8e8334-9347-4933-9333-875865538050"

# Lấy nhà hàng phổ biến
curl -X GET "http://localhost:8080/api/v1/restaurants/popular?limit=10"
```

### JavaScript

```javascript
// Tìm kiếm nhà hàng
const searchRestaurants = async (keyword, cuisine, minRating) => {
  const params = new URLSearchParams({
    keyword: keyword || '',
    cuisine: cuisine || '',
    minRating: minRating || '',
    page: 0,
    size: 20,
    sortBy: 'popular'
  });

  const response = await fetch(`http://localhost:8080/api/v1/restaurants?${params}`);
  return await response.json();
};

// Lấy chi tiết nhà hàng
const getRestaurantDetails = async (restaurantId) => {
  const response = await fetch(`http://localhost:8080/api/v1/restaurants/${restaurantId}`);
  return await response.json();
};

// Lấy nhà hàng phổ biến
const getPopularRestaurants = async (limit = 10) => {
  const response = await fetch(`http://localhost:8080/api/v1/restaurants/popular?limit=${limit}`);
  return await response.json();
};
```

---

## 💡 Tips

1. **Pagination**: Sử dụng phân trang để tránh load quá nhiều dữ liệu
2. **Filtering**: Kết hợp nhiều filter để tìm kiếm chính xác hơn
3. **Sorting**: Sử dụng `sortBy=rating` để hiển thị nhà hàng chất lượng cao nhất
4. **Currently Open**: Kiểm tra `isCurrentlyOpen` để hiển thị nhà hàng đang mở
