-- data.sql
-- Insert roles with all BaseEntity fields
INSERT INTO roles (name, description, deleted, created_at, updated_at) VALUES
('ROLE_USER', 'Обычный пользователь', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
--('ROLE_MANAGER', 'Менеджер', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ROLE_ADMIN', 'Администратор', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;

-- Insert categories
INSERT INTO categories (name, description, deleted, created_at, updated_at)
VALUES
('Свадебные букеты', 'Элегантные букеты для самого важного дня в жизни', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Романтические букеты', 'Букеты, выражающие любовь и нежность', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Праздничные букеты', 'Яркие композиции для особых occasions', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Букеты на юбилей', 'Торжественные букеты для памятных дат', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Весенние букеты', 'Свежие и яркие цветы, символизирующие пробуждение природы', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Осенние композиции', 'Теплые цвета и уютные сочетания осенних цветов', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;

-- Insert sample bouquets
INSERT INTO bouquets (name, description, price, image_url, in_stock, stock_quantity, category_id, deleted, created_at, updated_at) VALUES
('Нежная роза', 'Композиция из 25 алых роз, символизирующая страстную любовь и преданность. Идеально подходит для романтических признаний.', 6000.00, 'rose_bouquet.jpg', true, 15, (SELECT id FROM categories WHERE name = 'Романтические букеты'), false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Весенняя свежесть', 'Яркий букет из тюльпанов и нарциссов, наполняющий пространство ароматом весны и обновления.', 4800.00, 'spring_fresh.jpg', true, 20, (SELECT id FROM categories WHERE name = 'Весенние букеты'), false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Свадебный шик', 'Элегантная композиция из белых пионов и роз для невесты. Символизирует чистоту и начало новой жизни.', 6500.00, 'wedding_elegance.jpg', true, 8, (SELECT id FROM categories WHERE name = 'Свадебные букеты'), false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Осенняя гармония', 'Теплая композиция из хризантем, гербер и декоративной зелени в осенней цветовой гамме.', 4200.00, 'autumn_harmony.jpg', true, 12, (SELECT id FROM categories WHERE name = 'Осенние композиции'), false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Романтический микс', 'Нежный букет из роз, пионов и эустом в пастельных тонах. Идеален для свиданий и признаний в любви.', 9200.00, 'romantic_mix.jpg', true, 18, (SELECT id FROM categories WHERE name = 'Романтические букеты'), false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Праздничный фейерверк', 'Яркий и жизнерадостный букет из гербер, ирисов и хризантем для создания праздничного настроения.', 4800.00, 'festive_fireworks.jpg', true, 25, (SELECT id FROM categories WHERE name = 'Праздничные букеты'), false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Классическая элегантность', 'Строгий и элегантный букет из красных роз и калл для деловых подарков и торжественных мероприятий.', 4500.00, 'classic_elegance.jpg', true, 10, (SELECT id FROM categories WHERE name = 'Букеты на юбилей'), false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Летний рассвет', 'Свежий букет из полевых цветов и зелени, напоминающий о теплом летнем утре и природной красоте.', 2900.00, 'summer_dawn.jpg', true, 22, (SELECT id FROM categories WHERE name = 'Весенние букеты'), false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Осенняя розария', 'Пышный букет из кустовых роз в осенней гамме: терракотовые, персиковые и бордовые оттенки. Идеально передает атмосферу золотой осени.', 6200.00, 'autumn_roses.jpg', true, 10, (SELECT id FROM categories WHERE name = 'Осенние композиции'), false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Нежный рассвет', 'Элегантная композиция из пионовидных роз в пастельных розовых тонах. Пушистые бутоны создают ощущение нежности и романтики.', 7500.00, 'pink_dawn.jpg', true, 7, (SELECT id FROM categories WHERE name = 'Романтические букеты'), false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Лилиевая симфония', 'Изящный букет из розовых лилий, чайных роз и облаков гипсофилы. Нежное сочетание текстур и ароматов для истинных ценителей красоты.', 5800.00, 'lily_symphony.jpg', true, 12, (SELECT id FROM categories WHERE name = 'Романтические букеты'), false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Солнечное настроение', 'Щедрый букет-сборник из ромашек, роз, гербер и гортензий. Яркая и жизнерадостная композиция, которая подарит солнечное настроение.', 6900.00, 'sunny_mood.jpg', true, 18, (SELECT id FROM categories WHERE name = 'Праздничные букеты'), false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Create users with hashed passwords
INSERT INTO users (username, email, password, first_name, last_name, phone_number, address, deleted, created_at, updated_at) VALUES
('admin', 'admin@flowershop.ru', '$2a$12$ssBYdXCHgxMCZSNkam4NLen7KXndmb3LnV1UZjwzR9Eb.jCGCPREe', 'Алексей', 'Иванов', '+7 (999) 123-45-67', 'г. Москва, ул. Цветочная, д. 1', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
--('manager', 'manager@flowershop.ru', '$2a$12$cx1BpZ0/AiiBmcjcuKWfruMVZcgeHrBUcn4S1v6dc7pLFNtlQ8mLy', 'Мария', 'Петрова', '+7 (999) 123-45-68', 'г. Москва, ул. Цветочная, д. 1', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('user', 'user@flowershop.ru', '$2a$12$qXD9BKJ1F4Hhy5wCHB0snu37XavcGh1SXwimRT.GxGBNTyP8PrrLW', 'Сергей', 'Сидоров', '+7 (999) 123-45-69', 'г. Москва, ул. Садовая, д. 15', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- Assign roles to users
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
CROSS JOIN roles r
WHERE (u.username = 'admin' AND r.name = 'ROLE_ADMIN')
   --OR (u.username = 'manager' AND r.name = 'ROLE_MANAGER')
   OR (u.username = 'user' AND r.name = 'ROLE_USER')
ON CONFLICT (user_id, role_id) DO NOTHING;

-- Create carts for users
INSERT INTO carts (user_id, total_amount, deleted, created_at, updated_at)
SELECT id, 0.00, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM users
ON CONFLICT (user_id) DO NOTHING;

-- Insert sample orders
INSERT INTO orders (order_number, user_id, total_amount, status, recipient_name, recipient_phone, recipient_email, delivery_address, delivery_date, deleted, created_at, updated_at)
SELECT
    'ORD-20240001',
    u.id,
    3500.00,
    'COMPLETED',
    'Анна Сергеева',
    '+7 (999) 111-22-33',
    'anna@mail.ru',
    'г. Москва, ул. Ленина, д. 10, кв. 25',
    CURRENT_TIMESTAMP + INTERVAL '2 hours',
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'user'
UNION ALL
SELECT
    'ORD-20240002',
    u.id,
    5500.00,
    'IN_PROGRESS',
    'Дмитрий Волков',
    '+7 (999) 222-33-44',
    'dmitry@mail.ru',
    'г. Москва, пр. Мира, д. 45, кв. 12',
    CURRENT_TIMESTAMP + INTERVAL '3 hours',
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'user'
UNION ALL
SELECT
    'ORD-20240003',
    u.id,
    2800.00,
    'PENDING',
    'Елена Ковалева',
    '+7 (999) 333-44-55',
    'elena@mail.ru',
    'г. Москва, ул. Пушкина, д. 8, кв. 7',
    CURRENT_TIMESTAMP + INTERVAL '1 day',
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM users u WHERE u.username = 'user'
ON CONFLICT (order_number) DO NOTHING;

-- Insert order items
INSERT INTO order_items (order_id, bouquet_id, quantity, unit_price, subtotal, deleted, created_at, updated_at)
SELECT
    o.id,
    b.id,
    1,
    3500.00,
    3500.00,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM orders o
JOIN bouquets b ON b.name = 'Нежная роза'
WHERE o.order_number = 'ORD-20240001'
UNION ALL
SELECT
    o.id,
    b.id,
    1,
    5500.00,
    5500.00,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM orders o
JOIN bouquets b ON b.name = 'Свадебный шик'
WHERE o.order_number = 'ORD-20240002'
UNION ALL
SELECT
    o.id,
    b.id,
    1,
    2800.00,
    2800.00,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM orders o
JOIN bouquets b ON b.name = 'Весенняя свежесть'
WHERE o.order_number = 'ORD-20240003';

-- Insert sample reviews
INSERT INTO reviews (user_id, bouquet_id, rating, comment, is_approved, deleted, created_at, updated_at)
SELECT
    u.id,
    b.id,
    5,
    'Прекрасный букет! Розы были свежие и ароматные. Получательница была в восторге!',
    true,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM users u
JOIN bouquets b ON b.name = 'Нежная роза'
WHERE u.username = 'user'
UNION ALL
SELECT
    u.id,
    b.id,
    4,
    'Очень яркий и свежий букет. Доставили вовремя, все цветы в отличном состоянии.',
    true,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM users u
JOIN bouquets b ON b.name = 'Весенняя свежесть'
WHERE u.username = 'user'
UNION ALL
SELECT
    u.id,
    b.id,
    5,
    'Идеальный букет для свадьбы! Все гости восхищались. Спасибо за качественную работу!',
    true,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM users u
JOIN bouquets b ON b.name = 'Свадебный шик'
WHERE u.username = 'user';

-- Пересоздаем VIEW чтобы включить новые данные
DROP VIEW IF EXISTS sales_report;
CREATE VIEW sales_report AS
SELECT
    o.id as order_id,
    o.order_number,
    o.created_at as order_date,
    u.username as customer,
    o.total_amount,
    o.status,
    COUNT(oi.id) as items_count,
    (SELECT GROUP_CONCAT(b.name SEPARATOR ', ')
     FROM order_items oi2
     JOIN bouquets b ON oi2.bouquet_id = b.id
     WHERE oi2.order_id = o.id) as bouquet_names
FROM orders o
JOIN users u ON o.user_id = u.id
JOIN order_items oi ON o.id = oi.order_id
JOIN bouquets b ON oi.bouquet_id = b.id
WHERE o.deleted = false
GROUP BY o.id, o.order_number, o.created_at, u.username, o.total_amount, o.status;

DROP VIEW IF EXISTS bouquet_statistics;
CREATE VIEW bouquet_statistics AS
SELECT
    b.id,
    b.name,
    b.price,
    c.name as category,
    COUNT(oi.id) as times_ordered,
    COALESCE(SUM(oi.quantity), 0) as total_quantity,
    AVG(r.rating) as average_rating
FROM bouquets b
LEFT JOIN categories c ON b.category_id = c.id
LEFT JOIN order_items oi ON b.id = oi.bouquet_id
LEFT JOIN reviews r ON b.id = r.bouquet_id AND r.deleted = false
WHERE b.deleted = false
GROUP BY b.id, b.name, b.price, c.name;

DROP VIEW IF EXISTS user_activity_report;
CREATE VIEW user_activity_report AS
SELECT
    u.id,
    u.username,
    u.email,
    u.first_name,
    u.last_name,
    COUNT(o.id) as total_orders,
    COALESCE(SUM(o.total_amount), 0) as total_spent,
    u.created_at as registration_date
FROM users u
LEFT JOIN orders o ON u.id = o.user_id AND o.deleted = false
WHERE u.deleted = false
GROUP BY u.id, u.username, u.email, u.first_name, u.last_name, u.created_at;