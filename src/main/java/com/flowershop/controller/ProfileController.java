package com.flowershop.controller;

import com.flowershop.entity.Order;
import com.flowershop.entity.User;
import com.flowershop.service.OrderService;
import com.flowershop.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final OrderService orderService;

    public ProfileController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping
    public String showProfile(Model model, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            // Получаем статистику заказов пользователя
            List<Order> userOrders = orderService.findByUserId(user.getId());
            long orderCount = userOrders.size();
            long completedOrders = userOrders.stream()
                    .filter(order -> order.getStatus() == Order.OrderStatus.COMPLETED)
                    .count();
            long pendingOrders = userOrders.stream()
                    .filter(order -> order.getStatus() == Order.OrderStatus.PENDING)
                    .count();

            model.addAttribute("user", user);
            model.addAttribute("orderCount", orderCount);
            model.addAttribute("completedOrders", completedOrders);
            model.addAttribute("pendingOrders", pendingOrders);

            return "user/profile";

        } catch (Exception e) {
            // Логируем ошибку для отладки
            System.out.println("Error in showProfile: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Ошибка при загрузке профиля: " + e.getMessage());
            return "error/error";
        }
    }

    @PostMapping("/update")
    public String updateProfile(User updatedUser,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            User currentUser = userService.findByUsername(username);

            // Обновляем только разрешенные поля (защита от null)
            if (updatedUser.getFirstName() != null) {
                currentUser.setFirstName(updatedUser.getFirstName());
            }
            if (updatedUser.getLastName() != null) {
                currentUser.setLastName(updatedUser.getLastName());
            }
            if (updatedUser.getEmail() != null) {
                currentUser.setEmail(updatedUser.getEmail());
            }
            if (updatedUser.getPhoneNumber() != null) {
                currentUser.setPhoneNumber(updatedUser.getPhoneNumber());
            }
            if (updatedUser.getAddress() != null) {
                currentUser.setAddress(updatedUser.getAddress());
            }

            userService.updateUser(currentUser.getId(), currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Профиль успешно обновлен");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении профиля: " + e.getMessage());
        }

        return "redirect:/profile";
    }

    // Вспомогательные методы для Thymeleaf с защитой от null
    @org.springframework.web.bind.annotation.ModelAttribute("getRoleBadgeClass")
    public String getRoleBadgeClass(String role) {
        if (role == null) {
            return "secondary";
        }
        switch (role) {
            case "ROLE_ADMIN": return "danger";
//            case "ROLE_MANAGER": return "warning";
            case "ROLE_USER": return "primary";
            default: return "secondary";
        }
    }

    @org.springframework.web.bind.annotation.ModelAttribute("getRoleText")
    public String getRoleText(String role) {
        if (role == null) {
            return "Неизвестно";
        }
        switch (role) {
            case "ROLE_ADMIN": return "Админ";
//            case "ROLE_MANAGER": return "Менеджер";
            case "ROLE_USER": return "Пользователь";
            default: return role;
        }
    }
}