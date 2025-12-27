-- ================================================
-- FOODYA DATABASE SEED DATA (PostgreSQL with UUID)
-- ================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ================================================
-- TEST ACCOUNTS
-- Password: SecurePass123!
-- BCrypt Hash: $2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH
-- ================================================

-- ADMIN Accounts (3 accounts)
INSERT INTO users (id, username, email, password, full_name, role, phone_number, is_phone_number_verified, is_active, is_email_verified, account_locked, created_at, updated_at)
VALUES
(gen_random_uuid(), 'test.admin.01', 'test.admin.01@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Admin User One', 'ADMIN', '+84900000001', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()),
(gen_random_uuid(), 'test.admin.02', 'test.admin.02@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Admin User Two', 'ADMIN', '+84900000002', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()),
(gen_random_uuid(), 'test.admin.03', 'test.admin.03@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Admin User Three', 'ADMIN', '+84900000003', TRUE, TRUE, TRUE, FALSE, NOW(), NOW());

-- MERCHANT Accounts (8 accounts)
INSERT INTO users (id, username, email, password, full_name, role, phone_number, is_phone_number_verified, is_active, is_email_verified, account_locked, created_at, updated_at)
VALUES
(gen_random_uuid(), 'test.merchant.01', 'test.merchant.01@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Merchant User One', 'MERCHANT', '+84901000001', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()),
(gen_random_uuid(), 'test.merchant.02', 'test.merchant.02@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Merchant User Two', 'MERCHANT', '+84901000002', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()),
(gen_random_uuid(), 'test.merchant.03', 'test.merchant.03@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Merchant User Three', 'MERCHANT', '+84901000003', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()),
(gen_random_uuid(), 'test.merchant.04', 'test.merchant.04@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Merchant User Four', 'MERCHANT', '+84901000004', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()),
(gen_random_uuid(), 'test.merchant.05', 'test.merchant.05@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Merchant User Five', 'MERCHANT', '+84901000005', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()),
(gen_random_uuid(), 'test.merchant.06', 'test.merchant.06@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Merchant User Six', 'MERCHANT', '+84901000006', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()),
(gen_random_uuid(), 'test.merchant.07', 'test.merchant.07@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Merchant User Seven', 'MERCHANT', '+84901000007', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()),
(gen_random_uuid(), 'test.merchant.08', 'test.merchant.08@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Merchant User Eight', 'MERCHANT', '+84901000008', TRUE, TRUE, TRUE, FALSE, NOW(), NOW());

-- CUSTOMER Accounts (12 accounts)
INSERT INTO users (id, username, email, password, full_name, role, phone_number, is_phone_number_verified, is_active, is_email_verified, account_locked, created_at, updated_at)
VALUES
(gen_random_uuid(), 'test.customer.01', 'test.customer.01@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Customer User One', 'CUSTOMER', '+84902000001', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()),
(gen_random_uuid(), 'test.customer.02', 'test.customer.02@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Customer User Two', 'CUSTOMER', '+84902000002', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()),
(gen_random_uuid(), 'test.customer.03', 'test.customer.03@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Customer User Three', 'CUSTOMER', '+84902000003', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()),
(gen_random_uuid(), 'test.customer.04', 'test.customer.04@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Customer User Four', 'CUSTOMER', '+84902000004', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()),
(gen_random_uuid(), 'test.customer.05', 'test.customer.05@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Customer User Five', 'CUSTOMER', '+84902000005', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()),
(gen_random_uuid(), 'test.customer.06', 'test.customer.06@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Customer User Six', 'CUSTOMER', '+84902000006', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()),
(gen_random_uuid(), 'test.customer.07', 'test.customer.07@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Customer User Seven', 'CUSTOMER', '+84902000007', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()),
(gen_random_uuid(), 'test.customer.08', 'test.customer.08@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Customer User Eight', 'CUSTOMER', '+84902000008', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()),
(gen_random_uuid(), 'test.customer.09', 'test.customer.09@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Customer User Nine', 'CUSTOMER', '+84902000009', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()),
(gen_random_uuid(), 'test.customer.10', 'test.customer.10@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Customer User Ten', 'CUSTOMER', '+84902000010', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()),
(gen_random_uuid(), 'test.customer.11', 'test.customer.11@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Customer User Eleven', 'CUSTOMER', '+84902000011', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()),
(gen_random_uuid(), 'test.customer.12', 'test.customer.12@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Customer User Twelve', 'CUSTOMER', '+84902000012', TRUE, TRUE, TRUE, FALSE, NOW(), NOW());

