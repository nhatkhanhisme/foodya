# Quick Start Guide

Hướng dẫn nhanh để bắt đầu sử dụng Foodya Backend API.

---

## 🚀 Setup & Installation

### Prerequisites

- Java 21 or higher
- PostgreSQL 14+
- Maven 3.8+

### Installation Steps

1. **Clone repository**
   ```bash
   git clone <repository-url>
   cd foodya-backend
   ```

2. **Configure database**

   Tạo file `src/main/resources/application.properties`:
   ```properties
   # Database Configuration
   spring.datasource.url=jdbc:postgresql://localhost:5432/foodya_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password

   # JPA/Hibernate
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.format_sql=true

   # JWT Configuration
   jwt.secret=your-256-bit-secret-key-here
   jwt.expiration=86400000
   jwt.refresh-expiration=2592000000

   # Server Configuration
   server.port=8080
   ```

3. **Build & Run**
   ```bash
   # Build
   ./mvnw clean install

   # Run
   ./mvnw spring-boot:run
   ```

4. **Verify**

   Truy cập Swagger UI:
   ```
   http://localhost:8080/swagger-ui.html
   ```

---

## 📱 Mobile App Integration Guide

### Step 1: Authentication Flow

```javascript
// 1. Đăng ký tài khoản
const registerUser = async (userData) => {
  const response = await fetch('http://localhost:8080/api/v1/auth/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      username: userData.username,
      email: userData.email,
      password: userData.password,
      fullName: userData.fullName,
      phoneNumber: userData.phoneNumber,
      role: "CUSTOMER"
    }),
  });

  const data = await response.json();
  // Lưu tokens
  localStorage.setItem('accessToken', data.accessToken);
  localStorage.setItem('refreshToken', data.refreshToken);
  return data;
};

// 2. Đăng nhập
const login = async (username, password) => {
  const response = await fetch('http://localhost:8080/api/v1/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password }),
  });

  const data = await response.json();
  localStorage.setItem('accessToken', data.accessToken);
  localStorage.setItem('refreshToken', data.refreshToken);
  return data;
};

// 3. Refresh token khi hết hạn
const refreshToken = async () => {
  const refreshToken = localStorage.getItem('refreshToken');
  const response = await fetch('http://localhost:8080/api/v1/auth/refresh', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ refreshToken }),
  });

  const data = await response.json();
  localStorage.setItem('accessToken', data.accessToken);
  return data.accessToken;
};
```

### Step 2: Browse Restaurants

```javascript
// Tìm kiếm nhà hàng
const searchRestaurants = async (keyword = '', page = 0) => {
  const params = new URLSearchParams({
    keyword,
    page,
    size: 20,
    sortBy: 'popular'
  });

  const response = await fetch(
    `http://localhost:8080/api/v1/restaurants?${params}`
  );
  return await response.json();
};

// Lấy chi tiết nhà hàng
const getRestaurant = async (restaurantId) => {
  const response = await fetch(
    `http://localhost:8080/api/v1/restaurants/${restaurantId}`
  );
  return await response.json();
};

// Lấy nhà hàng phổ biến
const getPopularRestaurants = async () => {
  const response = await fetch(
    'http://localhost:8080/api/v1/restaurants/popular?limit=10'
  );
  return await response.json();
};
```

### Step 3: Browse Menu

```javascript
// Lấy menu của nhà hàng
const getMenu = async (restaurantId) => {
  const response = await fetch(
    `http://localhost:8080/api/v1/restaurants/${restaurantId}/menu-items/all`
  );
  return await response.json();
};

// Tìm kiếm món ăn
const searchMenuItems = async (restaurantId, keyword) => {
  const response = await fetch(
    `http://localhost:8080/api/v1/restaurants/${restaurantId}/menu-items/search?keyword=${keyword}`
  );
  return await response.json();
};

// Lọc món theo sở thích
const filterVegetarian = async (restaurantId) => {
  const response = await fetch(
    `http://localhost:8080/api/v1/restaurants/${restaurantId}/menu-items/dietary?vegetarian=true`
  );
  return await response.json();
};
```

### Step 4: Place Order

```javascript
// Tạo đơn hàng
const createOrder = async (orderData, token) => {
  const response = await fetch('http://localhost:8080/api/v1/customers/orders', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      customerId: orderData.customerId,
      restaurantId: orderData.restaurantId,
      items: orderData.items.map(item => ({
        menuItemId: item.id,
        quantity: item.quantity,
        specialInstructions: item.notes || ''
      })),
      deliveryAddress: orderData.address,
      deliveryFee: orderData.deliveryFee,
      orderNotes: orderData.notes
    }),
  });

  return await response.json();
};

// Theo dõi đơn hàng
const trackOrder = async (token) => {
  const response = await fetch(
    'http://localhost:8080/api/v1/customers/orders/me/active',
    {
      headers: { 'Authorization': `Bearer ${token}` },
    }
  );
  return await response.json();
};

