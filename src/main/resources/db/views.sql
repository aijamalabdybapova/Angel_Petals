-- Представление для отчета по продажам
CREATE OR REPLACE VIEW sales_report AS
SELECT
    o.id as order_id,
    o.order_number,
    o.created_at as order_date,
    u.username as customer,
    o.total_amount,
    o.status,
    COUNT(oi.id) as items_count,
    STRING_AGG(b.name, ', ') as bouquet_names
FROM orders o
JOIN users u ON o.user_id = u.id
JOIN order_items oi ON o.id = oi.order_id
JOIN bouquets b ON oi.bouquet_id = b.id
WHERE o.deleted = false
GROUP BY o.id, o.order_number, o.created_at, u.username, o.total_amount, o.status;

-- Представление для статистики букетов
CREATE OR REPLACE VIEW bouquet_statistics AS
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
LEFT JOIN reviews r ON b.id = r.bouquet_id
WHERE b.deleted = false
GROUP BY b.id, b.name, b.price, c.name;

-- Представление для активности пользователей
CREATE OR REPLACE VIEW user_activity_report AS
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