package com.flowershop.repository;

import com.flowershop.entity.Order;
import com.flowershop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByStatus(Order.OrderStatus status);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    @Query("SELECT o FROM Order o WHERE o.deleted = false")
    List<Order> findAllActive();

    @Query("SELECT o FROM Order o WHERE o.deleted = true")
    List<Order> findAllDeleted();

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.deleted = false")
    Page<Order> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.deleted = false")
    Page<Order> findByStatus(@Param("status") Order.OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate AND o.deleted = false")
    Page<Order> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE LOWER(o.recipientName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Order> searchOrders(@Param("search") String search);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status AND o.deleted = false")
    Long countByStatus(@Param("status") Order.OrderStatus status);
}