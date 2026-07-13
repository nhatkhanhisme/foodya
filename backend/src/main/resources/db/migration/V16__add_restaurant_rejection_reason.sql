-- UC-A02: a rejected restaurant's owner must be told why (and can resubmit — BR-31).
-- Nullable: only populated for REJECTED restaurants.
ALTER TABLE restaurants ADD COLUMN rejection_reason VARCHAR(500);
