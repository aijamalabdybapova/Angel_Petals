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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BouquetRepository bouquetRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            UserRepository userRepository,
                            BouquetRepository bouquetRepository,
                            OrderItemRepository orderItemRepository,
                            CartRepository cartRepository,
                            CartItemRepository cartItemRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.bouquetRepository = bouquetRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findByIdWithItems(Long id) {
        return orderRepository.findByIdWithItems(id);
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
    @Transactional(readOnly = true)
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));
    }

    @Override
    @Transactional(readOnly = true)
    public Order findByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findAllActive() {
        return orderRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findAllDeleted() {
        return orderRepository.findAllDeleted();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return orderRepository.findByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> findByUserId(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return orderRepository.findByCreatedAtBetween(startDate, endDate, pageable);
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional
    public Order createOrderFromCart(Long userId, OrderDto orderDto) {
        try {
            System.out.println("=== CREATING ORDER FROM CART ===");
            System.out.println("User ID: " + userId);
            System.out.println("Order DTO: " + orderDto);

            // Получаем пользователя
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            // Получаем корзину пользователя
            Cart cart = cartRepository.findByUserIdWithItems(userId)
                    .orElseThrow(() -> new RuntimeException("Корзина не найдена"));

            if (cart.getItems().isEmpty()) {
                throw new RuntimeException("Корзина пуста");
            }

            // Создаем заказ
            Order order = new Order();
            order.setUser(user);
            order.setRecipientName(orderDto.getRecipientName());
            order.setRecipientPhone(orderDto.getRecipientPhone());
            order.setRecipientEmail(orderDto.getRecipientEmail());
            order.setDeliveryAddress(orderDto.getDeliveryAddress());
            order.setDeliveryDate(orderDto.getDeliveryDate());
            order.setNotes(orderDto.getNotes());
            order.setStatus(Order.OrderStatus.PENDING);

            // Переносим товары из корзины в заказ
            for (CartItem cartItem : cart.getItems()) {
                Bouquet bouquet = cartItem.getBouquet();

                // Проверяем наличие
                if (!bouquet.getInStock() || bouquet.getStockQuantity() < cartItem.getQuantity()) {
                    throw new RuntimeException("Недостаточно товара: " + bouquet.getName() +
                            ". В наличии: " + bouquet.getStockQuantity() +
                            ", запрошено: " + cartItem.getQuantity());
                }

                // Обновляем остатки
                bouquet.setStockQuantity(bouquet.getStockQuantity() - cartItem.getQuantity());
                if (bouquet.getStockQuantity() <= 0) {
                    bouquet.setInStock(false);
                }
                bouquetRepository.save(bouquet);

                // Создаем элемент заказа
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setBouquet(bouquet);
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setUnitPrice(cartItem.getUnitPrice());
                orderItem.calculateSubtotal();
                order.getOrderItems().add(orderItem);
            }

            // Рассчитываем общую сумму
            order.calculateTotal();

            // Сохраняем заказ
            Order savedOrder = orderRepository.save(order);

            // Очищаем корзину
            cart.getItems().clear();
            cart.setTotalAmount(BigDecimal.ZERO);
            cartRepository.save(cart);
            cartItemRepository.deleteByCartId(cart.getId());

            System.out.println("Order created successfully: " + savedOrder.getOrderNumber());
            return savedOrder;

        } catch (Exception e) {
            System.out.println("Error creating order from cart: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Ошибка при создании заказа: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Order createOrderWithTransaction(OrderDto orderDto) {
        // Начинаем транзакцию
        try {
            // Валидация данных
            if (orderDto.getOrderItems() == null || orderDto.getOrderItems().isEmpty()) {
                throw new RuntimeException("Заказ должен содержать хотя бы один товар");
            }

            User user = userRepository.findById(orderDto.getUserId())
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            // Проверяем наличие всех товаров
            for (OrderItemDto itemDto : orderDto.getOrderItems()) {
                Bouquet bouquet = bouquetRepository.findById(itemDto.getBouquetId())
                        .orElseThrow(() -> new RuntimeException("Букет не найден: " + itemDto.getBouquetId()));

                if (!bouquet.getInStock() || bouquet.getStockQuantity() < itemDto.getQuantity()) {
                    throw new RuntimeException("Недостаточно товара: " + bouquet.getName() +
                            ". В наличии: " + bouquet.getStockQuantity() +
                            ", запрошено: " + itemDto.getQuantity());
                }
            }

            // Создаем заказ
            Order order = new Order();
            order.setUser(user);
            order.setRecipientName(orderDto.getRecipientName());
            order.setRecipientPhone(orderDto.getRecipientPhone());
            order.setRecipientEmail(orderDto.getRecipientEmail());
            order.setDeliveryAddress(orderDto.getDeliveryAddress());
            order.setDeliveryDate(orderDto.getDeliveryDate());
            order.setNotes(orderDto.getNotes());

            Order savedOrder = orderRepository.save(order);

            BigDecimal totalAmount = BigDecimal.ZERO;

            // Добавляем товары и обновляем остатки
            for (OrderItemDto itemDto : orderDto.getOrderItems()) {
                Bouquet bouquet = bouquetRepository.findById(itemDto.getBouquetId())
                        .orElseThrow(() -> new RuntimeException("Букет не найден"));

                // Создаем элемент заказа
                OrderItem orderItem = new OrderItem(savedOrder, bouquet, itemDto.getQuantity());
                orderItemRepository.save(orderItem);

                // Обновляем остатки
                bouquet.setStockQuantity(bouquet.getStockQuantity() - itemDto.getQuantity());
                if (bouquet.getStockQuantity() <= 0) {
                    bouquet.setInStock(false);
                }
                bouquetRepository.save(bouquet);

                totalAmount = totalAmount.add(orderItem.getSubtotal());
            }

            // Обновляем общую сумму
            savedOrder.setTotalAmount(totalAmount);
            return orderRepository.save(savedOrder);

        } catch (Exception e) {
            // В случае ошибки транзакция откатится автоматически благодаря @Transactional
            throw new RuntimeException("Ошибка при создании заказа: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Заказ не найден"));

            if (order.getStatus() == Order.OrderStatus.COMPLETED) {
                throw new RuntimeException("Нельзя отменить завершенный заказ");
            }

            // Возвращаем товары на склад
            for (OrderItem item : order.getOrderItems()) {
                Bouquet bouquet = item.getBouquet();
                bouquet.setStockQuantity(bouquet.getStockQuantity() + item.getQuantity());
                if (bouquet.getStockQuantity() > 0) {
                    bouquet.setInStock(true);
                }
                bouquetRepository.save(bouquet);
            }

            // Меняем статус заказа
            order.setStatus(Order.OrderStatus.CANCELLED);
            orderRepository.save(order);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при отмене заказа: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByStatus(Order.OrderStatus status) {
        return orderRepository.countByStatus(status);
    }
}