-- DELIVERY Accounts (6 accounts)
INSERT INTO users (id, username, email, password, full_name, role, phone_number, is_phone_number_verified, is_active, is_email_verified, account_locked, created_at, updated_at)
VALUES
(gen_random_uuid(), 'test.delivery.01', 'test.delivery.01@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Delivery User One', 'DELIVERY', '+84903000001', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()),
(gen_random_uuid(), 'test.delivery.02', 'test.delivery.02@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Delivery User Two', 'DELIVERY', '+84903000002', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()),
(gen_random_uuid(), 'test.delivery.03', 'test.delivery.03@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Delivery User Three', 'DELIVERY', '+84903000003', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()),
(gen_random_uuid(), 'test.delivery.04', 'test.delivery.04@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Delivery User Four', 'DELIVERY', '+84903000004', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()),
(gen_random_uuid(), 'test.delivery.05', 'test.delivery.05@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Delivery User Five', 'DELIVERY', '+84903000005', TRUE, TRUE, TRUE, FALSE, NOW(), NOW()),
(gen_random_uuid(), 'test.delivery.06', 'test.delivery.06@example.com', '$2a$10$rJ8S7p5L5Q8vH7Y9F7z7.uVf5J5Y5Q8vH7Y9F7z7.uVf5J5Y5Q8vH', 'Delivery User Six', 'DELIVERY', '+84903000006', TRUE, TRUE, TRUE, FALSE, NOW(), NOW());

-- ================================================
-- RESTAURANTS (15 restaurants - diverse cuisines)
-- ================================================

INSERT INTO restaurants (id, name, address, phone_number, description, cuisine, rating, total_reviews, is_open, is_active, opening_time, closing_time, delivery_fee, estimated_delivery_time, owner_id, created_at, updated_at)
VALUES
-- Italian Restaurants
(gen_random_uuid(), 'Napoli Pizza House', '123 Nguyen Hue Boulevard, District 1, Ho Chi Minh City', '+84901234567', 'Authentic Neapolitan pizza from wood-fired oven', 'Italian', 4.8, 245, TRUE, TRUE, '10:00', '23:00', 2.5, 30, (SELECT id FROM users WHERE username = 'test.merchant.01'), NOW(), NOW()),
(gen_random_uuid(), 'La Bella Pasta', '456 Le Loi Street, District 3, Ho Chi Minh City', '+84901234568', 'Fresh homemade pasta daily', 'Italian', 4.6, 189, TRUE, TRUE, '11:00', '22:00', 2.0, 35, (SELECT id FROM users WHERE username = 'test.merchant.01'), NOW(), NOW()),

-- Vietnamese Restaurants
(gen_random_uuid(), 'Pho Hanoi', '789 Hai Ba Trung, District 1, Ho Chi Minh City', '+84901234569', 'Traditional Northern Vietnamese Pho', 'Vietnamese', 4.9, 567, TRUE, TRUE, '06:00', '14:00', 1.0, 20, (SELECT id FROM users WHERE username = 'test.merchant.02'), NOW(), NOW()),
(gen_random_uuid(), 'Bun Cha Saigon', '321 Pasteur, District 3, Ho Chi Minh City', '+84901234570', 'Grilled pork with vermicelli noodles', 'Vietnamese', 4.7, 423, TRUE, TRUE, '10:00', '21:00', 1.5, 25, (SELECT id FROM users WHERE username = 'test.merchant.02'), NOW(), NOW()),
(gen_random_uuid(), 'Com Tam Ba Ghien', '555 Nguyen Thi Minh Khai, District 1, Ho Chi Minh City', '+84901234571', 'Best broken rice in town since 1995', 'Vietnamese', 4.5, 892, TRUE, TRUE, '06:00', '23:00', 1.0, 20, (SELECT id FROM users WHERE username = 'test.merchant.03'), NOW(), NOW()),

-- Japanese Restaurants
(gen_random_uuid(), 'Sushi Tokyo', '234 Dong Khoi, District 1, Ho Chi Minh City', '+84901234572', 'Premium sushi by Japanese chef', 'Japanese', 4.9, 312, TRUE, TRUE, '11:00', '22:00', 3.5, 40, (SELECT id FROM users WHERE username = 'test.merchant.03'), NOW(), NOW()),
(gen_random_uuid(), 'Ramen Ichiban', '567 Nguyen Van Cu, District 5, Ho Chi Minh City', '+84901234573', 'Authentic Tonkotsu ramen', 'Japanese', 4.7, 278, TRUE, TRUE, '11:00', '23:00', 2.5, 30, (SELECT id FROM users WHERE username = 'test.merchant.04'), NOW(), NOW()),

-- Korean Restaurants
(gen_random_uuid(), 'Seoul BBQ', '890 Le Thanh Ton, District 1, Ho Chi Minh City', '+84901234574', 'All-you-can-eat Korean BBQ', 'Korean', 4.6, 445, TRUE, TRUE, '11:00', '23:30', 2.0, 35, (SELECT id FROM users WHERE username = 'test.merchant.04'), NOW(), NOW()),
(gen_random_uuid(), 'Kimchi House', '111 Vo Van Tan, District 3, Ho Chi Minh City', '+84901234575', 'Traditional Korean home cooking', 'Korean', 4.8, 334, TRUE, TRUE, '10:00', '22:00', 2.0, 30, (SELECT id FROM users WHERE username = 'test.merchant.05'), NOW(), NOW()),

