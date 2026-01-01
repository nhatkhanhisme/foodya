# Authentication API

API xác thực người dùng và quản lý JWT tokens.

**Base Path**: `/api/v1/auth`

---

## 📋 Endpoints

### 1. Đăng ký tài khoản mới

Tạo tài khoản người dùng mới.

- **URL**: `POST /api/v1/auth/register`
- **Authentication**: Không yêu cầu
- **Content-Type**: `application/json`

#### Request Body

```json
{
  "username": "nguyenvana",
  "email": "nguyenvana@example.com",
  "password": "SecurePass123!",
  "fullName": "Nguyen Van A",
  "phoneNumber": "+84987654321",
  "role": "CUSTOMER"
}
```

#### Request Fields

| Field | Type | Required | Description | Validation |
|-------|------|----------|-------------|------------|
| username | String | ✅ | Tên đăng nhập duy nhất | 3-50 ký tự, chỉ chữ, số, _, - |
| email | String | ✅ | Email hợp lệ | Định dạng email chuẩn |
| password | String | ✅ | Mật khẩu mạnh | Min 8 ký tự, có chữ hoa, chữ thường, số, ký tự đặc biệt |
| fullName | String | ✅ | Họ và tên đầy đủ | 2-100 ký tự |
| phoneNumber | String | ✅ | Số điện thoại | Định dạng quốc tế (+84...) |
| role | String | ❌ | Vai trò người dùng | CUSTOMER, MERCHANT, DELIVERY, ADMIN (Mặc định: CUSTOMER) |

#### Response (201 Created)

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "username": "nguyenvana",
  "role": "CUSTOMER"
}
```

#### Error Responses

| Status | Description | Example |
|--------|-------------|---------|
| 400 | Dữ liệu không hợp lệ | Username/Email/Phone đã tồn tại hoặc validation failed |
| 409 | Conflict | Username/Email/Phone number đã tồn tại |
| 500 | Lỗi server | Internal server error |

---

### 2. Đăng nhập

Xác thực người dùng và nhận JWT tokens.

- **URL**: `POST /api/v1/auth/login`
- **Authentication**: Không yêu cầu
- **Content-Type**: `application/json`

#### Request Body

```json
{
  "username": "nguyenvana",
  "password": "SecurePass123!"
}
```

#### Request Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| username | String | ✅ | Tên đăng nhập |
| password | String | ✅ | Mật khẩu |

#### Response (200 OK)

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "username": "nguyenvana",
  "role": "CUSTOMER"
}
```

#### Error Responses

| Status | Description |
|--------|-------------|
| 401 | Username hoặc password không đúng |
| 403 | Tài khoản đã bị vô hiệu hóa |

---

### 3. Làm mới Access Token

Tạo access token mới từ refresh token còn hiệu lực.

- **URL**: `POST /api/v1/auth/refresh`
- **Authentication**: Không yêu cầu
- **Content-Type**: `application/json`

#### Request Body

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Request Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| refreshToken | String | ✅ | Refresh token còn hiệu lực |

#### Response (200 OK)

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "username": "nguyenvana",
  "role": "CUSTOMER"
}
```

#### Error Responses

| Status | Description |
|--------|-------------|
| 401 | Refresh token không hợp lệ hoặc đã hết hạn |
| 404 | Không tìm thấy người dùng |

---

### 4. Đổi mật khẩu

Thay đổi mật khẩu cho người dùng đã đăng nhập.

- **URL**: `POST /api/v1/auth/change-password`
- **Authentication**: ✅ Required (Bearer Token)
- **Content-Type**: `application/json`

#### Request Headers

```
Authorization: Bearer <access_token>
```

#### Request Body

```json
{
  "currentPassword": "OldPass123!",
  "newPassword": "NewPass456!",
  "confirmPassword": "NewPass456!"
}
```

#### Request Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| currentPassword | String | ✅ | Mật khẩu hiện tại |
| newPassword | String | ✅ | Mật khẩu mới (phải đủ mạnh) |
| confirmPassword | String | ✅ | Xác nhận mật khẩu mới |

#### Response (200 OK)

```json
{
  "message": "Password changed successfully"
}
```

#### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Mật khẩu hiện tại không đúng hoặc validation failed |
| 401 | Chưa đăng nhập |

---

## 🔒 JWT Token Structure

### Access Token

- **Expiration**: 24 giờ (86400 giây)
- **Claims**: username, role, userId
- **Usage**: Gửi kèm trong header của mỗi API request cần xác thực

### Refresh Token

- **Expiration**: 30 ngày
- **Usage**: Dùng để lấy access token mới khi access token hết hạn

---

## 📝 Ví dụ sử dụng

### cURL

#### Đăng ký

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

#### Đăng nhập

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "nguyenvana",
    "password": "SecurePass123!"
  }'
```

#### Đổi mật khẩu

```bash
curl -X POST http://localhost:8080/api/v1/auth/change-password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_access_token>" \
  -d '{
    "currentPassword": "SecurePass123!",
    "newPassword": "NewSecurePass456!",
    "confirmPassword": "NewSecurePass456!"
  }'
```

### JavaScript (Fetch API)

```javascript
// Đăng nhập
const login = async (username, password) => {
  const response = await fetch('http://localhost:8080/api/v1/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ username, password }),
  });

  const data = await response.json();

  // Lưu tokens
  localStorage.setItem('accessToken', data.accessToken);
  localStorage.setItem('refreshToken', data.refreshToken);

  return data;
};
```

---

## ⚠️ Security Best Practices

1. **Mật khẩu**: Luôn yêu cầu mật khẩu mạnh (chữ hoa, chữ thường, số, ký tự đặc biệt)
2. **HTTPS**: Sử dụng HTTPS trong production để bảo vệ credentials
3. **Token Storage**: Lưu tokens an toàn (HTTPOnly cookies hoặc secure storage)
4. **Token Refresh**: Làm mới access token trước khi hết hạn
5. **Logout**: Xóa tokens khi logout
