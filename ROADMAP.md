# Foodya Development Roadmap

Kế hoạch phát triển tiếp backend từ trạng thái hiện tại đến app hoàn thiện theo SRS.

- **SRS:** [`docs/FOODYA_SRS.md`](docs/FOODYA_SRS.md) — 34 UC, 33 BR
- **Nguyên tắc:** mỗi phase kết thúc bằng một luồng end-to-end chạy được + có test; UC chỉ DONE khi đủ Definition of Done (cuối file)
- **Hiện trạng (audit 2026-07):** 8 UC DONE, 9 PARTIAL, 16 NOT_STARTED — chi tiết trong bảng từng phase

---

## Hiện trạng module

| Module | Trạng thái |
|---|---|
| auth, user | ✅ Hoàn thiện (C01, C02, C11–C13; BR-01/03/04/30/33) |
| restaurant, merchant, admin (restaurant/menu/user) | ✅ Phần lớn (R01–R03, A02, C03, C04; BR-10/17/31) |
| order | ⚠️ Partial — thiếu nhiều (xem Phase 1) |
| cart, delivery, payment, review, notification | ❌ Rỗng (schema DB đã có sẵn V2–V14) |
| Redis | ✅ Blacklist, rate-limit, cache, popularity ranking |

---

## Phase 1 — Trả nợ order core (~3–5 ngày) 🔴 làm trước mọi feature mới

Các lỗ hổng đã biết trong code hiện có — càng để lâu càng khó sửa vì feature mới xây chồng lên:

- [x] **1.1 Server tính tiền** — `OrderCommandService.createOrder` đang nhận `deliveryFee` từ client. Bỏ field khỏi `OrderRequest`; server tính (tạm: hằng số/công thức đơn giản BR-07, config qua properties). Không bao giờ tin giá từ client.
- [x] **1.2 Validate BR-06 khi đặt đơn** — check restaurant `APPROVED` + `isOpen` trước khi tạo order (hiện chỉ check menu item available).
- [x] **1.3 Ownership check merchant order** — `MerchantOrderController` hiện cho owner bất kỳ xem/sửa đơn của nhà hàng khác. Check `restaurant.ownerId == currentUser` ở cả 3 endpoint (dùng `OwnershipService` có sẵn).
- [x] **1.4 Bỏ/khóa hard-delete vi phạm SRS** — `DELETE /admin/users/{id}` (BR-30 cấm xóa user), `DELETE /admin/orders/{id}`, `DELETE /admin/restaurants/{id}`. Xóa endpoint hoặc chuyển thành soft (BANNED/CANCELLED/SUSPENDED).
- [x] **1.5 Reject kèm reason** — R05/A02 yêu cầu reason khi reject order/restaurant; thêm request body + lưu vào entity.
- [x] **1.6 Test cho order state machine** — transition map, isCancellable, timestamps (đã có logic, chưa có test nào).

✅ **Phase DONE khi:** không còn endpoint nào tin dữ liệu tiền từ client, không owner nào đụng được resource người khác, test order model xanh.

## Phase 2 — Address + Cart (~4–5 ngày)

UC: **C05, C06** · BR: **05, 22** · Schema có sẵn: `V2__create_addresses`, `V7__create_carts`

- [ ] 2.1 Address CRUD (`address` entity + controller trong module user hoặc riêng) — verify CHECK constraint tọa độ (BR-22, đã có trong V2)
- [ ] 2.2 Cart model + service: 1 cart / 1 restaurant (BR-05); thêm item khác nhà hàng → 409 báo clear cart
- [ ] 2.3 Item unavailable được flag khi xem cart/checkout, không âm thầm xóa (C05 alt flow)
- [ ] 2.4 Checkout đọc từ cart thay vì items trong request body; `deliveryAddressId` thay cho chuỗi address tự do

✅ **Phase DONE khi:** luồng thêm món → xem cart → đổi nhà hàng bị chặn → checkout từ cart chạy trọn, có test.

## Phase 3 — Hoàn thiện owner flow (~2–3 ngày)

UC: **R04 (lọc), R05 (window), R07** · BR: **11, 12**

- [ ] 3.1 R04 lọc đơn `PENDING` (hiện trả tất cả), sort mới nhất trước
- [ ] 3.2 `@Scheduled` sweep: PENDING quá window (BR-11, config properties, default 5 phút) → auto-cancel `RESTAURANT_TIMEOUT` (BR-12)
- [ ] 3.3 Dashboard thô R07: đơn hôm nay, doanh thu hôm nay (đã có `calculateRevenue`)

## Phase 4 — Shipper + Delivery (~1 tuần)

UC: **S01–S07, A03** · BR: **13, 14, 17, 28, 32** · Schema: `V3__shipper_profiles`, `V11__delivery_tracking`

- [ ] 4.1 ShipperProfile entity + đăng ký shipper (User + profile cùng transaction, BR-32); admin approve/reject (A03 + AuditLog)
- [ ] 4.2 Job board: đơn `READY_FOR_PICKUP` chưa có shipper (S02)
- [ ] 4.3 Accept job — optimistic lock `@Version` đã có sẵn trên Order (BR-13); **test 2 thread cùng accept**: 1 thắng, 1 nhận `JOB_ALREADY_TAKEN`
- [ ] 4.4 Update location → DeliveryTracking (S04); customer xem qua C08
- [ ] 4.5 Picked up / Delivered (S05, S06) — DELIVERED flip Payment COD → `SUCCESS` (BR-28; cần Payment row từ Phase 5 hoặc tạo stub trước)
- [ ] 4.6 Delivery exception (BR-14): reason code cấu trúc, admin notify
- [ ] 4.7 Earnings (S07)

