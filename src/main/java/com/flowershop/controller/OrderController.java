package com.flowershop.controller;

import com.flowershop.entity.Order;
import com.flowershop.entity.User;
import com.flowershop.service.OrderService;
import com.flowershop.service.UserService;
import com.flowershop.util.ThymeleafUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final ThymeleafUtil thymeleafUtil;

    public OrderController(OrderService orderService, UserService userService, ThymeleafUtil thymeleafUtil) {
        this.orderService = orderService;
        this.userService = userService;
        this.thymeleafUtil = thymeleafUtil;
    }
    @ModelAttribute("getStatusBadgeClass")
    public String getStatusBadgeClass(Order.OrderStatus status) {
        if (status == null) {
            return "bg-secondary";
        }
        switch (status) {
            case PENDING: return "bg-warning";
            case CONFIRMED: return "bg-info";
            case IN_PROGRESS: return "bg-primary";
            case COMPLETED: return "bg-success";
            case CANCELLED: return "bg-danger";
            default: return "bg-secondary";
        }
    }

    @ModelAttribute("getStatusText")
    public String getStatusText(Order.OrderStatus status) {
        if (status == null) {
            return "Ожидает";
        }
        switch (status) {
            case PENDING:
                return "Ожидает";
            case CONFIRMED:
                return "Подтвержден";
            case IN_PROGRESS:
                return "В процессе";
            case COMPLETED:
                return "Завершен";
            case CANCELLED:
                return "Отменен";
            default:
                return status.toString();
        }
    }
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.findByUsername(username);
    }

    // Вспомогательные методы для проверки ролей через SecurityContext
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    private boolean isManager() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_MANAGER"));
    }

    @GetMapping
    public String listOrders(Model model,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size) {
        User currentUser = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders;

        if (isAdmin() || isManager()) {
            orders = orderService.findAll(pageable);
        } else {
            orders = orderService.findByUserId(currentUser.getId(), pageable);
        }

        model.addAttribute("orders", orders);
        return "order/list";
    }

    @GetMapping("/{id}")
    public String orderDetails(@PathVariable Long id, Model model) {
        Order order = orderService.findById(id);
        User currentUser = getCurrentUser();

        if (!isAdmin() && !isManager() && !order.getUser().getId().equals(currentUser.getId())) {
            return "redirect:/access-denied";
        }

        model.addAttribute("order", order);
        return "order/details";
    }

    @PostMapping("/{id}/status")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam String status,
                                    RedirectAttributes redirectAttributes) {
        try {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            orderService.updateOrderStatus(id, orderStatus);
            redirectAttributes.addFlashAttribute("successMessage", "Статус заказа обновлен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении статуса: " + e.getMessage());
        }
        return "redirect:/orders/" + id;
    }

    @PostMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.deleteOrder(id);
            redirectAttributes.addFlashAttribute("successMessage", "Заказ успешно удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении заказа: " + e.getMessage());
        }
        return "redirect:/orders";
    }
}