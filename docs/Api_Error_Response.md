```markdown
# API Error Responses Documentation

## 📋 Table of Contents
- [Error Response Format](#error-response-format)
- [HTTP Status Codes](#http-status-codes)
- [Authentication Errors](#authentication-errors)
- [Authorization Errors](#authorization-errors)
- [Validation Errors](#validation-errors)
- [Resource Errors](#resource-errors)
- [Business Logic Errors](#business-logic-errors)
- [Server Errors](#server-errors)
- [Error Codes Reference](#error-codes-reference)

---

## Error Response Format

All API errors follow a consistent JSON structure:

```json
{
  "timestamp": "2025-12-14T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for object='registerRequest'",
  "path": "/api/v1/auth/register",
  "errors": [
    {
      "field": "email",
      "rejectedValue": "invalid-email",
      "message": "Email should be valid"
    }
  ]
}
```

### Fields Description

| Field | Type | Description |
|-------|------|-------------|
| `timestamp` | ISO 8601 DateTime | When the error occurred |
| `status` | Integer | HTTP status code |
| `error` | String | HTTP status text |
| `message` | String | Detailed error message |
| `path` | String | API endpoint that caused the error |
| `errors` | Array | List of validation errors (optional) |

---

## HTTP Status Codes

### Success Codes (2xx)

| Code | Status | Description |
|------|--------|-------------|
| 200 | OK | Request successful |
| 201 | Created | Resource created successfully |
| 204 | No Content | Request successful, no content to return |

### Client Error Codes (4xx)

| Code | Status | Description |
|------|--------|-------------|
| 400 | Bad Request | Invalid request format or validation error |
| 401 | Unauthorized | Authentication required or failed |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Resource already exists |
| 422 | Unprocessable Entity | Business logic validation failed |
| 429 | Too Many Requests | Rate limit exceeded |

### Server Error Codes (5xx)

| Code | Status | Description |
|------|--------|-------------|
| 500 | Internal Server Error | Unexpected server error |
| 502 | Bad Gateway | Invalid response from upstream server |
| 503 | Service Unavailable | Server temporarily unavailable |

---

## Authentication Errors

### 1. Missing or Invalid Token

**Status Code:** `401 Unauthorized`

**Scenario:** No token provided or token is malformed

```json
{
  "timestamp": "2025-12-14T10:30:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/api/v1/users/me"
}
```

**How to Fix:**
- Include `Authorization: Bearer {token}` header
- Ensure token format is correct

---

### 2. Expired Token

**Status Code:** `401 Unauthorized`

**Scenario:** Access token has expired

```json
{
  "timestamp": "2025-12-14T10:30:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "JWT token is expired",
  "path": "/api/v1/restaurants/123/menu-items"
}
```

**How to Fix:**
- Use refresh token to get new access token
- Call `POST /api/v1/auth/refresh`

---

### 3. Invalid Credentials

**Status Code:** `401 Unauthorized`

**Scenario:** Wrong username/password during login

```json
{
  "timestamp": "2025-12-14T10:30:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid username or password",
  "path": "/api/v1/auth/login"
}
```

**How to Fix:**
- Verify username and password are correct
- Check if account exists

---

### 4. Invalid Refresh Token

**Status Code:** `401 Unauthorized`

**Scenario:** Refresh token is invalid or expired

```json
{
  "timestamp": "2025-12-14T10:30:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid refresh token",
  "path": "/api/v1/auth/refresh"
}
```

**How to Fix:**
- User must login again
- Cannot refresh with expired refresh token

---

## Authorization Errors

### 1. Insufficient Permissions

**Status Code:** `403 Forbidden`

**Scenario:** User doesn't have required role

```json
{
  "timestamp": "2025-12-14T10:30:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/v1/admin/users"
}
```

**Example Scenarios:**
- Customer trying to access admin endpoints
- Customer trying to create menu items
- User trying to delete resources without permission

**How to Fix:**
- Ensure user has correct role (ADMIN, MERCHANT, etc.)
- Check endpoint permissions

---

### 2. Not Resource Owner

**Status Code:** `403 Forbidden`

**Scenario:** Merchant trying to modify another merchant's restaurant

```json
{
  "timestamp": "2025-12-14T10:30:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "You don't have permission to modify this resource",
  "path": "/api/v1/restaurants/abc123/menu-items/def456"
}
```

**How to Fix:**
- Only modify resources you own
- Admins can modify any resource

---

## Validation Errors

### 1. Missing Required Fields

**Status Code:** `400 Bad Request`

**Scenario:** Required fields not provided

```json
{
  "timestamp": "2025-12-14T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/auth/register",
  "errors": [
    {
      "field": "username",
      "rejectedValue": null,
      "message": "Username is required"
    },
    {
      "field": "email",
      "rejectedValue": null,
      "message": "Email is required"
    },
    {
      "field": "password",
      "rejectedValue": null,
      "message": "Password is required"
    }
  ]
}
```

---

### 2. Invalid Email Format

**Status Code:** `400 Bad Request`

```json
{
  "timestamp": "2025-12-14T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/auth/register",
  "errors": [
    {
      "field": "email",
      "rejectedValue": "not-an-email",
      "message": "Email should be valid"
    }
  ]
}
```

---

### 3. Password Too Weak

**Status Code:** `400 Bad Request`

```json
{
  "timestamp": "2025-12-14T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/auth/register",
  "errors": [
    {
      "field": "password",
      "rejectedValue": "123",
      "message": "Password must be at least 8 characters long"
    }
  ]
}
```

---

### 4. Invalid Input Format

**Status Code:** `400 Bad Request`

**Scenario:** Invalid UUID, number format, etc.

```json
{
  "timestamp": "2025-12-14T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid UUID string: not-a-uuid",
  "path": "/api/v1/restaurants/not-a-uuid"
}
```

---

### 5. Phone Number Format

**Status Code:** `400 Bad Request`

```json
{
  "timestamp": "2025-12-14T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/auth/register",
  "errors": [
    {
      "field": "phoneNumber",
      "rejectedValue": "123",
      "message": "Phone number must be in format +84xxxxxxxxx"
    }
  ]
}
```

---

## Resource Errors

### 1. Resource Not Found

**Status Code:** `404 Not Found`

**Scenario:** Requested resource doesn't exist

```json
{
  "timestamp": "2025-12-14T10:30:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Restaurant not found with id: 123e4567-e89b-12d3-a456-426614174000",
  "path": "/api/v1/restaurants/123e4567-e89b-12d3-a456-426614174000"
}
```

**Common Scenarios:**
- Restaurant not found
- Menu item not found
- User not found
- Order not found

---

### 2. User Not Found

**Status Code:** `404 Not Found`

```json
{
  "timestamp": "2025-12-14T10:30:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found",
  "path": "/api/v1/users/123e4567-e89b-12d3-a456-426614174000"
}
```

---

## Business Logic Errors

### 1. User Already Exists

**Status Code:** `409 Conflict`

**Scenario:** Trying to register with existing username/email/phone

```json
{
  "timestamp": "2025-12-14T10:30:00.000+00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Username already exists",
  "path": "/api/v1/auth/register"
}
```

**Similar Errors:**
- `Email already exists`
- `Phone number already exists`

---

### 2. Restaurant Closed

**Status Code:** `422 Unprocessable Entity`

```json
{
  "timestamp": "2025-12-14T10:30:00.000+00:00",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Restaurant is currently closed",
  "path": "/api/v1/orders"
}
```

---

### 3. Menu Item Unavailable

**Status Code:** `422 Unprocessable Entity`

```json
{
  "timestamp": "2025-12-14T10:30:00.000+00:00",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Menu item 'Margherita Pizza' is currently unavailable",
  "path": "/api/v1/orders"
}
```

---

### 4. Account Locked

**Status Code:** `403 Forbidden`

```json
{
  "timestamp": "2025-12-14T10:30:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Account is locked. Please contact support",
  "path": "/api/v1/auth/login"
}
```

---

### 5. Email Not Verified

**Status Code:** `403 Forbidden`

```json
{
  "timestamp": "2025-12-14T10:30:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Email verification required",
  "path": "/api/v1/orders"
}
```

---

## Server Errors

### 1. Internal Server Error

**Status Code:** `500 Internal Server Error`

```json
{
  "timestamp": "2025-12-14T10:30:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred. Please try again later",
  "path": "/api/v1/restaurants"
}
```

**What to Do:**
- Contact support if error persists
- Check server logs
- Retry request after some time

---

### 2. Database Connection Error

**Status Code:** `503 Service Unavailable`

```json
{
  "timestamp": "2025-12-14T10:30:00.000+00:00",
  "status": 503,
  "error": "Service Unavailable",
  "message": "Database connection failed. Please try again later",
  "path": "/api/v1/restaurants"
}
```

---

## Error Codes Reference

### Authentication & Authorization

| Error Code | Status | Message |
|------------|--------|---------|
| AUTH_001 | 401 | Invalid credentials |
| AUTH_002 | 401 | Token expired |
| AUTH_003 | 401 | Invalid token |
| AUTH_004 | 401 | Token missing |
| AUTH_005 | 403 | Insufficient permissions |
| AUTH_006 | 403 | Account locked |
| AUTH_007 | 403 | Email not verified |

### Validation

| Error Code | Status | Message |
|------------|--------|---------|
| VAL_001 | 400 | Required field missing |
| VAL_002 | 400 | Invalid email format |
| VAL_003 | 400 | Invalid phone format |
| VAL_004 | 400 | Password too weak |
| VAL_005 | 400 | Invalid UUID format |

### Resources

| Error Code | Status | Message |
|------------|--------|---------|
| RES_001 | 404 | Restaurant not found |
| RES_002 | 404 | Menu item not found |
| RES_003 | 404 | User not found |
| RES_004 | 404 | Order not found |

### Business Logic

| Error Code | Status | Message |
|------------|--------|---------|
| BUS_001 | 409 | Username already exists |
| BUS_002 | 409 | Email already exists |
| BUS_003 | 409 | Phone number already exists |
| BUS_004 | 422 | Restaurant closed |
| BUS_005 | 422 | Menu item unavailable |
| BUS_006 | 422 | Insufficient balance |

---

## Example API Call with Error Handling

### JavaScript/TypeScript Example

```typescript
async function loginUser(username: string, password: string) {
  try {
    const response = await fetch('http://localhost:8080/api/v1/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ username, password }),
    });

    if (!response.ok) {
      const error = await response.json();

      // Handle specific error codes
      switch (error.status) {
        case 401:
          throw new Error('Invalid username or password');
        case 403:
          if (error.message.includes('locked')) {
            throw new Error('Account is locked. Contact support');
          }
          break;
        case 500:
          throw new Error('Server error. Please try again later');
        default:
          throw new Error(error.message || 'An error occurred');
      }
    }

    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Login failed:', error);
    throw error;
  }
}
```

### Flutter/Dart Example

```dart
Future<JwtAuthResponse> login(String username, String password) async {
  try {
    final response = await http.post(
      Uri.parse('http://localhost:8080/api/v1/auth/login'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'username': username,
        'password': password,
      }),
    );

    if (response.statusCode == 200) {
      return JwtAuthResponse.fromJson(jsonDecode(response.body));
    } else {
      final error = jsonDecode(response.body);

      switch (response.statusCode) {
        case 401:
          throw Exception('Invalid username or password');
        case 403:
          if (error['message'].contains('locked')) {
            throw Exception('Account is locked');
          }
          break;
        case 500:
          throw Exception('Server error. Please try again');
        default:
          throw Exception(error['message'] ?? 'An error occurred');
      }
    }
  } catch (e) {
    rethrow;
  }
}
```

---

## Best Practices for Error Handling

### Frontend Implementation

1. **Always Check Status Code**
   ```javascript
   if (!response.ok) {
     // Handle error
   }
   ```

2. **Display User-Friendly Messages**
   ```javascript
   // Don't show technical errors to users
   const userMessage = error.status === 500
     ? 'Something went wrong. Please try again'
     : error.message;
   ```

3. **Implement Retry Logic**
   ```javascript
   async function fetchWithRetry(url, options, retries = 3) {
     for (let i = 0; i < retries; i++) {
       try {
         return await fetch(url, options);
       } catch (error) {
         if (i === retries - 1) throw error;
         await new Promise(r => setTimeout(r, 1000 * (i + 1)));
       }
     }
   }
   ```

4. **Handle Token Expiry**
   ```javascript
   if (error.status === 401 && error.message.includes('expired')) {
     // Attempt token refresh
     const newToken = await refreshAccessToken();
     // Retry original request with new token
   }
   ```

5. **Log Errors for Debugging**
   ```javascript
   console.error('API Error:', {
     endpoint: error.path,
     status: error.status,
     message: error.message,
     timestamp: error.timestamp
   });
   ```

---

## Testing Error Responses

### Using cURL

```bash
# Test invalid credentials
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"wrong","password":"wrong"}' \
  -v

# Test missing token
curl -X GET http://localhost:8080/api/v1/users/me \
  -v

# Test expired token
curl -X GET http://localhost:8080/api/v1/users/me \
  -H "Authorization: Bearer expired_token_here" \
  -v

# Test invalid UUID
curl -X GET http://localhost:8080/api/v1/restaurants/invalid-uuid \
  -v
```

---

## Contact & Support

If you encounter errors not documented here:

- 📧 Email: support@foodya.com
- 📝 GitHub Issues: [Create Issue](https://github.com/foodya/foodya-backend/issues)
- 📖 API Documentation: http://localhost:8080/swagger-ui.html

---

**Last Updated:** December 14, 2025
**API Version:** 1.0
**Document Version:** 1.0
```
