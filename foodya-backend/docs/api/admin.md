# Admin API

API cho quản trị viên hệ thống.

**Base Path**: `/api/v1/admin`

**Authentication**: ✅ Required (Bearer Token - Role: ADMIN only)

---

## 🔐 Authorization

- **Required Role**: `ADMIN` only
- **Header**: `Authorization: Bearer <access_token>`
- **Warning**: ⚠️ API này chỉ dành cho admin, không dùng cho mobile app

---

## 📋 Restaurant Management

### 1. Lấy tất cả nhà hàng (bao gồm inactive)

Lấy toàn bộ nhà hàng trong hệ thống, kể cả nhà hàng đã bị vô hiệu hóa.

- **URL**: `GET /api/v1/admin/restaurants`
- **Authentication**: ✅ Required (ADMIN only)
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
    "isVerified": true,
    "rating": 4.5,
    "totalOrders": 1500,
    "ownerId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "createdAt": "2025-01-01T10:00:00",
    "updatedAt": "2025-12-31T15:00:00"
  },
  {
    "id": "7a7b7c7d-8347-4933-9333-876865538051",
    "name": "Old Restaurant (Inactive)",
    "isActive": false,
    "createdAt": "2024-01-01T10:00:00"
  }
]
```

---

### 2. Xóa nhà hàng vĩnh viễn

Xóa hoàn toàn nhà hàng khỏi hệ thống (hard delete).

- **URL**: `DELETE /api/v1/admin/restaurants/{id}`
- **Authentication**: ✅ Required (ADMIN only)
- **Method**: DELETE

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | UUID | ✅ | ID nhà hàng cần xóa |

#### Request Headers

```
Authorization: Bearer <access_token>
```

#### Response (204 No Content)

Không có body response.

#### Error Responses

| Status | Description |
|--------|-------------|
| 404 | Không tìm thấy nhà hàng |
| 500 | Lỗi khi xóa (có thể do ràng buộc foreign key) |

---

## 📋 User Management (Coming Soon)

### Các API sẽ được thêm:

- `GET /api/v1/admin/users` - Lấy danh sách tất cả users
- `GET /api/v1/admin/users/{id}` - Lấy chi tiết user
- `PATCH /api/v1/admin/users/{id}/deactivate` - Vô hiệu hóa user
- `PATCH /api/v1/admin/users/{id}/activate` - Kích hoạt user
- `DELETE /api/v1/admin/users/{id}` - Xóa user vĩnh viễn

---

## 📋 Menu Items Management (Coming Soon)

### Các API sẽ được thêm:

- `GET /api/v1/admin/menu-items` - Lấy tất cả menu items
- `DELETE /api/v1/admin/menu-items/{id}` - Xóa menu item vĩnh viễn

---

## 📋 Order Management (Coming Soon)

### Các API sẽ được thêm:

- `GET /api/v1/admin/orders` - Lấy tất cả đơn hàng
- `GET /api/v1/admin/orders/stats` - Thống kê đơn hàng
- `PATCH /api/v1/admin/orders/{id}/status` - Cập nhật status đơn hàng
- `DELETE /api/v1/admin/orders/{id}` - Xóa đơn hàng

---

## 📊 Statistics & Reports (Coming Soon)

### Các API sẽ được thêm:

- `GET /api/v1/admin/stats/overview` - Tổng quan hệ thống
- `GET /api/v1/admin/stats/revenue` - Thống kê doanh thu
- `GET /api/v1/admin/stats/restaurants` - Thống kê nhà hàng
- `GET /api/v1/admin/stats/users` - Thống kê người dùng
- `GET /api/v1/admin/reports/daily` - Báo cáo hàng ngày
- `GET /api/v1/admin/reports/monthly` - Báo cáo hàng tháng

---

## 📝 Ví dụ sử dụng

### cURL

```bash
# Lấy tất cả nhà hàng
curl -X GET http://localhost:8080/api/v1/admin/restaurants \
  -H "Authorization: Bearer <admin_token>"