-- Thai Restaurants
(gen_random_uuid(), 'Bangkok Street Food', '222 Tran Hung Dao, District 1, Ho Chi Minh City', '+84901234576', 'Authentic Thai street food experience', 'Thai', 4.7, 523, TRUE, TRUE, '10:00', '23:00', 2.5, 30, (SELECT id FROM users WHERE username = 'test.merchant.05'), NOW(), NOW()),

-- Chinese Restaurants
(gen_random_uuid(), 'Dragon Dim Sum', '333 Nguyen Trai, District 5, Ho Chi Minh City', '+84901234577', 'Cantonese dim sum and BBQ', 'Chinese', 4.5, 667, TRUE, TRUE, '07:00', '22:00', 2.0, 35, (SELECT id FROM users WHERE username = 'test.merchant.06'), NOW(), NOW()),

-- American Restaurants
(gen_random_uuid(), 'NY Burger & Steaks', '444 Mac Dinh Chi, District 1, Ho Chi Minh City', '+84901234578', 'Premium burgers and USDA steaks', 'American', 4.6, 389, TRUE, TRUE, '11:00', '23:00', 3.0, 35, (SELECT id FROM users WHERE username = 'test.merchant.06'), NOW(), NOW()),

-- Mexican Restaurants
(gen_random_uuid(), 'Taco Fiesta', '555 Bui Vien, District 1, Ho Chi Minh City', '+84901234579', 'Authentic Mexican tacos and burritos', 'Mexican', 4.4, 234, TRUE, TRUE, '11:00', '23:00', 2.5, 30, (SELECT id FROM users WHERE username = 'test.merchant.07'), NOW(), NOW()),

-- Indian Restaurants
(gen_random_uuid(), 'Mumbai Spice', '666 Pham Ngu Lao, District 1, Ho Chi Minh City', '+84901234580', 'North and South Indian cuisine', 'Indian', 4.7, 445, TRUE, TRUE, '11:00', '22:30', 2.0, 35, (SELECT id FROM users WHERE username = 'test.merchant.07'), NOW(), NOW()),

-- Vegetarian/Vegan
(gen_random_uuid(), 'Green Garden Vegan', '777 Ly Tu Trong, District 1, Ho Chi Minh City', '+84901234581', '100% plant-based Vietnamese & International', 'Vegetarian', 4.8, 567, TRUE, TRUE, '08:00', '21:00', 1.5, 25, (SELECT id FROM users WHERE username = 'test.merchant.08'), NOW(), NOW());

-- ================================================
-- MENU ITEMS (100+ items across all restaurants)
-- ================================================

-- Napoli Pizza House (Italian)
INSERT INTO menu_items (id, restaurant_id, name, description, price, category, is_available, is_active, preparation_time, calories, is_vegetarian, is_vegan, is_gluten_free, is_spicy, order_count, created_at, updated_at)
VALUES
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Napoli Pizza House'), 'Margherita Pizza', 'San Marzano tomatoes, fresh mozzarella, basil', 12.99, 'Main Course', TRUE, TRUE, 15, 800, TRUE, FALSE, FALSE, FALSE, 234, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Napoli Pizza House'), 'Pepperoni Pizza', 'Tomato sauce, mozzarella, spicy pepperoni', 14.99, 'Main Course', TRUE, TRUE, 15, 950, FALSE, FALSE, FALSE, TRUE, 456, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Napoli Pizza House'), 'Quattro Formaggi', 'Four cheese pizza (mozzarella, gorgonzola, parmesan, ricotta)', 15.99, 'Main Course', TRUE, TRUE, 18, 1100, TRUE, FALSE, FALSE, FALSE, 178, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Napoli Pizza House'), 'Prosciutto e Rucola', 'Tomato, mozzarella, prosciutto, arugula, parmesan', 16.99, 'Main Course', TRUE, TRUE, 17, 850, FALSE, FALSE, FALSE, FALSE, 234, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Napoli Pizza House'), 'Caprese Salad', 'Fresh tomatoes, buffalo mozzarella, basil, olive oil', 8.99, 'Appetizer', TRUE, TRUE, 8, 320, TRUE, FALSE, TRUE, FALSE, 123, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Napoli Pizza House'), 'Tiramisu', 'Classic Italian coffee-flavored dessert', 6.99, 'Dessert', TRUE, TRUE, 5, 450, TRUE, FALSE, FALSE, FALSE, 189, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Napoli Pizza House'), 'Panna Cotta', 'Italian cream dessert with berry sauce', 6.49, 'Dessert', TRUE, TRUE, 5, 380, TRUE, FALSE, TRUE, FALSE, 145, NOW(), NOW());

