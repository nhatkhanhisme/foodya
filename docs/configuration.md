# Configuration Guide - Foodya Backend

Hướng dẫn cấu hình ứng dụng Foodya Backend với environment variables.

---

## 📋 Tổng quan

Ứng dụng sử dụng **environment variables** để quản lý cấu hình, giúp:
- ✅ Bảo mật credentials (không hardcode trong code)
- ✅ Dễ dàng deploy trên các môi trường khác nhau
- ✅ Quản lý tập trung tại file `.env`

---

## 🚀 Quick Setup

### Bước 1: Tạo file .env

```bash
# Tại thư mục root project: /home/debian/dev/project/foodya/
cp .env.example .env
```

### Bước 2: Điền thông tin vào .env

Mở file `.env` và cập nhật các giá trị:

```properties
# Database - Giữ nguyên config Supabase hiện tại
SPRING_DATASOURCE_URL=jdbc:postgresql://aws-1-ap-northeast-1.pooler.supabase.com:6543/postgres?sslmode=require
SPRING_DATASOURCE_USERNAME=postgres.rxifptroexopdnqtxjnk
SPRING_DATASOURCE_PASSWORD=Foodya_db123456

# JWT - Có thể giữ nguyên hoặc generate mới
SPRING_JWT_SECRET_KEY=5e9AfqvyHlRiU88gj7JzCvuhSiX98466B9kiDbpvbg0=
SPRING_JWT_SECRET_KEY_EXPIRATION=86400000
REFRESH_TOKEN_EXPIRATION=2592000000
JWT_PASSWORD_SECRET=your-password-reset-secret
RESET_PASSWORD_TOKEN_EXPIRATION=15

# Email (optional - bỏ qua nếu chưa cần)
SUPPORT_EMAIL=
APP_PASSWORD=

# Frontend URL
FRONTEND_URL=http://localhost:3000
```

### Bước 3: Chạy ứng dụng

```bash
cd backend
./mvnw spring-boot:run
```

---

## 🔧 Chi tiết Environment Variables

### Database Configuration

| Variable | Description | Example |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | JDBC URL của database | `jdbc:postgresql://host:port/dbname?sslmode=require` |
| `SPRING_DATASOURCE_USERNAME` | Username database | `postgres.project_id` |
| `SPRING_DATASOURCE_PASSWORD` | Password database | `your_password` |

**Lưu ý:**
- Đang dùng **PostgreSQL** / **Supabase**
- Driver tự động detect từ URL
- HikariCP connection pool đã được config sẵn

---

### JWT Configuration

| Variable | Description | Default | Recommended |
|----------|-------------|---------|-------------|
| `SPRING_JWT_SECRET_KEY` | Secret key cho JWT signing | - | Generate mới cho production |
| `SPRING_JWT_SECRET_KEY_EXPIRATION` | Access token expiration (ms) | 86400000 | 24 hours |
| `REFRESH_TOKEN_EXPIRATION` | Refresh token expiration (ms) | 2592000000 | 30 days |
| `JWT_PASSWORD_SECRET` | Secret cho password reset token | - | Generate riêng |
| `RESET_PASSWORD_TOKEN_EXPIRATION` | Password reset expiration (min) | 15 | 15 minutes |

**Cách generate JWT secret mới:**

```bash
# Option 1: OpenSSL
openssl rand -base64 32

# Option 2: Online tool
# https://generate-secret.vercel.app/32

# Option 3: Node.js
node -e "console.log(require('crypto').randomBytes(32).toString('base64'))"
```

**⚠️ Quan trọng:**
- **KHÔNG** sử dụng secret mặc định trong production
- Mỗi environment nên có secret riêng
- Không share secret giữa các môi trường

---

### Email Configuration (Optional)

Nếu cần gửi email (password reset, notifications, etc.):

| Variable | Description | Example |
|----------|-------------|---------|
| `SUPPORT_EMAIL` | Email gửi đi | `support@foodya.com` |
| `APP_PASSWORD` | Gmail App Password | `xxxx xxxx xxxx xxxx` |

**Cách setup Gmail SMTP:**

1. **Bật 2-Factor Authentication** cho Gmail
   - Vào: https://myaccount.google.com/security
   - Enable 2FA

2. **Tạo App Password**
   - Vào: https://myaccount.google.com/apppasswords
   - Chọn: Mail > Your Device
   - Copy password (16 ký tự)

3. **Cập nhật .env**
   ```properties
   SUPPORT_EMAIL=your-email@gmail.com
   APP_PASSWORD=xxxx xxxx xxxx xxxx
   ```

4. **Uncomment trong application.properties**
   ```properties
   # Uncomment các dòng này:
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=${SUPPORT_EMAIL}
   spring.mail.password=${APP_PASSWORD}
   # ... (các dòng mail config khác)
   ```

---

### Frontend URL

| Variable | Description | Example |
|----------|-------------|---------|
| `FRONTEND_URL` | URL frontend app | `http://localhost:3000` (dev)<br>`https://foodya.com` (prod) |

Dùng cho:
- CORS configuration
- Email templates (links)
- Redirect URLs

---

## 📂 File Structure

```
foodya/                              # Root project
├── .env                             # ⚠️ Environment variables (KHÔNG commit)
├── .env.example                     # ✅ Template (commit được)
├── .gitignore                       # Phải chứa .env
└── backend/
    └── src/main/resources/
        └── application.properties   # Đọc từ ../.env
```

