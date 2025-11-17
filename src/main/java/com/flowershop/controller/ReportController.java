package com.flowershop.controller;

import com.flowershop.service.ReportService;
import com.flowershop.util.ExportUtil;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {

    private final ReportService reportService;
    private final ExportUtil exportUtil;

    public ReportController(ReportService reportService, ExportUtil exportUtil) {
        this.reportService = reportService;
        this.exportUtil = exportUtil;
    }

    // 📊 Главная страница отчетов
    @GetMapping
    public String reportsDashboard(Model model) {
        Map<String, Object> dashboardStats = reportService.getDashboardStatistics();
        model.addAttribute("stats", dashboardStats);
        return "admin/reports/dashboard";
    }

    // 📈 Отчет по продажам с фильтрами
    @GetMapping("/sales")
    public String salesReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {

        if (startDate == null) startDate = LocalDate.now().minusMonths(1);
        if (endDate == null) endDate = LocalDate.now();

        List<Map<String, Object>> salesReport = reportService.getDailySalesReport(startDate);
        model.addAttribute("salesReport", salesReport);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "admin/reports/sales";
    }

    // 🌸 Статистика букетов
    @GetMapping("/bouquets")
    public String bouquetStatistics(Model model) {
        List<Map<String, Object>> bouquetStats = reportService.getBouquetStatistics();
        List<Map<String, Object>> popularBouquets = reportService.getPopularBouquets(10);

        model.addAttribute("bouquetStats", bouquetStats);
        model.addAttribute("popularBouquets", popularBouquets);
        return "admin/reports/bouquets";
    }

    // 👥 Активность пользователей
    @GetMapping("/users")
    public String userActivityReport(Model model) {
        List<Map<String, Object>> userActivity = reportService.getUserActivityReport();
        List<Map<String, Object>> loyaltyReport = reportService.getCustomerLoyaltyReport();

        model.addAttribute("userActivity", userActivity);
        model.addAttribute("loyaltyReport", loyaltyReport);
        return "admin/reports/users";
    }

    // 💰 Выручка по месяцам
    @GetMapping("/revenue")
    public String revenueReport(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model) {

        List<Map<String, Object>> monthlyRevenue = reportService.calculateMonthlyRevenue(year, month);
        model.addAttribute("monthlyRevenue", monthlyRevenue);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedMonth", month);

        return "admin/reports/revenue";
    }

    // 📥 Экспорт отчетов в CSV
    @GetMapping("/export/sales")
    public ResponseEntity<byte[]> exportSalesReport() {
        try {
            List<Map<String, Object>> salesData = reportService.getSalesReport();
            InputStream csvStream = exportUtil.exportSalesToCsv(salesData);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sales-report.csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(csvStream.readAllBytes());

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/export/users")
    public ResponseEntity<byte[]> exportUsersReport() {
        try {
            List<Map<String, Object>> usersData = reportService.getUserActivityReport();
            InputStream csvStream = exportUtil.exportUsersToCsv(usersData);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users-report.csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(csvStream.readAllBytes());

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}