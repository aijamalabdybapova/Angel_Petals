package com.flowershop.controller.api;

import com.flowershop.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportApiController {

    private final ReportService reportService;

    public ReportApiController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/monthly-revenue")
    public ResponseEntity<Map<String, Object>> getMonthlyRevenue() {
        try {
            System.out.println("=== MONTHLY REVENUE API CALLED ===");

            // Временная реализация пока метод в сервисе не готов
            List<Map<String, Object>> data = generateMonthlyRevenueData();

            System.out.println("Returning monthly revenue data: " + data.size() + " months");

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Error in monthly-revenue: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Ошибка при получении данных: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/user-activity")
    public ResponseEntity<Map<String, Object>> getUserActivity() {
        try {
            // Используем реальные данные из сервиса
            List<Map<String, Object>> data = reportService.getUserActivityReport();

            // Логируем для отладки
            System.out.println("=== REAL USER ACTIVITY DATA ===");
            if (data != null) {
                data.forEach(item -> System.out.println(item));
            } else {
                System.out.println("No data returned from service");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data != null ? data : List.of());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Error in user-activity: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Ошибка при получении данных пользователей: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/bouquet-stats")
    public ResponseEntity<Map<String, Object>> getBouquetStats() {
        try {
            // Используем реальные данные из сервиса
            List<Map<String, Object>> data = reportService.getBouquetStatistics();

            // Логируем для отладки
            System.out.println("=== REAL BOUQUET STATS DATA ===");
            if (data != null) {
                data.forEach(item -> System.out.println(item));
            } else {
                System.out.println("No data returned from service");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data != null ? data : List.of());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Error in bouquet-stats: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Ошибка при получении статистики букетов: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/sales")
    public ResponseEntity<Map<String, Object>> getSalesReport() {
        try {
            // Используем реальные данные из сервиса
            List<Map<String, Object>> data = reportService.getSalesReport();

            // Логируем для отладки
            System.out.println("=== REAL SALES REPORT DATA ===");
            if (data != null) {
                data.forEach(item -> System.out.println(item));
            } else {
                System.out.println("No data returned from service");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data != null ? data : List.of());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Error in sales report: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Ошибка при получении отчета по продажам: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }



    // Временный метод для генерации данных
    private List<Map<String, Object>> generateMonthlyRevenueData() {
        List<Map<String, Object>> data = new ArrayList<>();

        // Генерируем данные за последние 6 месяцев
        String[] months = {"Январь 2024", "Февраль 2024", "Март 2024", "Апрель 2024", "Май 2024", "Июнь 2024"};
        int[] revenues = {15000, 18000, 24000, 19000, 22000, 26000};
        int[] orders = {25, 30, 40, 32, 38, 45};

        for (int i = 0; i < months.length; i++) {
            data.add(Map.of(
                    "month_year", months[i],
                    "total_revenue", revenues[i],
                    "total_orders", orders[i],
                    "average_order_value", revenues[i] / orders[i]
            ));
        }

        return data;
    }
}

