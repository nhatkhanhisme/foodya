# API Error Handling Guide

Hướng dẫn xử lý lỗi khi sử dụng Foodya Backend API.

---

## 📋 HTTP Status Codes

### Success Responses (2xx)

| Code | Name | Description | Usage |
|------|------|-------------|-------|
| 200 | OK | Request thành công | GET, PUT, PATCH requests |
| 201 | Created | Tạo mới thành công | POST requests (tạo resource mới) |
| 204 | No Content | Xóa thành công | DELETE requests |

### Client Error Responses (4xx)

| Code | Name | Description | Common Causes |
|------|------|-------------|---------------|
| 400 | Bad Request | Dữ liệu không hợp lệ | - Validation failed<br>- Missing required fields<br>- Invalid format |
| 401 | Unauthorized | Chưa xác thực | - Missing token<br>- Invalid token<br>- Expired token |
| 403 | Forbidden | Không có quyền truy cập | - Wrong role<br>- Not resource owner |
| 404 | Not Found | Không tìm thấy resource | - Invalid ID<br>- Resource deleted |
| 409 | Conflict | Dữ liệu bị trùng lặp | - Duplicate username<br>- Duplicate email/phone |

### Server Error Responses (5xx)

| Code | Name | Description |
|------|------|-------------|
| 500 | Internal Server Error | Lỗi server |

---

## 🔧 Error Response Format

### Standard Error Response

```json
{
  "timestamp": "2025-12-31T16:00:00.123+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/auth/register",
  "details": {
    "username": "Username must be between 3 and 50 characters",
    "password": "Password must contain at least one digit"
  }
}
```

### Error Response Fields

| Field | Type | Description |
|-------|------|-------------|
| timestamp | String | Thời gian xảy ra lỗi (ISO 8601) |
| status | Integer | HTTP status code |
| error | String | Tên lỗi HTTP |
| message | String | Mô tả lỗi chính |
| path | String | API endpoint gây lỗi |
| details | Object | Chi tiết lỗi (nếu có) |

---

## 💡 Common Errors & Solutions

### 1. Authentication Errors

#### Error: 401 Unauthorized - Missing Token

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource"
}
```

**Solution:**
```javascript
// Thêm token vào header
const response = await fetch(url, {
  headers: {
    'Authorization': `Bearer ${accessToken}`,
  },
});
```

#### Error: 401 Unauthorized - Invalid/Expired Token

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "JWT token is expired or invalid"
}
```

**Solution:**
```javascript
// Làm mới token
const refreshResponse = await fetch('/api/v1/auth/refresh', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ refreshToken }),
});

const { accessToken } = await refreshResponse.json();
// Retry với token mới
```

---

### 2. Validation Errors

#### Error: 400 Bad Request - Validation Failed

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": {
    "username": "Username must be between 3 and 50 characters",
    "email": "Email must be valid",
    "password": "Password must be at least 8 characters"
  }
}
```

**Solution:**
```javascript
// Validate dữ liệu trước khi gửi
const validateRegisterForm = (data) => {
  const errors = {};

  if (!data.username || data.username.length < 3) {
    errors.username = 'Username must be at least 3 characters';
  }

  if (!data.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(data.email)) {
    errors.email = 'Email must be valid';
  }

  if (!data.password || data.password.length < 8) {
    errors.password = 'Password must be at least 8 characters';
  }

  return errors;
};
```

---

### 3. Duplicate Data Errors

#### Error: 409 Conflict

```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Username already exists"
}
```

**Solution:**
```javascript
// Kiểm tra trước khi submit
const checkUsernameAvailable = async (username) => {
  try {
    const response = await fetch(`/api/v1/auth/check-username?username=${username}`);
    return response.ok;
  } catch (error) {
    return false;
  }
};

// Hiển thị lỗi cho user
if (error.status === 409) {
  alert('Username already exists. Please choose another one.');
}
```

---

### 4. Not Found Errors

#### Error: 404 Not Found

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Restaurant not found with id: 8f8e8334-9347-4933-9333-875865538050"
}
```

