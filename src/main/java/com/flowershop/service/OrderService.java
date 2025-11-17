package com.flowershop.service;

import com.flowershop.dto.OrderDto;
import com.flowershop.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    Order createOrder(OrderDto orderDto);
    Order updateOrder(Long id, OrderDto orderDto);
    void deleteOrder(Long id);
    void restoreOrder(Long id);
    Order findById(Long id);
    Order findByOrderNumber(String orderNumber);
    List<Order> findAll();
    List<Order> findAllActive();
    List<Order> findAllDeleted();
    Page<Order> findAll(Pageable pageable);
    List<Order> findByUserId(Long userId);
    Page<Order> findByUserId(Long userId, Pageable pageable);
    List<Order> findByStatus(Order.OrderStatus status);
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);
    Page<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    List<Order> searchOrders(String search);
    void updateOrderStatus(Long orderId, Order.OrderStatus status);
    Long countByStatus(Order.OrderStatus status);
    Order createOrderFromCart(Long userId, OrderDto orderDto);
    Order createOrderWithTransaction(OrderDto orderDto);
    Optional<Order> findByIdWithItems(Long id);
    void cancelOrder(Long orderId);
}