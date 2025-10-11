package com.flowershop.controller.api;

import com.flowershop.dto.ApiResponse;
import com.flowershop.dto.OrderDto;
import com.flowershop.entity.Order;
import com.flowershop.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

    private final OrderService orderService;

    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Order>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success("Заказы получены успешно", orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable Long id) {
        Order order = orderService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Заказ найден", order));
    }

    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<ApiResponse<Order>> getOrderByNumber(@PathVariable String orderNumber) {
        Order order = orderService.findByOrderNumber(orderNumber);
        return ResponseEntity.ok(ApiResponse.success("Заказ найден", order));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Order>> createOrder(@Valid @RequestBody OrderDto orderDto) {
        Order order = orderService.createOrder(orderDto);
        return ResponseEntity.ok(ApiResponse.success("Заказ создан успешно", order));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> updateOrder(@PathVariable Long id,
                                                          @Valid @RequestBody OrderDto orderDto) {
        Order order = orderService.updateOrder(id, orderDto);
        return ResponseEntity.ok(ApiResponse.success("Заказ обновлен успешно", order));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok(ApiResponse.success("Заказ удален успешно"));
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restoreOrder(@PathVariable Long id) {
        orderService.restoreOrder(id);
        return ResponseEntity.ok(ApiResponse.success("Заказ восстановлен успешно"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Order>>> getOrdersByUser(@PathVariable Long userId) {
        List<Order> orders = orderService.findByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Заказы пользователя", orders));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Order>>> getOrdersByStatus(@PathVariable String status) {
        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        List<Order> orders = orderService.findByStatus(orderStatus);
        return ResponseEntity.ok(ApiResponse.success("Заказы по статусу", orders));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateOrderStatus(@PathVariable Long id,
                                                               @RequestParam String status) {
        try {
            System.out.println("=== ORDER STATUS UPDATE REQUEST ===");
            System.out.println("Order ID: " + id);
            System.out.println("New status: " + status);

            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            System.out.println("Parsed status: " + orderStatus);

            orderService.updateOrderStatus(id, orderStatus);

            System.out.println("Order status updated successfully");
            return ResponseEntity.ok(ApiResponse.success("Статус заказа обновлен"));

        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status: " + status);
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Неверный статус: " + status));
        } catch (Exception e) {
            System.out.println("Error updating order status: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Ошибка при обновлении статуса: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Order>>> searchOrders(@RequestParam String query) {
        List<Order> orders = orderService.searchOrders(query);
        return ResponseEntity.ok(ApiResponse.success("Результаты поиска заказов", orders));
    }

    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<Page<Order>>> getOrdersByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        Pageable pageable = PageRequest.of(page, size);

        Page<Order> orders = orderService.findByCreatedAtBetween(start, end, pageable);
        return ResponseEntity.ok(ApiResponse.success("Заказы по диапазону дат", orders));
    }

    @GetMapping("/stats/count-by-status")
    public ResponseEntity<ApiResponse<Long>> getOrderCountByStatus(@RequestParam String status) {
        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        Long count = orderService.countByStatus(orderStatus);
        return ResponseEntity.ok(ApiResponse.success("Количество заказов по статусу", count));
    }
}