-- La Bella Pasta (Italian)
INSERT INTO menu_items (id, restaurant_id, name, description, price, category, is_available, is_active, preparation_time, calories, is_vegetarian, is_vegan, is_gluten_free, is_spicy, order_count, created_at, updated_at)
VALUES
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'La Bella Pasta'), 'Spaghetti Carbonara', 'Eggs, pecorino cheese, guanciale, black pepper', 13.99, 'Main Course', TRUE, TRUE, 18, 920, FALSE, FALSE, FALSE, FALSE, 345, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'La Bella Pasta'), 'Penne Arrabiata', 'Spicy tomato sauce with garlic and chili', 11.99, 'Main Course', TRUE, TRUE, 15, 650, TRUE, TRUE, FALSE, TRUE, 234, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'La Bella Pasta'), 'Fettuccine Alfredo', 'Creamy parmesan sauce', 12.99, 'Main Course', TRUE, TRUE, 16, 880, TRUE, FALSE, FALSE, FALSE, 278, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'La Bella Pasta'), 'Lasagna Bolognese', 'Layers of pasta, meat sauce, bechamel, cheese', 14.99, 'Main Course', TRUE, TRUE, 25, 1050, FALSE, FALSE, FALSE, FALSE, 423, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'La Bella Pasta'), 'Ravioli Ricotta e Spinaci', 'Ricotta and spinach filled pasta in butter sage sauce', 13.99, 'Main Course', TRUE, TRUE, 20, 780, TRUE, FALSE, FALSE, FALSE, 189, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'La Bella Pasta'), 'Bruschetta', 'Toasted bread with tomatoes, garlic, basil', 6.99, 'Appetizer', TRUE, TRUE, 10, 280, TRUE, TRUE, FALSE, FALSE, 156, NOW(), NOW());

-- Pho Hanoi (Vietnamese)
INSERT INTO menu_items (id, restaurant_id, name, description, price, category, is_available, is_active, preparation_time, calories, is_vegetarian, is_vegan, is_gluten_free, is_spicy, order_count, created_at, updated_at)
VALUES
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Pho Hanoi'), 'Pho Bo Tai', 'Rare beef rice noodle soup', 8.99, 'Main Course', TRUE, TRUE, 12, 550, FALSE, FALSE, TRUE, FALSE, 789, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Pho Hanoi'), 'Pho Bo Chin', 'Well-done beef rice noodle soup', 8.99, 'Main Course', TRUE, TRUE, 12, 580, FALSE, FALSE, TRUE, FALSE, 678, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Pho Hanoi'), 'Pho Ga', 'Chicken rice noodle soup', 7.99, 'Main Course', TRUE, TRUE, 12, 480, FALSE, FALSE, TRUE, FALSE, 456, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Pho Hanoi'), 'Pho Dac Biet', 'Special combo beef noodle soup', 10.99, 'Main Course', TRUE, TRUE, 15, 720, FALSE, FALSE, TRUE, FALSE, 892, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Pho Hanoi'), 'Nem Ran', 'Fried spring rolls (4 pieces)', 5.99, 'Appetizer', TRUE, TRUE, 10, 380, FALSE, FALSE, TRUE, FALSE, 234, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Pho Hanoi'), 'Goi Cuon', 'Fresh spring rolls (2 pieces)', 4.99, 'Appetizer', TRUE, TRUE, 8, 180, TRUE, TRUE, TRUE, FALSE, 345, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Pho Hanoi'), 'Ca Phe Sua Da', 'Vietnamese iced coffee', 3.50, 'Beverage', TRUE, TRUE, 5, 150, TRUE, FALSE, TRUE, FALSE, 567, NOW(), NOW());

-- Bun Cha Saigon (Vietnamese)
INSERT INTO menu_items (id, restaurant_id, name, description, price, category, is_available, is_active, preparation_time, calories, is_vegetarian, is_vegan, is_gluten_free, is_spicy, order_count, created_at, updated_at)
VALUES
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Bun Cha Saigon'), 'Bun Cha Hanoi', 'Grilled pork with vermicelli and herbs', 9.99, 'Main Course', TRUE, TRUE, 15, 650, FALSE, FALSE, TRUE, FALSE, 678, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Bun Cha Saigon'), 'Bun Thit Nuong', 'Grilled pork with vermicelli', 8.99, 'Main Course', TRUE, TRUE, 15, 620, FALSE, FALSE, TRUE, FALSE, 523, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Bun Cha Saigon'), 'Bun Bo Nam Bo', 'Beef with vermicelli and vegetables', 9.99, 'Main Course', TRUE, TRUE, 15, 680, FALSE, FALSE, TRUE, FALSE, 445, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Bun Cha Saigon'), 'Cha Gio', 'Crispy Vietnamese egg rolls (5 pieces)', 6.99, 'Appetizer', TRUE, TRUE, 12, 420, FALSE, FALSE, TRUE, FALSE, 289, NOW(), NOW());

