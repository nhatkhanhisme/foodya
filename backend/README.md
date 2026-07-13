# Foodya Backend

Spring Boot backend cho nền tảng giao đồ ăn Foodya — modular monolith, package-by-feature.

Tài liệu đặc tả: [`docs/FOODYA_SRS.md`](../docs/FOODYA_SRS.md) · Lộ trình phát triển: [`ROADMAP.md`](../ROADMAP.md)

## Tech stack

| Thành phần | Công nghệ |
|---|---|
| Runtime | Java 21, Spring Boot 3.3 |
| Database | PostgreSQL 17 (Supabase ở prod), Flyway migrations |
| Cache / denylist / ranking | Redis 7 |
| Auth | JWT (access 24h + refresh 30d, denylist khi logout) |
| API docs | springdoc-openapi (Swagger UI) |

## Chạy local

```bash
# 1. Tạo file cấu hình từ template rồi điền giá trị
cp .env.example .env

# 2. Dựng Postgres + Redis + backend
docker compose up -d

# Hoặc chạy backend ngoài Docker (cần Postgres/Redis đang chạy):
./mvnw spring-boot:run
```

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Health: `http://localhost:8080/actuator/health`

Flyway tự chạy migration khi khởi động (`src/main/resources/db/migration`). Schema do migration quản lý — `ddl-auto=validate`, không để Hibernate sinh schema.

## Test

```bash
./mvnw test -Dmaven.test.skip=false
```

## Cấu trúc

```
src/main/java/com/foodya/foodya_backend/
├── auth/          # đăng ký, đăng nhập, JWT, logout denylist
├── user/          # hồ sơ người dùng
├── restaurant/    # nhà hàng, menu, category (public + nghiệp vụ chung)
├── merchant/      # API cho chủ nhà hàng (quản lý nhà hàng/menu/đơn)
├── order/         # đặt đơn, vòng đời trạng thái đơn
├── admin/         # API quản trị (duyệt nhà hàng, quản lý user...)
├── cart/ delivery/ payment/ review/ notification/   # theo roadmap, chưa triển khai
└── shared/        # config, security, exception handler, response envelope, Redis keys
```

Quy tắc module: gọi chéo qua `*Service` của module khác, không đụng trực tiếp `*Repository`/`*Model` (SRS §3.2).

## Quy ước

- Tiền lưu số nguyên (đơn vị nhỏ nhất), **server tính mọi giá trị tiền** — không nhận từ client
- Mọi response bọc trong `ApiResponse<T>` kèm `traceId` (theo dấu log qua MDC)
- Endpoint mới phải có ownership check + Swagger annotation + test ngay khi viết (xem Definition of Done trong ROADMAP.md)