✅ **Phase DONE khi:** vòng đời COD trọn vẹn: đặt → confirm → ready → accept → picked up → delivered.

## Phase 5 — Payment (~1 tuần, khó nhất)

UC: **C14, sửa C07/C09** · BR: **23–29** · Schema: `V10__create_payments`

- [ ] 5.1 Payment entity 1:1 Order (BR-28) — tạo row COD ngay khi checkout (sửa Phase 2.4)
- [ ] 5.2 Interface `PaymentProvider` + adapter đầu tiên (VNPay sandbox hoặc mock); order online bắt đầu `AWAITING_PAYMENT` (BR-23), ẩn khỏi R04
- [ ] 5.3 Webhook: verify signature (BR-26), idempotent bằng **unique constraint `provider_txn_ref` trong Postgres** (không dùng Redis cho việc này)
- [ ] 5.4 Success → `PENDING` + notify nhà hàng; fail → `CANCELLED` `PAYMENT_FAILED` (BR-24); không retry cùng order (BR-29)
- [ ] 5.5 `@Scheduled` timeout 15 phút → `PAYMENT_TIMEOUT` (BR-24)
- [ ] 5.6 Refund best-effort khi hủy online trước PICKED_UP (BR-25); cancel flow C09 cập nhật theo
- [ ] 5.7 Test webhook: gửi trùng 2 lần, sai signature, out-of-order — đều ra đúng trạng thái

## Phase 6 — Review (~2–3 ngày)

UC: **C10** · BR: **15, 16** · Schema: `V12__create_reviews`

- [ ] 6.1 Tạo review: 1 review/order, chỉ khi DELIVERED (BR-15) — test cả 2 chặn
- [ ] 6.2 Recalculate `rating_avg` (BR-16) — cân nhắc evict cache restaurant khi update
- [ ] 6.3 Public list reviews theo nhà hàng

## Phase 7 — Admin còn lại + Notification (~1 tuần)

UC: **A01, A04–A06** · BR: **02 ✅, 09, 20** · Schema: `V13__notifications`, `V14__audit_logs`

- [ ] 7.1 AuditLog entity + ghi mọi hành động moderation (BR-20) — **bổ sung ngược** cho approve/reject/suspend/ban đã có
- [ ] 7.2 A01: search/filter/paging users, ban kèm reason
- [ ] 7.3 BR-09: đếm cancel CONFIRMED trong 30 ngày, flag cho admin (A06)
- [ ] 7.4 A06 disputes: xem flagged orders/reviews, refund, gỡ review, warn/ban
- [ ] 7.5 A04 analytics mở rộng + A05 view config (đọc properties)
- [ ] 7.6 Notification module: bảng + API đọc/đánh dấu đã đọc; notify các sự kiện chính (order confirmed/cancelled, restaurant approved...)

## Phase 8 — Hardening trước khi "ship" (~3–4 ngày)

- [ ] 8.1 Login bằng **email** thay username (SRS §6.1 — hiện đang sai)
- [ ] 8.2 Integration test suite Testcontainers cho các luồng chính (checkout, accept job, webhook)
- [ ] 8.3 Rà lại toàn bộ alternative flows trong SRS §4 — mục "Alternative:" từng UC
- [ ] 8.4 Load nhẹ: N+1 query check (bật `LOG_HIBERNATE_SQL=DEBUG` chạy các luồng chính)
- [ ] 8.5 Swagger final pass + README hướng dẫn chạy từ zero (`docker compose up` + `.env.example`)

---

## Nguyên tắc xuyên suốt

1. **Tiền do server tính** — không bao giờ nhận giá/phí từ client
2. **Ownership check ngay khi viết endpoint** — resource này của user đang gọi không?
3. **Mỗi BR có logic = ít nhất 1 test** lúc viết ra nó
4. **Migration đi cùng feature** — schema V2–V14 có sẵn nhưng entity phải khớp thật (`ddl-auto=validate` sẽ bắt); cần đổi schema thì thêm V16+, không sửa file cũ
5. **Không viết code "cho tương lai"** — security rule/config chỉ tồn tại khi có endpoint thật dùng

## Definition of Done cho mỗi UC

1. Main flow đúng SRS · 2. Validation đầy đủ · 3. **Alternative flows** xử lý được · 4. BR liên quan enforce · 5. Có test · 6. Swagger đúng thực tế

## Nhật ký tiến độ

| Ngày | Phase/Bước | Ghi chú |
|---|---|---|
| 2026-07-11 | (pre) | Redis integration, security/swagger/config fixes, Flyway PG17, OSIV off |
| 2026-07-13 | Phase 1 ✅ | Server tính fee (dùng restaurant.deliveryFee + freeDeliveryThreshold + minimumOrder), BR-06 check, ownership merchant orders + whitelist status, gỡ 3 hard-delete, reject kèm reason (V16), 33 test order model. Bonus: fix bug @Builder.Default version=0 làm save() đi đường merge → subtotal/totalItems ghi 0; map 3 exception thiếu trong GlobalExceptionHandler (400/405/404 thay vì 500) |
| | | |
