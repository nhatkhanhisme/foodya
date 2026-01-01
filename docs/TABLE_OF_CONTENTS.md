# Foodya Backend API Documentation - Table of Contents

Tài liệu API đầy đủ cho Foodya Backend - Hệ thống giao đồ ăn.

---

## 📚 Tổng quan tài liệu

### Cấu trúc thư mục

```
docs/
├── README.md                        # Tổng quan tài liệu chính
├── quick-start.md                   # Hướng dẫn bắt đầu nhanh
├── error-handling.md                # Hướng dẫn xử lý lỗi
└── api/                             # Chi tiết các API endpoints
    ├── authentication.md            # API xác thực người dùng
    ├── restaurants.md               # API nhà hàng (Customer)
    ├── menu-items.md                # API menu món ăn (Customer)
    ├── orders.md                    # API đơn hàng (Customer)
    ├── merchant-restaurants.md      # API quản lý nhà hàng (Merchant)
    ├── merchant-menu-items.md       # API quản lý menu (Merchant)
    ├── merchant-orders.md           # API quản lý đơn hàng (Merchant)
    └── admin.md                     # API quản trị hệ thống (Admin)
```

---

## 🚀 Bắt đầu

### Dành cho người mới

1. **[Quick Start Guide](./quick-start.md)** ⭐
   - Cài đặt và chạy server
   - Ví dụ tích hợp mobile app
   - Test với Postman
   - Common workflows

### Tìm hiểu API

2. **[README - Tổng quan](./README.md)**
   - Kiến trúc hệ thống
   - Base URL và Authentication
   - HTTP Status Codes
   - Ví dụ cơ bản

3. **[Error Handling Guide](./error-handling.md)**
   - Các loại lỗi thường gặp
   - Cách xử lý lỗi
   - Best practices
   - Error logging

---

## 📖 API Documentation

### 🔐 Authentication APIs

**[Authentication API](./api/authentication.md)**
- 🆕 Đăng ký tài khoản mới (`POST /auth/register`)
- 🔑 Đăng nhập (`POST /auth/login`)
- 🔄 Làm mới token (`POST /auth/refresh`)
- 🔒 Đổi mật khẩu (`POST /auth/change-password`)

**Use Cases:**
- Customer đăng ký và đăng nhập
- Merchant đăng ký tài khoản kinh doanh
- Quản lý JWT tokens

---

### 👥 Customer APIs

#### **[Restaurant API](./api/restaurants.md)**
- 🔍 Tìm kiếm nhà hàng với filters (`GET /restaurants`)
- 📍 Lấy chi tiết nhà hàng (`GET /restaurants/{id}`)
- ⭐ Lấy nhà hàng phổ biến (`GET /restaurants/popular`)

**Features:**
- Search by keyword, cuisine, rating
- Pagination và sorting
- Filter theo nhiều tiêu chí

#### **[Menu Items API](./api/menu-items.md)**
- 📜 Lấy menu với phân trang (`GET /restaurants/{id}/menu-items`)
- 🍕 Lấy chi tiết món ăn (`GET /restaurants/{id}/menu-items/{itemId}`)
- 🔎 Tìm kiếm món ăn (`GET /restaurants/{id}/menu-items/search`)
- 🏆 Món phổ biến (`GET /restaurants/{id}/menu-items/popular`)
- 🥗 Lọc theo dietary (`GET /restaurants/{id}/menu-items/dietary`)

**Features:**
- Search trong menu
- Filter chay/thuần chay/gluten-free
- Sort by price, popularity
- Category filtering

#### **[Order API](./api/orders.md)**
- 🛒 Tạo đơn hàng mới (`POST /customers/orders`)
- 📋 Xem lịch sử đơn hàng (`GET /customers/orders/me`)
- 🚚 Theo dõi đơn đang giao (`GET /customers/orders/me/active`)
- ❌ Hủy đơn hàng (`PATCH /customers/orders/{id}/cancel`)

**Features:**
- Place order với nhiều món
- Track order status
- Cancel order (nếu hợp lệ)
- Order history

---

### 🏪 Merchant APIs

#### **[Merchant Restaurant API](./api/merchant-restaurants.md)**
- 📂 Lấy nhà hàng của tôi (`GET /merchant/restaurants/me`)
- ➕ Tạo nhà hàng mới (`POST /merchant/restaurants`)
- ✏️ Cập nhật thông tin (`PUT /merchant/restaurants/{id}`)
- 🔄 Bật/Tắt mở cửa (`PATCH /merchant/restaurants/{id}/toggle-status`)

**Features:**
- Quản lý thông tin nhà hàng
- Toggle open/close
- Update delivery info, pricing

#### **[Merchant Menu Items API](./api/merchant-menu-items.md)**
- 📋 Lấy tất cả món (kể cả inactive) (`GET /merchant/restaurants/{id}/menu-items`)
- ➕ Thêm món mới (`POST /merchant/restaurants/{id}/menu-items`)
- ✏️ Cập nhật món (`PUT /merchant/restaurants/{id}/menu-items/{itemId}`)
- 🗑️ Xóa món (soft delete) (`DELETE /merchant/restaurants/{id}/menu-items/{itemId}`)
- 🔄 Toggle còn/hết hàng (`PATCH /merchant/restaurants/{id}/menu-items/{itemId}/toggle-availability`)

**Features:**
- Full CRUD menu items
- Soft delete (archive)
- Toggle availability
- Category management

#### **[Merchant Order API](./api/merchant-orders.md)**
- 📦 Lấy đơn hàng nhà hàng (`GET /merchant/orders/restaurant/{restaurantId}`)
- 📄 Chi tiết đơn hàng (`GET /merchant/orders/{id}`)
- ✅ Cập nhật trạng thái (`PATCH /merchant/orders/{id}/status`)

