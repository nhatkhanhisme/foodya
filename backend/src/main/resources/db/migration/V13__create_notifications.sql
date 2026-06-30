-- Purpose: In-app/push notification feed (§9: order status, approval results, payment
-- outcomes). No JPA entity exists yet, so this follows the SRS domain model (§6.1, §6.4)
-- directly.
--
-- §6.4: Notification.related_order_id -> Order is SET NULL — a notification stays
-- readable as history even if its related order is later purged; only the link clears.
-- notifications.user_id stays RESTRICT, consistent with the platform-wide rule that
-- users are never hard-deleted (BR-30) — deactivation always goes through
-- users.is_active/account_locked, never row deletion.

CREATE TABLE notifications (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL,
    type                VARCHAR(50) NOT NULL,
    title               VARCHAR(255) NOT NULL,
    message             VARCHAR(1000) NOT NULL,
    is_read             BOOLEAN NOT NULL DEFAULT FALSE,
    related_order_id    UUID,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT fk_notifications_order FOREIGN KEY (related_order_id) REFERENCES orders (id) ON DELETE SET NULL
);

-- Partial index backs the common "unread notifications for me" query (§7.8) without
-- bloating the index with already-read rows.
CREATE INDEX idx_notifications_user_unread ON notifications (user_id, created_at DESC) WHERE is_read = FALSE;
CREATE INDEX idx_notifications_user_id ON notifications (user_id, created_at DESC);
