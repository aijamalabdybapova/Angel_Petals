package com.flowershop.controller;

import com.flowershop.entity.AuditLog;
import com.flowershop.entity.User;
import com.flowershop.service.AuditService;
import com.flowershop.service.BouquetService;
import com.flowershop.service.OrderService;
import com.flowershop.service.UserService;
import com.flowershop.service.ReportService;
import com.flowershop.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final OrderService orderService;
    private final BouquetService bouquetService;
    private final AuditService auditService;
    private final ReportService reportService;

    public AdminController(UserService userService,
                           OrderService orderService,
                           BouquetService bouquetService,
                           AuditService auditService,
                           ReportService reportService) {
        this.userService = userService;
        this.orderService = orderService;
        this.bouquetService = bouquetService;
        this.auditService = auditService;
        this.reportService = reportService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Get statistics
        long totalUsers = userService.findAll().size();
        long totalOrders = orderService.findAll().size();
        long totalBouquets = bouquetService.findAll().size();
        long pendingOrders = orderService.findByStatus(Order.OrderStatus.PENDING).size();

        // Get recent orders (last 5)
        var recentOrders = orderService.findAll(PageRequest.of(0, 5)).getContent();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalBouquets", totalBouquets);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("recentOrders", recentOrders);

        return "admin/dashboard";
    }

    @GetMapping("/analytics")
    public String analytics(Model model) {
        System.out.println("=== ANALYTICS PAGE REQUEST ===");

        Map<String, Object> stats = reportService.getDashboardStatistics();

        System.out.println("Statistics data:");
        stats.forEach((key, value) -> {
            System.out.println("  " + key + ": " + value);
        });

        model.addAttribute("stats", stats);
        return "admin/analytics";
    }

    @GetMapping("/audit")
    public String audit(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLog> auditLogs = auditService.getAllAuditLogs(pageable);

        model.addAttribute("auditLogs", auditLogs);
        return "admin/audit";
    }

    @GetMapping("/statistics")
    public String statistics(Model model) {
        // Используем ReportService для получения реальных данных
        Map<String, Object> dashboardStats = reportService.getDashboardStatistics();

        // Добавляем популярные букеты для отображения в таблице
        List<Map<String, Object>> popularBouquets = reportService.getPopularBouquets(5);
        List<Map<String, Object>> userActivity = reportService.getUserActivityReport();

        model.addAttribute("stats", dashboardStats);
        model.addAttribute("popularBouquets", popularBouquets);
        model.addAttribute("userActivity", userActivity);

        return "admin/statistics";
    }

    // Вспомогательные методы для аудита
    public String getActionBadgeClass(String action) {
        if (action == null) return "secondary";

        switch (action) {
            case "CREATE": return "success";
            case "UPDATE": return "warning";
            case "DELETE": return "danger";
            default: return "secondary";
        }
    }

    public String getActionText(String action) {
        if (action == null) return "Неизвестно";

        switch (action) {
            case "CREATE": return "Создание";
            case "UPDATE": return "Обновление";
            case "DELETE": return "Удаление";
            default: return action;
        }
    }

    // Вспомогательные методы для заказов
    public String getStatusBadgeClass(Order.OrderStatus status) {
        if (status == null) return "secondary";

        switch (status) {
            case PENDING: return "warning";
            case CONFIRMED: return "info";
            case IN_PROGRESS: return "primary";
            case COMPLETED: return "success";
            case CANCELLED: return "danger";
            default: return "secondary";
        }
    }

    public String getStatusText(Order.OrderStatus status) {
        if (status == null) return "Неизвестно";

        switch (status) {
            case PENDING: return "Ожидает";
            case CONFIRMED: return "Подтвержден";
            case IN_PROGRESS: return "В процессе";
            case COMPLETED: return "Завершен";
            case CANCELLED: return "Отменен";
            default: return status.toString();
        }
    }
}