**Features:**
- View all restaurant orders
- Update order status (PENDING → PREPARING → SHIPPING → DELIVERED)
- Process orders workflow

**Status Flow:**
```
PENDING → PREPARING → SHIPPING → DELIVERED
   ↓          ↓
CANCELLED  CANCELLED
```

---

### 👨‍💼 Admin APIs

#### **[Admin API](./api/admin.md)**
- 🏢 Lấy tất cả nhà hàng (kể cả inactive) (`GET /admin/restaurants`)
- 🗑️ Xóa nhà hàng vĩnh viễn (`DELETE /admin/restaurants/{id}`)

**Features:**
- System-wide management
- Hard delete operations
- View all data (including inactive)

**Coming Soon:**
- User management
- Menu items management
- Statistics & reports
- System configuration

---

## 🎯 Use Case Guides

### Customer Flow
```
1. Đăng ký/Đăng nhập → Authentication API
2. Tìm nhà hàng → Restaurant API
3. Xem menu → Menu Items API
4. Đặt món → Order API
5. Theo dõi đơn → Order API
```

### Merchant Flow
```
1. Đăng ký Merchant → Authentication API
2. Tạo nhà hàng → Merchant Restaurant API
3. Thêm món ăn → Merchant Menu Items API
4. Nhận & xử lý đơn → Merchant Order API
```

### Admin Flow
```
1. Đăng nhập Admin → Authentication API
2. Quản lý nhà hàng → Admin API
3. Quản lý users → Admin API (coming soon)
4. Xem thống kê → Admin API (coming soon)
```

---

## 📊 API Statistics

| Category | Endpoints | Documentation |
|----------|-----------|---------------|
| Authentication | 4 | [Docs](./api/authentication.md) |
| Restaurants (Customer) | 3 | [Docs](./api/restaurants.md) |
| Menu Items (Customer) | 7 | [Docs](./api/menu-items.md) |
| Orders (Customer) | 4 | [Docs](./api/orders.md) |
| Restaurants (Merchant) | 4 | [Docs](./api/merchant-restaurants.md) |
| Menu Items (Merchant) | 6 | [Docs](./api/merchant-menu-items.md) |
| Orders (Merchant) | 3 | [Docs](./api/merchant-orders.md) |
| Admin | 2+ | [Docs](./api/admin.md) |
| **Total** | **33+** | |

---

## 🔑 Key Concepts

### Authentication
- **JWT Bearer Token** - Access token (24h) & Refresh token (30 days)
- **Roles**: CUSTOMER, MERCHANT, DELIVERY, ADMIN

### Roles & Permissions

| Role | Permissions |
|------|------------|
| **CUSTOMER** | Browse, Order, Track orders |
| **MERCHANT** | Manage own restaurants, menu, orders |
| **DELIVERY** | View & update delivery orders |
| **ADMIN** | Full system access |

### Common Patterns

1. **Pagination**: `page`, `size` params
2. **Sorting**: `sortBy`, `sortDirection` params
3. **Filtering**: Query parameters
4. **Soft Delete**: `isActive` flag
5. **Toggle**: PATCH endpoints

---

## 🛠️ Development Tools

### Swagger UI
```
http://localhost:8080/swagger-ui.html
```

### Actuator Health
```
http://localhost:8080/actuator/health
```

### Postman Collection
Import từ Swagger hoặc tự tạo theo [Quick Start](./quick-start.md)

---

## 📝 Response Formats

### Success Response (List)
```json
{
  "content": [...],
  "totalPages": 5,
  "totalElements": 95,
  "size": 20,
  "number": 0
}
```

### Success Response (Single)
```json
{
  "id": "...",
  "name": "...",
  ...
}
```

### Error Response
```json
{
  "timestamp": "2025-12-31T16:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": {...}
}
```

---

## 💡 Best Practices

1. ✅ **Always validate** input trước khi gửi request
2. ✅ **Handle errors** gracefully
3. ✅ **Store tokens** securely
4. ✅ **Use pagination** cho large datasets
5. ✅ **Implement retry** logic cho network errors
6. ✅ **Log important** events
7. ✅ **Cache responses** khi phù hợp

---

## 🆘 Support

- 📧 **Email**: support@foodya.com
- 📚 **Documentation**: Thư mục này
- 🐛 **Bug Reports**: GitHub Issues
- 💬 **Community**: Discord/Slack

---

## 📅 Version History

- **v0.0.1-SNAPSHOT** (Current)
  - Initial API documentation
  - Core endpoints implemented
  - Authentication, Restaurant, Menu, Order APIs

---

## 🔮 Roadmap

### Phase 1 ✅
- Authentication APIs
- Customer APIs (Restaurant, Menu, Order)
- Merchant APIs (Restaurant, Menu, Order)
- Basic Admin APIs

### Phase 2 🔄
- Admin User Management
- Delivery APIs
- Advanced filtering & search
- Real-time notifications

### Phase 3 📅
- Payment integration
- Reviews & Ratings
- Loyalty program
- Analytics & Reports

---

## 📖 Quick Links

| Document | Description | For |
|----------|-------------|-----|
| [Quick Start](./quick-start.md) | Bắt đầu nhanh | All |
| [Error Handling](./error-handling.md) | Xử lý lỗi | Developers |
| [Authentication](./api/authentication.md) | Auth APIs | All |
| [Restaurants](./api/restaurants.md) | Customer APIs | Mobile App |
| [Orders](./api/orders.md) | Order APIs | Mobile App |
| [Merchant Docs](./api/merchant-restaurants.md) | Merchant APIs | Merchant App |
| [Admin](./api/admin.md) | Admin APIs | Admin Panel |

---

**Happy Coding! 🚀**