// Hủy đơn hàng
const cancelOrder = async (orderId, reason, token) => {
  const url = `http://localhost:8080/api/v1/customers/orders/${orderId}/cancel?reason=${encodeURIComponent(reason)}`;
  const response = await fetch(url, {
    method: 'PATCH',
    headers: { 'Authorization': `Bearer ${token}` },
  });
  return await response.json();
};
```

---

## 🏪 Merchant Integration Guide

### Step 1: Setup Restaurant

```javascript
// Merchant đăng ký
const registerMerchant = async (userData) => {
  const response = await fetch('http://localhost:8080/api/v1/auth/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      ...userData,
      role: "MERCHANT"  // Quan trọng!
    }),
  });
  return await response.json();
};

// Tạo nhà hàng
const createRestaurant = async (restaurantData, token) => {
  const response = await fetch('http://localhost:8080/api/v1/merchant/restaurants', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(restaurantData),
  });
  return await response.json();
};
```

### Step 2: Manage Menu

```javascript
// Thêm món ăn
const addMenuItem = async (restaurantId, menuItem, token) => {
  const response = await fetch(
    `http://localhost:8080/api/v1/merchant/restaurants/${restaurantId}/menu-items`,
    {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(menuItem),
    }
  );
  return await response.json();
};

// Toggle món hết hàng
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

### Step 3: Process Orders

```javascript
// Lấy đơn hàng mới
const getNewOrders = async (restaurantId, token) => {
  const orders = await fetch(
    `http://localhost:8080/api/v1/merchant/orders/restaurant/${restaurantId}`,
    {
      headers: { 'Authorization': `Bearer ${token}` },
    }
  ).then(r => r.json());

  return orders.filter(order => order.status === 'PENDING');
};

// Xử lý đơn hàng
const processOrder = async (orderId, token) => {
  // 1. Chấp nhận đơn
  await updateOrderStatus(orderId, 'PREPARING', token);

  // 2. Sau khi chuẩn bị xong
  await updateOrderStatus(orderId, 'SHIPPING', token);

  // 3. Sau khi giao thành công
  await updateOrderStatus(orderId, 'DELIVERED', token);
};

const updateOrderStatus = async (orderId, status, token) => {
  const response = await fetch(
    `http://localhost:8080/api/v1/merchant/orders/${orderId}/status?status=${status}`,
    {
      method: 'PATCH',
      headers: { 'Authorization': `Bearer ${token}` },
    }
  );
  return await response.json();
};
```

---

## 🧪 Testing with Postman

### Import Collection

1. Tạo Postman Collection mới
2. Add Environment:
   ```json
   {
     "baseUrl": "http://localhost:8080/api/v1",
     "accessToken": "",
     "refreshToken": ""
   }
   ```

### Test Authentication

```http
### Register
POST {{baseUrl}}/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "Test123!@#",
  "fullName": "Test User",
  "phoneNumber": "+84987654321",
  "role": "CUSTOMER"
}

### Login
POST {{baseUrl}}/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "Test123!@#"
}

### Use Token (set trong Environment)
### Sau đó gọi protected endpoints:

GET {{baseUrl}}/customers/orders/me
Authorization: Bearer {{accessToken}}
```

---

## 📊 Common Workflows

### Customer Journey

```
1. Register/Login
   ↓
2. Browse Restaurants (search, filter)
   ↓
3. Select Restaurant → View Menu
   ↓
4. Add Items to Cart
   ↓
5. Place Order
   ↓
6. Track Order Status
   ↓
7. (Optional) Cancel Order
```

### Merchant Journey

```
1. Register as Merchant
   ↓
2. Create Restaurant
   ↓
3. Add Menu Items
   ↓
4. Wait for Orders
   ↓
5. Accept Order (PENDING → PREPARING)
   ↓
6. Prepare Food (PREPARING → SHIPPING)
   ↓
7. Complete Delivery (SHIPPING → DELIVERED)
```

---

## 🔍 Debugging Tips

### Check Server Health

```bash
# Health check
curl http://localhost:8080/actuator/health
```

### View Logs

```bash
# Trong terminal đang chạy app, bạn sẽ thấy logs
# Hoặc check file logs nếu có config
tail -f logs/spring-boot-logger.log
```

### Common Issues

1. **Connection refused**: Server chưa chạy
   ```bash
   ./mvnw spring-boot:run
   ```

2. **401 Unauthorized**: Token hết hạn
   - Gọi `/auth/refresh` để lấy token mới

3. **Database error**: Check connection string
   - Verify PostgreSQL đang chạy
   - Check username/password

---

## 📚 Next Steps

1. ✅ Hoàn thành Quick Start
2. 📖 Đọc [API Documentation](./api/README.md)
3. 🔐 Tìm hiểu [Error Handling](./error-handling.md)
4. 🎨 Integrate vào mobile app
5. 🚀 Deploy to production

---

## 💡 Best Practices

1. **Always use HTTPS** in production
2. **Store tokens securely** (không hardcode)
3. **Handle errors gracefully**
4. **Validate input** client-side
5. **Implement retry logic** cho network errors
6. **Cache responses** khi phù hợp
7. **Log important events** để debugging

---

## 🆘 Need Help?

- 📧 Email: support@foodya.com
- 📚 Documentation: [Full API Docs](./README.md)
- 🐛 Issues: GitHub Issues
- 💬 Community: Discord/Slack
