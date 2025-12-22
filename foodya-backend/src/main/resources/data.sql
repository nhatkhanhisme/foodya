-- ================================================
-- FOODYA DATABASE SEED DATA (PostgreSQL with UUID)
-- ================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Insert Users first (UUID will be auto-generated)
-- We'll use gen_random_uuid() to create specific UUIDs we can reference
INSERT INTO users (id, username, email, password, full_name, role, phone_number, is_phone_number_verified, is_active, is_email_verified, account_locked, created_at, updated_at)
VALUES
(gen_random_uuid(), 'admin', 'admin@foodya.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 'System Admin', 'ADMIN', '+84901111111', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()),
(gen_random_uuid(), 'merchant1', 'merchant1@foodya.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 'Restaurant Owner 1', 'MERCHANT', '+84902222222', TRUE, TRUE, TRUE, FALSE, NOW(), NOW());

-- Insert Restaurants (using a user that exists)
-- Since UUIDs are random, we use a subquery to get any user's ID
INSERT INTO restaurants (id, name, address, phone_number, description, cuisine, rating, total_reviews, is_open, is_active, opening_time, closing_time, delivery_fee, estimated_delivery_time, owner_id, created_at, updated_at)
VALUES
(gen_random_uuid(), 'The Italian Corner', '123 Nguyen Hue Boulevard, District 1, Ho Chi Minh City', '+84901234567', 'Authentic Italian cuisine', 'Italian', 4.5, 120, TRUE, TRUE, '09:00', '22:00', 2.5, 30, (SELECT id FROM users WHERE role = 'MERCHANT' LIMIT 1), NOW(), NOW()),
(gen_random_uuid(), 'Pho Saigon', '456 Le Loi Street, District 3, Ho Chi Minh City', '+84907654321', 'Traditional Vietnamese Pho', 'Vietnamese', 4.7, 200, TRUE, TRUE, '06:00', '23:00', 1.5, 25, (SELECT id FROM users WHERE role = 'MERCHANT' LIMIT 1), NOW(), NOW());

-- Insert Menu Items for Italian Restaurant
INSERT INTO menu_items (id, restaurant_id, name, description, price, category, is_available, is_active, preparation_time, calories, is_vegetarian, is_vegan, is_gluten_free, is_spicy, order_count, created_at, updated_at)
VALUES
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'The Italian Corner'), 'Margherita Pizza', 'Classic pizza with tomato, mozzarella, and basil', 12.99, 'Main Course', TRUE, TRUE, 15, 800, TRUE, FALSE, FALSE, FALSE, 45, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'The Italian Corner'), 'Spaghetti Carbonara', 'Creamy pasta with bacon and parmesan', 14.99, 'Main Course', TRUE, TRUE, 20, 950, FALSE, FALSE, FALSE, FALSE, 67, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'The Italian Corner'), 'Tiramisu', 'Traditional Italian dessert', 6.99, 'Dessert', TRUE, TRUE, 5, 450, TRUE, FALSE, FALSE, FALSE, 32, NOW(), NOW());

-- Insert Menu Items for Vietnamese Restaurant
INSERT INTO menu_items (id, restaurant_id, name, description, price, category, is_available, is_active, preparation_time, calories, is_vegetarian, is_vegan, is_gluten_free, is_spicy, order_count, created_at, updated_at)
VALUES
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Pho Saigon'), 'Pho Bo', 'Vietnamese beef noodle soup', 8.99, 'Main Course', TRUE, TRUE, 10, 600, FALSE, FALSE, TRUE, FALSE, 150, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Pho Saigon'), 'Banh Mi Thit', 'Vietnamese sandwich with pork', 4.99, 'Main Course', TRUE, TRUE, 8, 500, FALSE, FALSE, FALSE, FALSE, 89, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Pho Saigon'), 'Ca Phe Sua Da', 'Vietnamese iced coffee', 3.50, 'Beverage', TRUE, TRUE, 3, 150, TRUE, FALSE, TRUE, FALSE, 112, NOW(), NOW());
