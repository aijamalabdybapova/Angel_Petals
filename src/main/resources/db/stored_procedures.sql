-- 1. Процедура для создания заказа с проверкой наличия товара
CREATE OR REPLACE FUNCTION create_complete_order(
    p_user_id BIGINT,
    p_recipient_name VARCHAR(100),
    p_recipient_phone VARCHAR(20),
    p_recipient_email VARCHAR(255),
    p_delivery_address TEXT,
    p_delivery_date TIMESTAMP,
    p_notes TEXT DEFAULT NULL
) RETURNS TABLE(
    order_id BIGINT,
    order_number VARCHAR(50),
    total_amount DECIMAL(10,2),
    status VARCHAR(20)
) AS $$
DECLARE
    v_cart_id BIGINT;
    v_order_id BIGINT;
    v_order_number VARCHAR(50);
    v_cart_item RECORD;
    v_bouquet_stock INTEGER;
    v_subtotal DECIMAL(10,2);
    v_total DECIMAL(10,2) := 0;
BEGIN
    -- Получаем корзину пользователя
    SELECT id INTO v_cart_id FROM carts WHERE user_id = p_user_id;

    IF v_cart_id IS NULL THEN
        RAISE EXCEPTION 'Корзина пользователя не найдена';
    END IF;

    -- Проверяем наличие товаров и резервируем
    FOR v_cart_item IN
        SELECT ci.bouquet_id, ci.quantity, b.price, b.stock_quantity, b.name
        FROM cart_items ci
        JOIN bouquets b ON ci.bouquet_id = b.id
        WHERE ci.cart_id = v_cart_id
    LOOP
        IF v_cart_item.stock_quantity < v_cart_item.quantity THEN
            RAISE EXCEPTION 'Недостаточно товара "%". В наличии: %, запрошено: %',
                v_cart_item.name, v_cart_item.stock_quantity, v_cart_item.quantity;
        END IF;

        -- Резервируем товар
        UPDATE bouquets
        SET stock_quantity = stock_quantity - v_cart_item.quantity
        WHERE id = v_cart_item.bouquet_id;
    END LOOP;

    -- Создаем номер заказа
    v_order_number := 'ORD-' || to_char(CURRENT_TIMESTAMP, 'YYYYMMDDHH24MISS') || '-' || p_user_id;

    -- Создаем заказ
    INSERT INTO orders (
        order_number, user_id, recipient_name, recipient_phone,
        recipient_email, delivery_address, delivery_date, notes, status
    ) VALUES (
        v_order_number, p_user_id, p_recipient_name, p_recipient_phone,
        p_recipient_email, p_delivery_address, p_delivery_date, p_notes, 'PENDING'
    ) RETURNING id INTO v_order_id;

    -- Переносим товары из корзины в заказ
    FOR v_cart_item IN
        SELECT ci.bouquet_id, ci.quantity, b.price
        FROM cart_items ci
        JOIN bouquets b ON ci.bouquet_id = b.id
        WHERE ci.cart_id = v_cart_id
    LOOP
        v_subtotal := v_cart_item.quantity * v_cart_item.price;
        v_total := v_total + v_subtotal;

        INSERT INTO order_items (order_id, bouquet_id, quantity, unit_price, subtotal)
        VALUES (v_order_id, v_cart_item.bouquet_id, v_cart_item.quantity, v_cart_item.price, v_subtotal);
    END LOOP;

    -- Обновляем общую сумму заказа
    UPDATE orders SET total_amount = v_total WHERE id = v_order_id;

    -- Очищаем корзину
    DELETE FROM cart_items WHERE cart_id = v_cart_id;
    UPDATE carts SET total_amount = 0 WHERE id = v_cart_id;

    -- Возвращаем результат
    RETURN QUERY SELECT v_order_id, v_order_number, v_total, 'PENDING'::VARCHAR;
END;
$$ LANGUAGE plpgsql;

-- 2. Процедура для расчета месячной выручки
CREATE OR REPLACE FUNCTION calculate_monthly_revenue(
    p_year INTEGER,
    p_month INTEGER
) RETURNS TABLE(
    month_year TEXT,
    total_revenue DECIMAL(15,2),
    total_orders BIGINT,
    average_order_value DECIMAL(10,2)
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        to_char(date_trunc('month', o.created_at), 'YYYY-MM') as month_year,
        COALESCE(SUM(o.total_amount), 0) as total_revenue,
        COUNT(o.id) as total_orders,
        CASE
            WHEN COUNT(o.id) > 0 THEN SUM(o.total_amount) / COUNT(o.id)
            ELSE 0
        END as average_order_value
    FROM orders o
    WHERE EXTRACT(YEAR FROM o.created_at) = p_year
        AND EXTRACT(MONTH FROM o.created_at) = p_month
        AND o.deleted = false
        AND o.status = 'COMPLETED'
    GROUP BY date_trunc('month', o.created_at);
END;
$$ LANGUAGE plpgsql;

-- 3. Процедура для массового обновления цен по категории
CREATE OR REPLACE FUNCTION update_prices_by_category(
    p_category_id BIGINT,
    p_percentage_change DECIMAL(5,2)
) RETURNS TABLE(
    bouquet_id BIGINT,
    bouquet_name VARCHAR(200),
    old_price DECIMAL(10,2),
    new_price DECIMAL(10,2)
) AS $$
DECLARE
    v_bouquet RECORD;
    v_old_price DECIMAL(10,2);
    v_new_price DECIMAL(10,2);
BEGIN
    FOR v_bouquet IN
        SELECT id, name, price
        FROM bouquets
        WHERE category_id = p_category_id
        AND deleted = false
    LOOP
        v_old_price := v_bouquet.price;
        v_new_price := ROUND(v_bouquet.price * (1 + p_percentage_change / 100), 2);

        UPDATE bouquets
        SET price = v_new_price,
            updated_at = CURRENT_TIMESTAMP
        WHERE id = v_bouquet.id;

        bouquet_id := v_bouquet.id;
        bouquet_name := v_bouquet.name;
        old_price := v_old_price;
        new_price := v_new_price;
        RETURN NEXT;
    END LOOP;
END;
$$ LANGUAGE plpgsql;