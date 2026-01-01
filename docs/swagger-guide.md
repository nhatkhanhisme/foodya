# Swagger API Documentation Enhancement Guide

Hướng dẫn sử dụng `ApiResponseExamples` để cải thiện Swagger documentation.

---

## 📖 Tổng quan

Đã tạo utility class `ApiResponseExamples` chứa các annotation có sẵn để document error responses một cách nhất quán across tất cả controllers.

**Benefits:**
- ✅ Consistent error documentation
- ✅ Detailed examples cho mỗi error case
- ✅ Easy to maintain
- ✅ Reusable annotations

---

## 🔧 Available Annotations

### Individual Error Responses

```java
@BadRequest           // 400 - Validation failed
@Unauthorized         // 401 - Auth required/invalid token
@Forbidden            // 403 - Insufficient permissions
@NotFound             // 404 - Resource not found
@Conflict             // 409 - Duplicate resource
@InternalServerError  // 500 - Server error
```

### Combined Response Groups

```java
@StandardGetResponses      // GET: 200, 404, 500
@StandardCreateResponses   // POST: 201, 400, 401, 409, 500
@StandardUpdateResponses   // PUT/PATCH: 200, 400, 401, 403, 404, 409, 500
@StandardDeleteResponses   // DELETE: 204, 401, 403, 404, 500
@PublicEndpointResponses   // Public: 200, 400, 404, 500
```

---

## 📝 Usage Examples

### Example 1: Simple GET endpoint

**Before:**
```java
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Success"),
    @ApiResponse(responseCode = "404", description = "Not found"),
    @ApiResponse(responseCode = "500", description = "Server error")
})
```

**After:**
```java
@ApiResponse(
    responseCode = "200",
    description = "Success",
    content = @Content(schema = @Schema(implementation = RestaurantResponse.class))
)
@StandardGetResponses
```

---

### Example 2: POST endpoint (Create)

**Before:**
```java
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Created"),
    @ApiResponse(responseCode = "400", description = "Bad request"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "409", description = "Conflict")
})
```

**After:**
```java
@ApiResponse(
    responseCode = "201",
    description = "Resource created successfully",
    content = @Content(schema = @Schema(implementation = RestaurantResponse.class))
)
@BadRequest
@Unauthorized
@Conflict
@InternalServerError
```

---

### Example 3: Custom combinations

```java
// Login endpoint - specific errors
@ApiResponse(
    responseCode = "200",
    description = "Login successful",
    content = @Content(schema = @Schema(implementation = JwtAuthResponse.class))
)
@Unauthorized  // Invalid credentials
@Forbidden     // Account deactivated
```

---

## 🎯 Implementation Pattern

### Complete Example - AuthController

```java
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Auth APIs")
public class AuthController {

    @Operation(
        summary = "Register new user",
        description = "Create account. Phone number auto-normalized to +84..."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "User registered successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = JwtAuthResponse.class)
            )
        )
    })
    @BadRequest           // Validation errors with examples
    @Conflict             // Duplicate username/email/phone
    @InternalServerError  // Server errors
    @PostMapping("/register")
    public ResponseEntity<JwtAuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        // ...
    }
}
```

---

## 📊 Error Examples Included

### BadRequest (400)

Includes example:
```json
{
  "timestamp": "2026-01-01T14:52:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": {
    "username": "Username must be between 3 and 50 characters",
    "email": "Email must be valid",
    "password": "Password must contain at least one digit..."
  }
}
```

### Unauthorized (401)

Includes 3 examples:
1. Missing Token
2. Invalid/Expired Token
3. Invalid Credentials

```json
{
  "timestamp": "2026-01-01T14:52:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "JWT token is expired or invalid"
}
```

### Conflict (409)

Includes 3 examples:
1. Duplicate Username
2. Duplicate Email
3. Duplicate Phone Number

---

## ✅ Controllers Updated

- [x] ✅ **AuthController** - All 4 endpoints
  - `POST /register`
  - `POST /login`
  - `POST /refresh`
  - `POST /change-password`

---

## 📋 Next Steps

Áp dụng cho các controllers khác:

### Priority 1 - Customer APIs
- [ ] RestaurantController (3 endpoints)
- [ ] MenuItemController (7 endpoints)
- [ ] OrderController (4 endpoints)

### Priority 2 - Merchant APIs
- [ ] MerchantRestaurantController
- [ ] MerchantMenuItemController
- [ ] MerchantOrderController

### Priority 3 - Admin APIs
- [ ] AdminRestaurantController
- [ ] AdminUserController
- [ ] AdminOrderController

---

## 🔍 Verification

### Check Swagger UI

1. Start application
2. Vào http://localhost:8080/swagger-ui.html
3. Click vào endpoint (e.g., POST /api/v1/auth/register)
4. Xem "Responses" section
5. Verify:
   - ✅ Status code 201, 400, 409, 500 được list
   - ✅ Mỗi status có description
   - ✅ Error responses có schema `ErrorResponse`
   - ✅ Có example JSON đầy đủ

### Example View in Swagger

```
Responses
├─ 201 User registered successfully
│  └─ application/json
│     └─ JwtAuthResponse {accessToken, refreshToken, ...}
├─ 400 Bad Request - Validation failed or invalid input
│  └─ application/json
│     └─ ErrorResponse {timestamp, status, error, message, details}
│     └─ Example: Validation Failed
├─ 409 Conflict - Resource already exists
│  └─ application/json
│     └─ ErrorResponse {timestamp, status, error, message}
│     └─ Examples:
│        ├─ Duplicate Username
│        ├─ Duplicate Email
│        └─ Duplicate Phone Number
└─ 500 Internal Server Error
   └─ application/json
      └─ ErrorResponse {timestamp, status, error, message}
```

---

## 💡 Tips

1. **Always include success response first**
   ```java
   @ApiResponse(responseCode = "200", ...) // Success
   @BadRequest                              // Then errors
   @NotFound
   ```

2. **Use specific annotations for specific errors**
   ```java
   // For login - only these errors are possible
   @Unauthorized  // Wrong password
   @Forbidden     // Account deactivated

   // NOT needed:
   // @BadRequest (validation handled by framework)
   // @NotFound (user lookup returns 401)
   ```

3. **Combine with @Operation for better docs**
   ```java
   @Operation(
       summary = "Short title",
       description = "Detailed explanation including: " +
                     "- What it does " +
                     "- Special behaviors " +
                     "- Validation rules"
   )
   ```

4. **Custom examples for specific endpoints**
   ```java
   @ApiResponse(
       responseCode = "200",
       content = @Content(
           examples = @ExampleObject(
               name = "Success Response",
               value = "{\"message\": \"Password changed successfully\"}"
           )
       )
   )
   ```

---

## 🎨 Customization

### Adding new error types

Edit `ApiResponseExamples.java`:

```java
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(
    responseCode = "422",
    description = "Unprocessable Entity",
    content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ErrorResponse.class),
        examples = @ExampleObject(
            name = "Business Logic Error",
            value = """
            {
              "timestamp": "2026-01-01T14:52:00",
              "status": 422,
              "error": "Unprocessable Entity",
              "message": "Cannot cancel order that is already delivered"
            }
            """
        )
    )
)
public @interface UnprocessableEntity {}
```

---

## 📚 References

- OpenAPI 3.0 Spec: https://swagger.io/specification/
- Swagger Annotations: https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations
- Spring Doc: https://springdoc.org/

---

