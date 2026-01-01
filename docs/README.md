# Foodya Backend API Documentation

## 📖 Tổng quan

Foodya Backend là REST API cho ứng dụng giao đồ ăn, được xây dựng bằng Spring Boot 3.3.8 và Java 21.

## 🏗️ Kiến trúc hệ thống

- **Framework**: Spring Boot 3.3.8
- **Database**: PostgreSQL
- **Authentication**: JWT (JSON Web Tokens)
- **API Documentation**: OpenAPI 3.0 (Swagger)

## 🔗 Base URL

```
http://localhost:8080/api/v1
```

## 🔐 Authentication

API sử dụng JWT Bearer Token để xác thực. Thêm token vào header của request:

```
Authorization: Bearer <your_jwt_token>
```

## 👥 Vai trò (Roles)

Hệ thống hỗ trợ 4 vai trò người dùng:

- **CUSTOMER**: Khách hàng đặt món
- **MERCHANT**: Chủ nhà hàng quản lý món ăn và đơn hàng
- **DELIVERY**: Nhân viên giao hàng
- **ADMIN**: Quản trị viên hệ thống

## 📚 Tài liệu API theo module

1. [**Authentication API**](./api/authentication.md) - Đăng ký, đăng nhập, làm mới token
2. [**Restaurant API**](./api/restaurants.md) - Tìm kiếm và xem thông tin nhà hàng
3. [**Menu Items API**](./api/menu-items.md) - Xem menu và món ăn
4. [**Order API**](./api/orders.md) - Đặt hàng và quản lý đơn hàng
5. [**Merchant Restaurant API**](./api/merchant-restaurants.md) - Quản lý nhà hàng (Merchant)
6. [**Merchant Menu Items API**](./api/merchant-menu-items.md) - Quản lý menu (Merchant)
7. [**Merchant Order API**](./api/merchant-orders.md) - Quản lý đơn hàng (Merchant)
8. [**Admin API**](./api/admin.md) - Quản trị hệ thống

## 🚀 Swagger UI

Sau khi chạy ứng dụng, truy cập Swagger UI tại:

```
http://localhost:8080/swagger-ui.html
```

## 📋 HTTP Status Codes

| Status Code | Ý nghĩa |
|------------|---------|
| 200 | OK - Request thành công |
| 201 | Created - Tạo mới thành công |
| 204 | No Content - Xóa thành công |
| 400 | Bad Request - Dữ liệu không hợp lệ |
| 401 | Unauthorized - Chưa đăng nhập hoặc token không hợp lệ |
| 403 | Forbidden - Không có quyền truy cập |
| 404 | Not Found - Không tìm thấy tài nguyên |
| 409 | Conflict - Dữ liệu bị trùng lặp |
| 500 | Internal Server Error - Lỗi server |

## 🔧 Cài đặt và chạy

### Yêu cầu

- Java 21
- PostgreSQL 14+
- Maven 3.8+

### Cài đặt

1. Clone repository
2. Cấu hình database trong `application.properties`
3. Chạy ứng dụng:

```bash
./mvnw spring-boot:run
```

## 📝 Ví dụ sử dụng

### 1. Đăng ký tài khoản

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "nguyenvana",
    "email": "nguyenvana@example.com",
    "password": "SecurePass123!",
    "fullName": "Nguyen Van A",
    "phoneNumber": "+84987654321",
    "role": "CUSTOMER"
  }'
```

### 2. Đăng nhập

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "nguyenvana",
    "password": "SecurePass123!"
  }'
```

### 3. Tìm kiếm nhà hàng

```bash
curl -X GET "http://localhost:8080/api/v1/restaurants?keyword=pizza&minRating=4.0&page=0&size=20" \
  -H "Authorization: Bearer <your_token>"
```

## 🤝 Contributing

Vui lòng đọc [CONTRIBUTING.md](../CONTRIBUTING.md) để biết chi tiết về quy trình đóng góp.

## 📄 License

Dự án này được cấp phép theo giấy phép MIT.

## 📞 Liên hệ

- Email: support@foodya.com
- Website: https://foodya.com
