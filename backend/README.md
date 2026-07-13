# Foodya Backend

Spring Boot backend for the Foodya food delivery platform — modular monolith, package-by-feature.

Spec: [`docs/FOODYA_SRS.md`](../docs/FOODYA_SRS.md) · Development roadmap: [`ROADMAP.md`](../ROADMAP.md)

## Tech stack

| Component | Technology |
|---|---|
| Runtime | Java 21, Spring Boot 3.3 |
| Database | PostgreSQL 17 (Supabase in prod), Flyway migrations |
| Cache / denylist / ranking | Redis 7 |
| Auth | JWT (24h access + 30d refresh, denylist on logout) |
| API docs | springdoc-openapi (Swagger UI) |

## Running locally

```bash
# 1. Copy the template and fill in real values
cp .env.example .env

# 2. Spin up Postgres + Redis + backend
docker compose up -d

# Or run the backend outside Docker (requires Postgres/Redis already running):
./mvnw spring-boot:run
```

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Health: `http://localhost:8080/actuator/health`

Flyway applies migrations automatically on startup (`src/main/resources/db/migration`). Schema is migration-managed — `ddl-auto=validate`, Hibernate never generates schema.

## Tests

```bash
./mvnw test -Dmaven.test.skip=false
```

## Structure

```
src/main/java/com/foodya/foodya_backend/
├── auth/          # register, login, JWT, logout denylist
├── user/          # user profile
├── restaurant/    # restaurants, menu, categories (public + shared logic)
├── merchant/      # restaurant owner API (manage restaurant/menu/orders)
├── order/         # order placement, order status lifecycle
├── admin/         # admin API (restaurant approval, user management...)
├── cart/ delivery/ payment/ review/ notification/   # planned per roadmap, not yet implemented
└── shared/        # config, security, exception handling, response envelope, Redis keys
```

Module rule: cross-module calls go through the other module's `*Service`, never its `*Repository`/`*Model` directly (SRS §3.2).

## Conventions

- Money is stored as integers (smallest currency unit); **the server computes every money value** — never trust the client
- Every response is wrapped in `ApiResponse<T>` with a `traceId` (tracked via MDC)
- New endpoints need an ownership check + Swagger annotations + a test written alongside them (see Definition of Done in ROADMAP.md)