# Xóa nhà hàng
curl -X DELETE http://localhost:8080/api/v1/admin/restaurants/8f8e8334-9347-4933-9333-875865538050 \
  -H "Authorization: Bearer <admin_token>"
```

### JavaScript

```javascript
// Lấy tất cả nhà hàng
const getAllRestaurants = async (adminToken) => {
  const response = await fetch('http://localhost:8080/api/v1/admin/restaurants', {
    headers: {
      'Authorization': `Bearer ${adminToken}`,
    },
  });
  return await response.json();
};

// Xóa nhà hàng
const deleteRestaurant = async (restaurantId, adminToken) => {
  const response = await fetch(
    `http://localhost:8080/api/v1/admin/restaurants/${restaurantId}`,
    {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${adminToken}`,
      },
    }
  );
  return response.status === 204;
};
```

---

## 🎯 Admin Dashboard Example

```javascript
// Dashboard component
class AdminDashboard {
  constructor(token) {
    this.token = token;
  }

  // Lấy thống kê tổng quan
  async getOverview() {
    const restaurants = await this.getAllRestaurants();

    return {
      totalRestaurants: restaurants.length,
      activeRestaurants: restaurants.filter(r => r.isActive).length,
      verifiedRestaurants: restaurants.filter(r => r.isVerified).length,
      totalOrders: restaurants.reduce((sum, r) => sum + (r.totalOrders || 0), 0),
    };
  }

  // Lấy nhà hàng cần xác minh
  async getPendingVerification() {
    const restaurants = await this.getAllRestaurants();
    return restaurants.filter(r => !r.isVerified && r.isActive);
  }

  // Lấy nhà hàng inactive
  async getInactiveRestaurants() {
    const restaurants = await this.getAllRestaurants();
    return restaurants.filter(r => !r.isActive);
  }

  async getAllRestaurants() {
    const response = await fetch('http://localhost:8080/api/v1/admin/restaurants', {
      headers: { 'Authorization': `Bearer ${this.token}` },
    });
    return await response.json();
  }
}
```

---

## ⚠️ Important Notes

### Security

1. **Admin Only**: Tất cả endpoint đều yêu cầu role ADMIN
2. **Audit Logging**: Nên log tất cả hành động admin
3. **Two-Factor Auth**: Khuyến nghị bật 2FA cho tài khoản admin
4. **IP Whitelist**: Giới hạn IP có thể truy cập admin API

### Best Practices

1. **Soft Delete First**: Ưu tiên soft delete (inactive) trước khi hard delete
2. **Backup**: Backup dữ liệu trước khi xóa vĩnh viễn
3. **Confirmation**: Yêu cầu xác nhận 2 lần trước khi xóa
4. **Restore**: Cân nhắc thêm tính năng restore cho soft delete

### Data Integrity

1. **Foreign Keys**: Xử lý cascade delete cho các bảng liên quan
2. **Orders**: Không xóa nhà hàng có đơn hàng đang active
3. **History**: Giữ lại lịch sử cho mục đích thống kê

---

## 🔮 Roadmap

### Phase 1 (Current)
- ✅ Restaurant management (list, delete)

### Phase 2 (Next)
- ⏳ User management
- ⏳ Menu items management
- ⏳ Order management

### Phase 3 (Future)
- ⏳ Statistics & analytics
- ⏳ Reports & exports
- ⏳ System configuration
- ⏳ Audit logs

---

## 💡 Tips

1. **Pagination**: Thêm pagination cho endpoint list all (khi dữ liệu lớn)
2. **Filters**: Thêm filters để tìm kiếm admin dễ dàng hơn
3. **Export**: Thêm chức năng export CSV/Excel cho báo cáo
4. **Real-time**: Sử dụng WebSocket cho monitoring real-time
5. **Caching**: Cache statistics để giảm load database