**Solution:**
```javascript
// Xử lý 404
try {
  const restaurant = await getRestaurant(id);
} catch (error) {
  if (error.status === 404) {
    // Redirect về trang danh sách hoặc hiển thị message
    console.error('Restaurant not found');
    navigate('/restaurants');
  }
}
```

---

### 5. Permission Errors

#### Error: 403 Forbidden

```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "You don't have permission to update this restaurant"
}
```

**Solution:**
```javascript
// Kiểm tra quyền trước khi hiển thị UI
const canEditRestaurant = (restaurant, currentUser) => {
  return restaurant.ownerId === currentUser.id || currentUser.role === 'ADMIN';
};

// Ẩn nút edit nếu không có quyền
{canEditRestaurant(restaurant, user) && (
  <button onClick={handleEdit}>Edit</button>
)}
```

---

## 🛠️ Error Handling Best Practices

### 1. Generic Error Handler

```javascript
const apiCall = async (url, options = {}) => {
  try {
    const response = await fetch(url, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`,
        ...options.headers,
      },
    });

    if (!response.ok) {
      const error = await response.json();
      throw {
        status: response.status,
        ...error,
      };
    }

    // Handle 204 No Content
    if (response.status === 204) {
      return null;
    }

    return await response.json();
  } catch (error) {
    // Handle network errors
    if (!error.status) {
      throw {
        status: 0,
        message: 'Network error. Please check your connection.',
      };
    }
    throw error;
  }
};
```

### 2. Retry Logic for 401

```javascript
const apiCallWithRetry = async (url, options = {}) => {
  try {
    return await apiCall(url, options);
  } catch (error) {
    // Retry once if token expired
    if (error.status === 401 && !options._retry) {
      const newToken = await refreshAccessToken();
      return apiCall(url, {
        ...options,
        _retry: true,
        headers: {
          ...options.headers,
          'Authorization': `Bearer ${newToken}`,
        },
      });
    }
    throw error;
  }
};
```

### 3. User-Friendly Error Messages

```javascript
const getErrorMessage = (error) => {
  const messages = {
    400: 'Invalid input. Please check your data.',
    401: 'Please log in to continue.',
    403: 'You don\'t have permission to perform this action.',
    404: 'The requested resource was not found.',
    409: 'This data already exists.',
    500: 'Server error. Please try again later.',
  };

  return error.message || messages[error.status] || 'An error occurred';
};

// Usage
try {
  await createOrder(orderData);
} catch (error) {
  toast.error(getErrorMessage(error));
}
```

### 4. React Error Boundary

```javascript
class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    console.error('Error caught by boundary:', error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="error-page">
          <h1>Oops! Something went wrong.</h1>
          <p>{this.state.error?.message}</p>
          <button onClick={() => window.location.reload()}>
            Reload Page
          </button>
        </div>
      );
    }

    return this.props.children;
  }
}
```

---

## 📊 Error Logging

### Log Errors to Server

```javascript
const logError = async (error, context) => {
  try {
    await fetch('/api/v1/logs/errors', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        error: {
          message: error.message,
          status: error.status,
          stack: error.stack,
        },
        context: {
          url: window.location.href,
          userAgent: navigator.userAgent,
          timestamp: new Date().toISOString(),
          ...context,
        },
      }),
    });
  } catch (e) {
    console.error('Failed to log error:', e);
  }
};
```

---

## 💡 Tips

1. **Always Handle Errors**: Bọc tất cả API calls trong try-catch
2. **User-Friendly Messages**: Hiển thị message dễ hiểu cho user
3. **Log Important Errors**: Log lỗi để debugging và monitoring
4. **Retry Strategy**: Tự động retry cho một số lỗi tạm thời
5. **Offline Support**: Xử lý network errors và offline state
6. **Validation**: Validate client-side trước khi gửi request
7. **Loading States**: Hiển thị loading khi chờ response
8. **Error Recovery**: Cung cấp cách để user recover từ lỗi
