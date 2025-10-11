package com.flowershop.controller;

import com.flowershop.service.BouquetService;
import com.flowershop.service.OrderService;
import com.flowershop.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final OrderService orderService;
    private final BouquetService bouquetService;

    public AdminController(UserService userService, OrderService orderService, BouquetService bouquetService) {
        this.userService = userService;
        this.orderService = orderService;
        this.bouquetService = bouquetService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Get statistics
        long totalUsers = userService.findAll().size();
        long totalOrders = orderService.findAll().size();
        long totalBouquets = bouquetService.findAll().size();
        long pendingOrders = orderService.findByStatus(com.flowershop.entity.Order.OrderStatus.PENDING).size();

        // Get recent orders (last 5)
        var recentOrders = orderService.findAll(org.springframework.data.domain.PageRequest.of(0, 5)).getContent();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalBouquets", totalBouquets);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("recentOrders", recentOrders);

        return "admin/dashboard";
    }
}