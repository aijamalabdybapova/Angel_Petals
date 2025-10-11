package com.flowershop.service.impl;

import com.flowershop.dto.OrderDto;
import com.flowershop.dto.OrderItemDto;
import com.flowershop.entity.*;
import com.flowershop.exception.ResourceNotFoundException;
import com.flowershop.repository.*;
import com.flowershop.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BouquetRepository bouquetRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            UserRepository userRepository,
                            BouquetRepository bouquetRepository,
                            OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.bouquetRepository = bouquetRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public Order createOrder(OrderDto orderDto) {
        User user = userRepository.findById(orderDto.getUserId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Order order = new Order();
        order.setUser(user);
        order.setRecipientName(orderDto.getRecipientName());
        order.setRecipientPhone(orderDto.getRecipientPhone());
        order.setRecipientEmail(orderDto.getRecipientEmail());
        order.setDeliveryAddress(orderDto.getDeliveryAddress());
        order.setDeliveryDate(orderDto.getDeliveryDate());
        order.setNotes(orderDto.getNotes());

        Order savedOrder = orderRepository.save(order);

        // Add order items
        for (OrderItemDto itemDto : orderDto.getOrderItems()) {
            Bouquet bouquet = bouquetRepository.findById(itemDto.getBouquetId())
                    .orElseThrow(() -> new RuntimeException("Букет не найден"));

            OrderItem orderItem = new OrderItem(savedOrder, bouquet, itemDto.getQuantity());
            orderItemRepository.save(orderItem);
        }

        // Calculate total
        savedOrder.calculateTotal();
        return orderRepository.save(savedOrder);
    }

    @Override
    public Order updateOrder(Long id, OrderDto orderDto) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        existingOrder.setRecipientName(orderDto.getRecipientName());
        existingOrder.setRecipientPhone(orderDto.getRecipientPhone());
        existingOrder.setRecipientEmail(orderDto.getRecipientEmail());
        existingOrder.setDeliveryAddress(orderDto.getDeliveryAddress());
        existingOrder.setDeliveryDate(orderDto.getDeliveryDate());
        existingOrder.setNotes(orderDto.getNotes());

        return orderRepository.save(existingOrder);
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));
        order.setDeleted(true);
        orderRepository.save(order);
    }

    @Override
    public void restoreOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));
        order.setDeleted(false);
        orderRepository.save(order);
    }

    @Override
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));
    }

    @Override
    public Order findByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> findAllActive() {
        return orderRepository.findAllActive();
    }

    @Override
    public List<Order> findAllDeleted() {
        return orderRepository.findAllDeleted();
    }

    @Override
    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return orderRepository.findByUser(user);
    }

    @Override
    public Page<Order> findByUserId(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }

    @Override
    public List<Order> findByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }

    @Override
    public Page<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return orderRepository.findByCreatedAtBetween(startDate, endDate, pageable);
    }

    @Override
    public List<Order> searchOrders(String search) {
        return orderRepository.searchOrders(search);
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, Order.OrderStatus status) {
        System.out.println("=== ORDER SERVICE: updateOrderStatus ===");
        System.out.println("Order ID: " + orderId);
        System.out.println("Target status: " + status);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    System.out.println("Order not found with id: " + orderId);
                    return new ResourceNotFoundException("Order not found with id: " + orderId);
                });

        System.out.println("Found order: " + order.getOrderNumber());
        System.out.println("Current status: " + order.getStatus());
        System.out.println("Updating to: " + status);

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        System.out.println("Order saved successfully. New status: " + savedOrder.getStatus());

        // Принудительно сбросим Hibernate кэш
        orderRepository.flush();
        System.out.println("Changes flushed to database");
    }

    @Override
    public Long countByStatus(Order.OrderStatus status) {
        return orderRepository.countByStatus(status);
    }
}