-- Com Tam Ba Ghien (Vietnamese)
INSERT INTO menu_items (id, restaurant_id, name, description, price, category, is_available, is_active, preparation_time, calories, is_vegetarian, is_vegan, is_gluten_free, is_spicy, order_count, created_at, updated_at)
VALUES
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Com Tam Ba Ghien'), 'Com Tam Suon Nuong', 'Broken rice with grilled pork chop', 7.99, 'Main Course', TRUE, TRUE, 12, 720, FALSE, FALSE, TRUE, FALSE, 892, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Com Tam Ba Ghien'), 'Com Tam Bi Cha', 'Broken rice with shredded pork skin and egg meatloaf', 7.49, 'Main Course', TRUE, TRUE, 12, 680, FALSE, FALSE, TRUE, FALSE, 756, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Com Tam Ba Ghien'), 'Com Tam Dac Biet', 'Special broken rice combo', 9.99, 'Main Course', TRUE, TRUE, 15, 850, FALSE, FALSE, TRUE, FALSE, 1023, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Com Tam Ba Ghien'), 'Com Tam Ga Nuong', 'Broken rice with grilled chicken', 7.99, 'Main Course', TRUE, TRUE, 12, 690, FALSE, FALSE, TRUE, FALSE, 678, NOW(), NOW());

-- Sushi Tokyo (Japanese)
INSERT INTO menu_items (id, restaurant_id, name, description, price, category, is_available, is_active, preparation_time, calories, is_vegetarian, is_vegan, is_gluten_free, is_spicy, order_count, created_at, updated_at)
VALUES
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Sushi Tokyo'), 'Salmon Nigiri Set', '8 pieces of fresh salmon sushi', 16.99, 'Main Course', TRUE, TRUE, 12, 480, FALSE, FALSE, TRUE, FALSE, 456, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Sushi Tokyo'), 'Tuna Nigiri Set', '8 pieces of bluefin tuna sushi', 19.99, 'Main Course', TRUE, TRUE, 12, 440, FALSE, FALSE, TRUE, FALSE, 389, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Sushi Tokyo'), 'California Roll', 'Crab, avocado, cucumber (8 pieces)', 11.99, 'Main Course', TRUE, TRUE, 10, 320, FALSE, FALSE, TRUE, FALSE, 567, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Sushi Tokyo'), 'Spicy Tuna Roll', 'Spicy tuna, cucumber (8 pieces)', 12.99, 'Main Course', TRUE, TRUE, 10, 380, FALSE, FALSE, TRUE, TRUE, 445, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Sushi Tokyo'), 'Dragon Roll', 'Eel, cucumber, avocado (8 pieces)', 15.99, 'Main Course', TRUE, TRUE, 15, 520, FALSE, FALSE, TRUE, FALSE, 334, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Sushi Tokyo'), 'Miso Soup', 'Traditional Japanese soybean soup', 3.99, 'Appetizer', TRUE, TRUE, 5, 80, TRUE, TRUE, TRUE, FALSE, 678, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Sushi Tokyo'), 'Edamame', 'Steamed young soybeans with sea salt', 4.99, 'Appetizer', TRUE, TRUE, 6, 120, TRUE, TRUE, TRUE, FALSE, 523, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Sushi Tokyo'), 'Tempura Mix', 'Assorted vegetables and shrimp tempura', 13.99, 'Appetizer', TRUE, TRUE, 15, 550, FALSE, FALSE, FALSE, FALSE, 289, NOW(), NOW());

-- Ramen Ichiban (Japanese)
INSERT INTO menu_items (id, restaurant_id, name, description, price, category, is_available, is_active, preparation_time, calories, is_vegetarian, is_vegan, is_gluten_free, is_spicy, order_count, created_at, updated_at)
VALUES
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Ramen Ichiban'), 'Tonkotsu Ramen', 'Pork bone broth, chashu, soft-boiled egg', 13.99, 'Main Course', TRUE, TRUE, 18, 780, FALSE, FALSE, FALSE, FALSE, 789, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Ramen Ichiban'), 'Shoyu Ramen', 'Soy sauce base, chicken broth', 12.99, 'Main Course', TRUE, TRUE, 18, 720, FALSE, FALSE, FALSE, FALSE, 656, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Ramen Ichiban'), 'Miso Ramen', 'Miso paste broth with pork', 13.49, 'Main Course', TRUE, TRUE, 18, 750, FALSE, FALSE, FALSE, FALSE, 567, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Ramen Ichiban'), 'Spicy Tan Tan Ramen', 'Spicy sesame broth with minced pork', 14.99, 'Main Course', TRUE, TRUE, 18, 820, FALSE, FALSE, FALSE, TRUE, 445, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Ramen Ichiban'), 'Vegetable Ramen', 'Vegetable broth with seasonal vegetables', 11.99, 'Main Course', TRUE, TRUE, 16, 580, TRUE, TRUE, FALSE, FALSE, 234, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Ramen Ichiban'), 'Gyoza', 'Pan-fried pork dumplings (6 pieces)', 6.99, 'Appetizer', TRUE, TRUE, 10, 380, FALSE, FALSE, FALSE, FALSE, 678, NOW(), NOW());

