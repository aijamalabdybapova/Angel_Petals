-- src/main/resources/db/schema_updates.sql

-- 1. Таблица для аудита изменений
CREATE TABLE IF NOT EXISTS audit_log (
    id BIGSERIAL PRIMARY KEY,
    table_name VARCHAR(100) NOT NULL,
    record_id BIGINT NOT NULL,
    action VARCHAR(10) NOT NULL CHECK (action IN ('INSERT', 'UPDATE', 'DELETE')),
    old_data JSONB,
    new_data JSONB,
    changed_by VARCHAR(100),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Добавляем CHECK ограничения в существующие таблицы
ALTER TABLE bouquets
ADD CONSTRAINT chk_bouquet_price_positive
CHECK (price > 0);

ALTER TABLE bouquets
ADD CONSTRAINT chk_bouquet_stock_non_negative
CHECK (stock_quantity >= 0);

ALTER TABLE orders
ADD CONSTRAINT chk_order_total_non_negative
CHECK (total_amount >= 0);

ALTER TABLE reviews
ADD CONSTRAINT chk_rating_range
CHECK (rating >= 1 AND rating <= 5);

-- 3. Создаем представления (VIEWS) для отчетности
CREATE OR REPLACE VIEW sales_report AS
SELECT
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

CREATE OR REPLACE VIEW bouquet_statistics AS
SELECT
    b.id,
    b.name,
    b.price,
    c.name as category,
    b.in_stock,
    b.stock_quantity,
    COALESCE(AVG(r.rating), 0) as average_rating,
    COUNT(r.id) as reviews_count,
    COUNT(oi.id) as times_ordered,
    COALESCE(SUM(oi.quantity), 0) as total_ordered_quantity
FROM bouquets b
LEFT JOIN categories c ON b.category_id = c.id
LEFT JOIN reviews r ON b.id = r.bouquet_id AND r.is_approved = true AND r.deleted = false
LEFT JOIN order_items oi ON b.id = oi.bouquet_id
WHERE b.deleted = false
GROUP BY b.id, b.name, b.price, c.name, b.in_stock, b.stock_quantity;

CREATE OR REPLACE VIEW user_activity_report AS
SELECT
    u.id,
    u.username,
    u.email,
    u.first_name,
    u.last_name,
    COUNT(DISTINCT o.id) as total_orders,
    COALESCE(SUM(o.total_amount), 0) as total_spent,
    COUNT(DISTINCT r.id) as reviews_written,
    u.created_at as registration_date
FROM users u
LEFT JOIN orders o ON u.id = o.user_id AND o.deleted = false
LEFT JOIN reviews r ON u.id = r.user_id AND r.deleted = false
WHERE u.deleted = false
GROUP BY u.id, u.username, u.email, u.first_name, u.last_name, u.created_at;