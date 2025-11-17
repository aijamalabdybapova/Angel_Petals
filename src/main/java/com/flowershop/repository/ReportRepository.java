package com.flowershop.repository;

import com.flowershop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface ReportRepository extends JpaRepository<Order, Long> {

    // ★★★★ ИСПРАВЛЕННЫЕ МЕТОДЫ - прямые запросы вместо VIEW ★★★★

    @Query(value = """
        SELECT 
            o.order_number as orderNumber,
            o.created_at as orderDate,
            u.username as customer,
            o.total_amount as totalAmount,
            o.status,
            COUNT(oi.id) as itemsCount,
            GROUP_CONCAT(b.name SEPARATOR ', ') as bouquetNames
        FROM orders o
        JOIN users u ON o.user_id = u.id
        JOIN order_items oi ON o.id = oi.order_id
        JOIN bouquets b ON oi.bouquet_id = b.id
        WHERE o.deleted = false
        GROUP BY o.id, o.order_number, o.created_at, u.username, o.total_amount, o.status
        ORDER BY o.created_at DESC
    """, nativeQuery = true)
    List<Map<String, Object>> getSalesReport();

    @Query(value = """
        SELECT 
            b.id,
            b.name,
            b.price,
            c.name as category,
            COUNT(oi.id) as times_ordered,
            COALESCE(SUM(oi.quantity), 0) as total_quantity,
            COALESCE(AVG(r.rating), 0) as average_rating
        FROM bouquets b
        LEFT JOIN categories c ON b.category_id = c.id
        LEFT JOIN order_items oi ON b.id = oi.bouquet_id
        LEFT JOIN reviews r ON b.id = r.bouquet_id AND r.deleted = false
        WHERE b.deleted = false
        GROUP BY b.id, b.name, b.price, c.name
        ORDER BY total_quantity DESC
    """, nativeQuery = true)
    List<Map<String, Object>> getBouquetStatistics();

    @Query(value = """
        SELECT 
            u.id,
            u.username,
            u.email,
            u.first_name as firstName,
            u.last_name as lastName,
            COUNT(o.id) as total_orders,
            COALESCE(SUM(o.total_amount), 0) as total_spent,
            u.created_at as registrationDate
        FROM users u
        LEFT JOIN orders o ON u.id = o.user_id AND o.deleted = false
        WHERE u.deleted = false
        GROUP BY u.id, u.username, u.email, u.first_name, u.last_name, u.created_at
        HAVING COUNT(o.id) > 0
        ORDER BY total_spent DESC
    """, nativeQuery = true)
    List<Map<String, Object>> getUserActivityReport();

    // ★★★★ ВРЕМЕННО ЗАКОММЕНТИРУЕМ - они используют PostgreSQL функции ★★★★
    /*
    @Query(value = "SELECT * FROM calculate_monthly_revenue(:year, :month)", nativeQuery = true)
    List<Map<String, Object>> calculateMonthlyRevenue(@Param("year") Integer year, @Param("month") Integer month);

    @Query(value = "SELECT * FROM update_prices_by_category(:categoryId, :percentageChange)", nativeQuery = true)
    List<Map<String, Object>> updatePricesByCategory(@Param("categoryId") Long categoryId,
                                                     @Param("percentageChange") BigDecimal percentageChange);
    */

    // ★★★★ НОВЫЕ МЕТОДЫ ДЛЯ ReportServiceImpl ★★★★
    @Query(value = """
        SELECT 
            o.order_number as orderNumber,
            o.created_at as orderDate,
            u.username as customer,
            o.total_amount as totalAmount,
            o.status,
            COUNT(oi.id) as itemsCount,
            GROUP_CONCAT(b.name SEPARATOR ', ') as bouquetNames
        FROM orders o
        JOIN users u ON o.user_id = u.id
        JOIN order_items oi ON o.id = oi.order_id
        JOIN bouquets b ON oi.bouquet_id = b.id
        WHERE o.created_at BETWEEN :startDate AND :endDate
        AND o.deleted = false
        GROUP BY o.id, o.order_number, o.created_at, u.username, o.total_amount, o.status
        ORDER BY o.created_at DESC
    """, nativeQuery = true)
    List<Map<String, Object>> getDailySalesReport(@Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    @Query(value = """
        SELECT 
            b.id,
            b.name,
            b.price,
            c.name as category,
            COUNT(oi.id) as times_ordered,
            COALESCE(SUM(oi.quantity), 0) as total_quantity
        FROM bouquets b
        LEFT JOIN categories c ON b.category_id = c.id
        LEFT JOIN order_items oi ON b.id = oi.bouquet_id
        WHERE b.deleted = false
        GROUP BY b.id, b.name, b.price, c.name
        ORDER BY total_quantity DESC
        LIMIT :limit
    """, nativeQuery = true)
    List<Map<String, Object>> getPopularBouquets(@Param("limit") Integer limit);

    @Query(value = """
        SELECT 
            u.id,
            u.username,
            u.email,
            u.first_name as firstName,
            u.last_name as lastName,
            COUNT(o.id) as total_orders,
            COALESCE(SUM(o.total_amount), 0) as total_spent,
            u.created_at as registrationDate
        FROM users u
        LEFT JOIN orders o ON u.id = o.user_id AND o.deleted = false
        WHERE u.deleted = false
        GROUP BY u.id, u.username, u.email, u.first_name, u.last_name, u.created_at
        HAVING COUNT(o.id) > 0
        ORDER BY total_spent DESC
    """, nativeQuery = true)
    List<Map<String, Object>> getCustomerLoyaltyReport();

    // Методы для статистики дашборда
    @Query("SELECT COUNT(u) FROM User u WHERE u.deleted = false")
    Long getTotalUsers();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.deleted = false")
    Long getTotalOrders();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.deleted = false AND o.status = 'COMPLETED'")
    BigDecimal getTotalRevenue();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.deleted = false AND o.status = 'PENDING'")
    Long getPendingOrdersCount();

    @Query(value = """
        SELECT COALESCE(SUM(total_amount), 0) 
        FROM orders 
        WHERE deleted = false 
        AND status = 'COMPLETED'
        AND EXTRACT(YEAR FROM created_at) = EXTRACT(YEAR FROM CURRENT_DATE)
        AND EXTRACT(MONTH FROM created_at) = EXTRACT(MONTH FROM CURRENT_DATE)
    """, nativeQuery = true)
    BigDecimal getCurrentMonthRevenue();

    // ★★★★ ИСПРАВЛЕННЫЙ МЕТОД - возвращает List<Object[]> ★★★★
    @Query(value = """
        SELECT 
            c.name as categoryName,
            COUNT(oi.id) as orderCount
        FROM categories c
        LEFT JOIN bouquets b ON c.id = b.category_id AND b.deleted = false
        LEFT JOIN order_items oi ON b.id = oi.bouquet_id
        LEFT JOIN orders o ON oi.order_id = o.id AND o.deleted = false
        WHERE c.deleted = false
        GROUP BY c.id, c.name
        ORDER BY orderCount DESC
        LIMIT :limit
    """, nativeQuery = true)
    List<Object[]> getPopularCategories(@Param("limit") Integer limit);
}