-- Seoul BBQ (Korean)
INSERT INTO menu_items (id, restaurant_id, name, description, price, category, is_available, is_active, preparation_time, calories, is_vegetarian, is_vegan, is_gluten_free, is_spicy, order_count, created_at, updated_at)
VALUES
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Seoul BBQ'), 'Bulgogi', 'Marinated grilled beef', 18.99, 'Main Course', TRUE, TRUE, 20, 680, FALSE, FALSE, TRUE, FALSE, 567, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Seoul BBQ'), 'Galbi', 'Grilled beef short ribs', 24.99, 'Main Course', TRUE, TRUE, 25, 850, FALSE, FALSE, TRUE, FALSE, 445, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Seoul BBQ'), 'Samgyeopsal', 'Grilled pork belly', 16.99, 'Main Course', TRUE, TRUE, 20, 920, FALSE, FALSE, TRUE, FALSE, 678, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Seoul BBQ'), 'Dak Galbi', 'Spicy stir-fried chicken', 15.99, 'Main Course', TRUE, TRUE, 22, 750, FALSE, FALSE, TRUE, TRUE, 389, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Seoul BBQ'), 'Kimchi Jjigae', 'Spicy kimchi stew', 12.99, 'Main Course', TRUE, TRUE, 18, 420, FALSE, FALSE, TRUE, TRUE, 523, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Seoul BBQ'), 'Japchae', 'Stir-fried glass noodles with vegetables', 11.99, 'Side Dish', TRUE, TRUE, 15, 380, TRUE, TRUE, TRUE, FALSE, 334, NOW(), NOW());

-- Kimchi House (Korean)
INSERT INTO menu_items (id, restaurant_id, name, description, price, category, is_available, is_active, preparation_time, calories, is_vegetarian, is_vegan, is_gluten_free, is_spicy, order_count, created_at, updated_at)
VALUES
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Kimchi House'), 'Bibimbap', 'Mixed rice with vegetables and beef', 13.99, 'Main Course', TRUE, TRUE, 16, 620, FALSE, FALSE, TRUE, TRUE, 678, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Kimchi House'), 'Dolsot Bibimbap', 'Hot stone pot mixed rice', 15.99, 'Main Course', TRUE, TRUE, 18, 680, FALSE, FALSE, TRUE, TRUE, 556, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Kimchi House'), 'Sundubu Jjigae', 'Soft tofu stew', 12.99, 'Main Course', TRUE, TRUE, 16, 380, TRUE, FALSE, TRUE, TRUE, 445, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Kimchi House'), 'Tteokbokki', 'Spicy rice cakes', 9.99, 'Appetizer', TRUE, TRUE, 12, 450, TRUE, TRUE, TRUE, TRUE, 567, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Kimchi House'), 'Korean Fried Chicken', 'Crispy fried chicken with sweet-spicy sauce', 14.99, 'Main Course', TRUE, TRUE, 20, 820, FALSE, FALSE, TRUE, TRUE, 789, NOW(), NOW());

-- Bangkok Street Food (Thai)
INSERT INTO menu_items (id, restaurant_id, name, description, price, category, is_available, is_active, preparation_time, calories, is_vegetarian, is_vegan, is_gluten_free, is_spicy, order_count, created_at, updated_at)
VALUES
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Bangkok Street Food'), 'Pad Thai', 'Stir-fried rice noodles with shrimp', 11.99, 'Main Course', TRUE, TRUE, 15, 680, FALSE, FALSE, TRUE, FALSE, 892, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Bangkok Street Food'), 'Green Curry Chicken', 'Spicy coconut curry with chicken', 12.99, 'Main Course', TRUE, TRUE, 18, 720, FALSE, FALSE, TRUE, TRUE, 678, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Bangkok Street Food'), 'Red Curry Beef', 'Spicy red curry with beef', 13.99, 'Main Course', TRUE, TRUE, 20, 780, FALSE, FALSE, TRUE, TRUE, 556, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Bangkok Street Food'), 'Tom Yum Goong', 'Spicy and sour shrimp soup', 9.99, 'Appetizer', TRUE, TRUE, 15, 280, FALSE, FALSE, TRUE, TRUE, 567, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Bangkok Street Food'), 'Som Tam', 'Green papaya salad', 7.99, 'Appetizer', TRUE, TRUE, 10, 180, TRUE, TRUE, TRUE, TRUE, 445, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Bangkok Street Food'), 'Mango Sticky Rice', 'Sweet mango with sticky rice and coconut milk', 6.99, 'Dessert', TRUE, TRUE, 8, 380, TRUE, TRUE, TRUE, FALSE, 678, NOW(), NOW());

