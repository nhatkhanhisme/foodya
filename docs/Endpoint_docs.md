## restaurants
# 1. Lấy tất cả restaurants (có pagination)
GET /api/v1/restaurants? page=0&size=20&sortBy=popular

# 2. Tìm kiếm theo keyword
GET /api/v1/restaurants?keyword=pizza&page=0&size=20

# 3. Lọc theo cuisine
GET /api/v1/restaurants?cuisine=Italian&page=0&size=20

# 4. Lọc theo rating
GET /api/v1/restaurants?minRating=4.0&page=0&size=20

# 5. Kết hợp nhiều filters
GET /api/v1/restaurants?keyword=pizza&cuisine=Italian&minRating=4.0&sortBy=rating&page=0&size=20

# 6. Lấy chi tiết restaurant
GET /api/v1/restaurants/{id}

# 7. Lấy top 10 popular restaurants
GET /api/v1/restaurants/popular? limit=10


## menuitems
### Mobile App APIs (public/authenticated)
# 1. Lấy menu items với pagination
GET /api/v1/restaurants/{restaurantId}/menu-items?page=0&size=20&sortBy=name&sortDirection=asc

# 2. Lấy tất cả menu items
GET /api/v1/restaurants/{restaurantId}/menu-items/all

# 3. Chi tiết 1 món
GET /api/v1/restaurants/{restaurantId}/menu-items/{menuItemId}

# 4. Tìm kiếm món
GET /api/v1/restaurants/{restaurantId}/menu-items/search?keyword=pizza

# 5. Lọc theo category
GET /api/v1/restaurants/{restaurantId}/menu-items/category/Main Course

# 6. Món phổ biến
GET /api/v1/restaurants/{restaurantId}/menu-items/popular?limit=10

# 7. Lọc dietary
GET /api/v1/restaurants/{restaurantId}/menu-items/dietary?vegetarian=true&vegan=false

### Merchant APIs (Requires MERCHANT or ADMIN role)
# 1. Tạo món mới
POST /api/v1/merchant/restaurants/{restaurantId}/menu-items
Body: { "name": "Margherita Pizza", "price": 120000, ...  }

# 2. Cập nhật món
PUT /api/v1/merchant/restaurants/{restaurantId}/menu-items/{menuItemId}
Body: { "name": "Margherita Pizza Premium", "price": 150000, ... }

# 3. Xóa món (soft delete)
DELETE /api/v1/merchant/restaurants/{restaurantId}/menu-items/{menuItemId}

# 4. Bật/tắt món
PATCH /api/v1/merchant/restaurants/{restaurantId}/menu-items/{menuItemId}/toggle-availability

### ADMIN APIs (Requires ADMIN role)
# Xóa vĩnh viễn món
DELETE /api/v1/admin/restaurants/{restaurantId}/menu-items/{menuItemId}/hard

### Public APIs Endpoints
GET /api/v1/auth/**                              → Auth endpoints
GET /api/v1/restaurants/**                       → Browse restaurants
GET /api/v1/restaurants/{id}/menu-items/**       → Browse menu items

### Authenticated APIs Endpoints
PUT /api/v1/users/me
PUT /api/v1/users/me

### Merchant APIs Endpoints
POST   /api/v1/merchant/restaurants/{id}/menu-items
PUT    /api/v1/merchant/restaurants/{id}/menu-items/{itemId}
DELETE /api/v1/merchant/restaurants/{id}/menu-items/{itemId}
PATCH  /api/v1/merchant/restaurants/{id}/menu-items/{itemId}/toggle-availability


### Admin APIs Endpoints
GET    /api/v1/admin/users
DELETE /api/v1/admin/users/{userId}
PATCH  /api/v1/admin/users/{userId}/toggle-active

GET    /api/v1/admin/restaurants
DELETE /api/v1/admin/restaurants/{id}

DELETE /api/v1/admin/restaurants/{id}/menu-items/{itemId}/hard