**application.properties imports .env:**
```properties
spring.config.import=optional:file:../.env[.properties]
```

Path `../` nghĩa là lên 1 cấp từ `backend/` → root project

---

## 🔒 Security Best Practices

### 1. Bảo vệ file .env

```bash
# Đảm bảo .env trong .gitignore
echo ".env" >> .gitignore
echo ".env.local" >> .gitignore
echo ".env.*.local" >> .gitignore

# Verify không bị track
git status | grep .env
# Không nên thấy output
```

### 2. Permissions

```bash
# Set permissions cho .env
chmod 600 .env  # Only owner can read/write
```

### 3. Different Secrets per Environment

| Environment | Secret Strategy |
|-------------|-----------------|
| Development | Simple/reusable secrets OK |
| Staging | Generate unique secrets |
| Production | **MUST** use unique, strong secrets |

### 4. Rotate Secrets Regularly

- Change JWT secrets mỗi 3-6 tháng
- Change database passwords mỗi 6-12 tháng
- Rotate email passwords khi cần

---

## 🧪 Testing Configuration

### Test 1: Environment Variables Loading

```bash
# Start app và check logs
./mvnw spring-boot:run

# Verify: Không có lỗi "Could not resolve placeholder"
# Verify: "Started FoodyaBackendApplication"
```

### Test 2: Database Connection

```bash
# Check logs khi startup
# Should see: "HikariPool-1 - Start completed"

# Test health endpoint
curl http://localhost:8080/actuator/health

# Expected: {"status":"UP"}
```

### Test 3: JWT Configuration

```bash
# Test register/login
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Test123!@#",
    "fullName": "Test User",
    "phoneNumber": "+84987654321",
    "role": "CUSTOMER"
  }'

# Verify: Nhận được accessToken và refreshToken
```

### Test 4: Email (nếu có enable)

Test bằng cách trigger password reset hoặc welcome email.

---

## 🐛 Troubleshooting

### Lỗi: "Could not resolve placeholder"

**Nguyên nhân:** Biến không tồn tại trong `.env`

**Giải pháp:**
```bash
# Check .env có đầy đủ variables
cat .env

# So sánh với .env.example
diff .env .env.example
```

### Lỗi: Database connection failed

**Nguyên nhân:** URL/credentials sai

**Giải pháp:**
```bash
# Verify database URL
echo $SPRING_DATASOURCE_URL

# Test connection manually
psql "$SPRING_DATASOURCE_URL"
```

### Lỗi: JWT token invalid

**Nguyên nhân:** Secret key không khớp hoặc đổi giữa chừng

**Giải pháp:**
- Đảm bảo `SPRING_JWT_SECRET_KEY` không thay đổi khi app đang chạy
- Client phải login lại khi secret thay đổi

### Lỗi: Email không gửi được

**Nguyên nhân:**
- Chưa uncomment config trong `application.properties`
- App Password sai
- 2FA chưa enable

**Giải pháp:**
1. Verify Gmail 2FA enabled
2. Generate App Password mới
3. Uncomment email config trong `application.properties`
4. Restart app

---

## 📝 Migration from Old Config

Nếu bạn đang upgrade từ config cũ:

### Before (hardcoded)
```properties
spring.datasource.url=jdbc:postgresql://...
spring.datasource.password=Foodya_db123456
app.jwt.secret=5e9AfqvyHlRiU88gj7JzCvuhSiX98466B9kiDbpvbg0=
```

### After (environment variables)
```properties
spring.config.import=optional:file:../.env[.properties]
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
app.jwt.secret=${SPRING_JWT_SECRET_KEY}
```

**Migration steps:**
1. ✅ Tạo `.env` từ `.env.example`
2. ✅ Copy giá trị hiện tại vào `.env`
3. ✅ Update `application.properties`
4. ✅ Test app startup
5. ✅ Verify tất cả APIs hoạt động

---

## 🚀 Production Deployment

### Docker

```dockerfile
# Dockerfile
FROM openjdk:21-jdk-slim
COPY .env /app/.env
COPY backend/target/*.jar /app/app.jar
WORKDIR /app
CMD ["java", "-jar", "app.jar"]
```

### Docker Compose

```yaml
services:
  backend:
    build: .
    env_file:
      - .env
    ports:
      - "8080:8080"
```

### Cloud Platforms

Các platform thường hỗ trợ environment variables:

- **Heroku**: Settings → Config Vars
- **AWS**: Elastic Beanstalk Environment Properties
- **Google Cloud**: Cloud Run Environment Variables
- **Azure**: App Service Configuration

---

## ✅ Checklist trước khi deploy

- [ ] Generate JWT secrets mới cho production
- [ ] Update database credentials
- [ ] Set `server.error.include-stacktrace=never`
- [ ] Set `spring.jpa.show-sql=false`
- [ ] Configure email (nếu cần)
- [ ] Test health endpoint
- [ ] Verify CORS configuration
- [ ] Backup `.env` ra nơi an toàn

---

## 📞 Support

Nếu gặp vấn đề, tham khảo:
- [Quick Start Guide](./quick-start.md)
- [Error Handling Guide](./error-handling.md)
- [API Documentation](./README.md)