-- Dragon Dim Sum (Chinese)
INSERT INTO menu_items (id, restaurant_id, name, description, price, category, is_available, is_active, preparation_time, calories, is_vegetarian, is_vegan, is_gluten_free, is_spicy, order_count, created_at, updated_at)
VALUES
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Dragon Dim Sum'), 'Har Gow', 'Shrimp dumplings (4 pieces)', 5.99, 'Appetizer', TRUE, TRUE, 10, 180, FALSE, FALSE, TRUE, FALSE, 567, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Dragon Dim Sum'), 'Siu Mai', 'Pork and shrimp dumplings (4 pieces)', 5.99, 'Appetizer', TRUE, TRUE, 10, 220, FALSE, FALSE, TRUE, FALSE, 523, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Dragon Dim Sum'), 'Char Siu Bao', 'BBQ pork buns (3 pieces)', 6.99, 'Appetizer', TRUE, TRUE, 12, 320, FALSE, FALSE, FALSE, FALSE, 678, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Dragon Dim Sum'), 'Cheung Fun', 'Rice noodle rolls with shrimp', 7.99, 'Main Course', TRUE, TRUE, 12, 280, FALSE, FALSE, TRUE, FALSE, 445, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Dragon Dim Sum'), 'Peking Duck', 'Crispy roasted duck with pancakes', 28.99, 'Main Course', TRUE, TRUE, 30, 920, FALSE, FALSE, FALSE, FALSE, 234, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Dragon Dim Sum'), 'Fried Rice', 'Yangzhou style fried rice', 8.99, 'Main Course', TRUE, TRUE, 12, 520, FALSE, FALSE, TRUE, FALSE, 556, NOW(), NOW());

-- NY Burger & Steaks (American)
INSERT INTO menu_items (id, restaurant_id, name, description, price, category, is_available, is_active, preparation_time, calories, is_vegetarian, is_vegan, is_gluten_free, is_spicy, order_count, created_at, updated_at)
VALUES
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'NY Burger & Steaks'), 'Classic Cheeseburger', 'Angus beef, cheddar, lettuce, tomato, pickles', 12.99, 'Main Course', TRUE, TRUE, 15, 850, FALSE, FALSE, FALSE, FALSE, 789, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'NY Burger & Steaks'), 'Bacon BBQ Burger', 'Beef patty, bacon, BBQ sauce, onion rings', 14.99, 'Main Course', TRUE, TRUE, 18, 1020, FALSE, FALSE, FALSE, FALSE, 678, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'NY Burger & Steaks'), 'Mushroom Swiss Burger', 'Beef, sautéed mushrooms, Swiss cheese', 13.99, 'Main Course', TRUE, TRUE, 16, 920, FALSE, FALSE, FALSE, FALSE, 567, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'NY Burger & Steaks'), 'Ribeye Steak 300g', 'USDA Prime ribeye, grilled to perfection', 29.99, 'Main Course', TRUE, TRUE, 25, 850, FALSE, FALSE, TRUE, FALSE, 445, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'NY Burger & Steaks'), 'T-Bone Steak 400g', 'Premium T-bone steak', 34.99, 'Main Course', TRUE, TRUE, 28, 1050, FALSE, FALSE, TRUE, FALSE, 334, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'NY Burger & Steaks'), 'French Fries', 'Crispy golden fries', 4.99, 'Side Dish', TRUE, TRUE, 8, 420, TRUE, TRUE, TRUE, FALSE, 892, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'NY Burger & Steaks'), 'Onion Rings', 'Beer-battered onion rings', 5.99, 'Side Dish', TRUE, TRUE, 10, 480, TRUE, FALSE, FALSE, FALSE, 567, NOW(), NOW());

-- Taco Fiesta (Mexican)
INSERT INTO menu_items (id, restaurant_id, name, description, price, category, is_available, is_active, preparation_time, calories, is_vegetarian, is_vegan, is_gluten_free, is_spicy, order_count, created_at, updated_at)
VALUES
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Taco Fiesta'), 'Beef Tacos', 'Seasoned ground beef tacos (3 pieces)', 9.99, 'Main Course', TRUE, TRUE, 12, 620, FALSE, FALSE, TRUE, TRUE, 567, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Taco Fiesta'), 'Chicken Tacos', 'Grilled chicken tacos (3 pieces)', 9.99, 'Main Course', TRUE, TRUE, 12, 580, FALSE, FALSE, TRUE, FALSE, 523, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Taco Fiesta'), 'Fish Tacos', 'Battered fish tacos (3 pieces)', 11.99, 'Main Course', TRUE, TRUE, 15, 650, FALSE, FALSE, TRUE, FALSE, 445, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Taco Fiesta'), 'Beef Burrito', 'Flour tortilla with beef, rice, beans, cheese', 12.99, 'Main Course', TRUE, TRUE, 15, 820, FALSE, FALSE, FALSE, TRUE, 678, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Taco Fiesta'), 'Chicken Quesadilla', 'Grilled chicken and cheese in tortilla', 10.99, 'Main Course', TRUE, TRUE, 12, 720, FALSE, FALSE, FALSE, FALSE, 556, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Taco Fiesta'), 'Nachos Supreme', 'Tortilla chips with beef, cheese, salsa, guacamole', 11.99, 'Appetizer', TRUE, TRUE, 12, 880, FALSE, FALSE, TRUE, TRUE, 445, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Taco Fiesta'), 'Guacamole & Chips', 'Fresh avocado dip with tortilla chips', 7.99, 'Appetizer', TRUE, TRUE, 8, 380, TRUE, TRUE, TRUE, FALSE, 567, NOW(), NOW());

-- Mumbai Spice (Indian)
INSERT INTO menu_items (id, restaurant_id, name, description, price, category, is_available, is_active, preparation_time, calories, is_vegetarian, is_vegan, is_gluten_free, is_spicy, order_count, created_at, updated_at)
VALUES
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Mumbai Spice'), 'Chicken Tikka Masala', 'Grilled chicken in creamy tomato sauce', 14.99, 'Main Course', TRUE, TRUE, 20, 680, FALSE, FALSE, TRUE, TRUE, 678, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Mumbai Spice'), 'Butter Chicken', 'Tandoori chicken in rich butter sauce', 14.99, 'Main Course', TRUE, TRUE, 20, 720, FALSE, FALSE, TRUE, FALSE, 789, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Mumbai Spice'), 'Lamb Rogan Josh', 'Slow-cooked lamb in aromatic curry', 16.99, 'Main Course', TRUE, TRUE, 25, 780, FALSE, FALSE, TRUE, TRUE, 445, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Mumbai Spice'), 'Palak Paneer', 'Spinach curry with cottage cheese', 12.99, 'Main Course', TRUE, TRUE, 18, 520, TRUE, FALSE, TRUE, FALSE, 556, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Mumbai Spice'), 'Vegetable Biryani', 'Fragrant rice with mixed vegetables', 11.99, 'Main Course', TRUE, TRUE, 22, 580, TRUE, TRUE, TRUE, FALSE, 567, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Mumbai Spice'), 'Naan Bread', 'Traditional Indian flatbread', 3.99, 'Side Dish', TRUE, TRUE, 8, 280, TRUE, FALSE, FALSE, FALSE, 892, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Mumbai Spice'), 'Samosa', 'Crispy pastry with spiced potatoes (2 pieces)', 5.99, 'Appetizer', TRUE, TRUE, 10, 320, TRUE, TRUE, FALSE, TRUE, 678, NOW(), NOW());

-- Green Garden Vegan (Vegetarian)
INSERT INTO menu_items (id, restaurant_id, name, description, price, category, is_available, is_active, preparation_time, calories, is_vegetarian, is_vegan, is_gluten_free, is_spicy, order_count, created_at, updated_at)
VALUES
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Green Garden Vegan'), 'Vegan Pho', 'Rice noodle soup with vegetables and tofu', 9.99, 'Main Course', TRUE, TRUE, 15, 420, TRUE, TRUE, TRUE, FALSE, 678, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Green Garden Vegan'), 'Buddha Bowl', 'Quinoa, roasted vegetables, tahini dressing', 12.99, 'Main Course', TRUE, TRUE, 18, 520, TRUE, TRUE, TRUE, FALSE, 567, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Green Garden Vegan'), 'Vegan Burger', 'Plant-based patty, avocado, vegetables', 11.99, 'Main Course', TRUE, TRUE, 15, 580, TRUE, TRUE, FALSE, FALSE, 445, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Green Garden Vegan'), 'Pad Thai Tofu', 'Stir-fried rice noodles with tofu', 10.99, 'Main Course', TRUE, TRUE, 16, 620, TRUE, TRUE, TRUE, FALSE, 556, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Green Garden Vegan'), 'Green Curry Vegetables', 'Thai green curry with mixed vegetables', 11.99, 'Main Course', TRUE, TRUE, 18, 480, TRUE, TRUE, TRUE, TRUE, 445, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Green Garden Vegan'), 'Fresh Smoothie Bowl', 'Açai, banana, berries, granola', 8.99, 'Dessert', TRUE, TRUE, 10, 320, TRUE, TRUE, TRUE, FALSE, 678, NOW(), NOW()),
(gen_random_uuid(), (SELECT id FROM restaurants WHERE name = 'Green Garden Vegan'), 'Hummus Platter', 'Homemade hummus with vegetables and pita', 7.99, 'Appetizer', TRUE, TRUE, 8, 280, TRUE, TRUE, FALSE, FALSE, 523, NOW(), NOW());
