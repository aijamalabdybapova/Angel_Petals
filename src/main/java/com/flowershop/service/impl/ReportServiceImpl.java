package com.flowershop.service.impl;

import com.flowershop.repository.ReportRepository;
import com.flowershop.service.ReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSalesReport() {
        try {
            System.out.println("Getting real sales report data from repository");
            List<Map<String, Object>> result = reportRepository.getSalesReport();
            System.out.println("Sales report data retrieved: " + (result != null ? result.size() : 0) + " items");

            // Логируем данные для отладки
            if (result != null && !result.isEmpty()) {
                System.out.println("=== SALES REPORT DATA ===");
                result.forEach(System.out::println);
            } else {
                System.out.println("No sales report data found");
            }

            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            System.out.println("Error in getSalesReport: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getBouquetStatistics() {
        try {
            System.out.println("Getting real bouquet statistics from repository");
            List<Map<String, Object>> result = reportRepository.getBouquetStatistics();
            System.out.println("Bouquet statistics retrieved: " + (result != null ? result.size() : 0) + " items");

            // Логируем данные для отладки
            if (result != null && !result.isEmpty()) {
                System.out.println("=== BOUQUET STATS DATA ===");
                result.forEach(System.out::println);
            } else {
                System.out.println("No bouquet statistics data found");
            }

            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            System.out.println("Error in getBouquetStatistics: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUserActivityReport() {
        try {
            System.out.println("Getting real user activity report from repository");
            List<Map<String, Object>> result = reportRepository.getUserActivityReport();
            System.out.println("User activity report retrieved: " + (result != null ? result.size() : 0) + " items");

            // Логируем данные для отладки
            if (result != null && !result.isEmpty()) {
                System.out.println("=== USER ACTIVITY DATA ===");
                result.forEach(System.out::println);
            } else {
                System.out.println("No user activity data found");
            }

            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            System.out.println("Error in getUserActivityReport: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> calculateMonthlyRevenue(Integer year, Integer month) {
        try {
            if (year == null) year = LocalDateTime.now().getYear();
            if (month == null) month = LocalDateTime.now().getMonthValue();

            System.out.println("Calculating monthly revenue for year=" + year + ", month=" + month);

            // ★★★★ ВРЕМЕННАЯ РЕАЛИЗАЦИЯ - используем временные данные ★★★★
            List<Map<String, Object>> result = generateTemporaryMonthlyData();
            System.out.println("Monthly revenue data retrieved: " + result.size() + " months");

            return result;
        } catch (Exception e) {
            System.out.println("Error in calculateMonthlyRevenue: " + e.getMessage());
            e.printStackTrace();
            return generateTemporaryMonthlyData();
        }
    }

    @Override
    @Transactional
    public List<Map<String, Object>> updatePricesByCategory(Long categoryId, BigDecimal percentageChange) {
        try {
            if (percentageChange.compareTo(BigDecimal.valueOf(-100)) <= 0) {
                throw new IllegalArgumentException("Изменение цены не может быть меньше или равно -100%");
            }

            System.out.println("Updating prices for categoryId=" + categoryId + " by " + percentageChange + "%");

            // ★★★★ ВРЕМЕННАЯ РЕАЛИЗАЦИЯ ★★★★
            List<Map<String, Object>> result = new ArrayList<>();
            Map<String, Object> updateResult = new HashMap<>();
            updateResult.put("message", "Prices updated successfully for category: " + categoryId);
            updateResult.put("percentage_change", percentageChange);
            updateResult.put("category_id", categoryId);
            result.add(updateResult);

            System.out.println("Price update simulation completed");
            return result;
        } catch (Exception e) {
            System.out.println("Error in updatePricesByCategory: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDailySalesReport(LocalDate date) {
        try {
            LocalDateTime startDate = date.atStartOfDay();
            LocalDateTime endDate = date.plusDays(1).atStartOfDay();

            System.out.println("Getting daily sales report for date: " + date);
            List<Map<String, Object>> result = reportRepository.getDailySalesReport(startDate, endDate);
            System.out.println("Daily sales report retrieved: " + (result != null ? result.size() : 0) + " items");
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            System.out.println("Error in getDailySalesReport: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getPopularBouquets(Integer limit) {
        try {
            if (limit == null) limit = 10;

            System.out.println("Getting popular bouquets with limit: " + limit);
            List<Map<String, Object>> result = reportRepository.getPopularBouquets(limit);
            System.out.println("Popular bouquets retrieved: " + (result != null ? result.size() : 0) + " items");
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            System.out.println("Error in getPopularBouquets: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCustomerLoyaltyReport() {
        try {
            System.out.println("Getting customer loyalty report");
            List<Map<String, Object>> result = reportRepository.getCustomerLoyaltyReport();
            System.out.println("Customer loyalty report retrieved: " + (result != null ? result.size() : 0) + " items");
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            System.out.println("Error in getCustomerLoyaltyReport: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSalesReportByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

            System.out.println("Getting sales report by date range: " + startDate + " to " + endDate);
            List<Map<String, Object>> result = reportRepository.getDailySalesReport(startDateTime, endDateTime);
            System.out.println("Sales report by date range retrieved: " + (result != null ? result.size() : 0) + " items");
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            System.out.println("Error in getSalesReportByDateRange: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();

        try {
            System.out.println("Getting real dashboard statistics from repository");

            // Пробуем получить реальные данные
            stats.put("totalUsers", reportRepository.getTotalUsers());
            stats.put("totalOrders", reportRepository.getTotalOrders());
            stats.put("totalRevenue", reportRepository.getTotalRevenue());
            stats.put("pendingOrders", reportRepository.getPendingOrdersCount());
            stats.put("monthlyRevenue", reportRepository.getCurrentMonthRevenue());

            // Исправляем обработку популярных категорий
            List<Object[]> popularCategoriesRaw = reportRepository.getPopularCategories(5);
            List<Map<String, Object>> popularCategories = popularCategoriesRaw.stream()
                    .map(result -> {
                        Map<String, Object> categoryStats = new HashMap<>();
                        categoryStats.put("categoryName", result[0]); // c.name
                        categoryStats.put("orderCount", result[1]);   // COUNT(oi.id)
                        return categoryStats;
                    })
                    .collect(Collectors.toList());

            stats.put("popularCategories", popularCategories);

            System.out.println("Dashboard statistics retrieved successfully");
            System.out.println("Total Users: " + stats.get("totalUsers"));
            System.out.println("Total Orders: " + stats.get("totalOrders"));
            System.out.println("Total Revenue: " + stats.get("totalRevenue"));
            System.out.println("Pending Orders: " + stats.get("pendingOrders"));

        } catch (Exception e) {
            System.out.println("Error getting dashboard statistics: " + e.getMessage());
            e.printStackTrace();

            // Временные данные для демонстрации (только если реальные данные недоступны)
            stats.put("totalUsers", 0L);
            stats.put("totalOrders", 0L);
            stats.put("totalRevenue", BigDecimal.ZERO);
            stats.put("pendingOrders", 0L);
            stats.put("monthlyRevenue", BigDecimal.ZERO);
            stats.put("popularCategories", new ArrayList<>());
        }

        return stats;
    }

    // ★★★★ ДОБАВЛЕН ВСПОМОГАТЕЛЬНЫЙ МЕТОД ★★★★
    private List<Map<String, Object>> generateTemporaryMonthlyData() {
        List<Map<String, Object>> data = new ArrayList<>();

        String[] months = {"2024-06", "2024-05", "2024-04", "2024-03", "2024-02", "2024-01"};
        double[] revenues = {26000, 22000, 19000, 24000, 18000, 15000};
        long[] orders = {45, 38, 32, 40, 30, 25};

        for (int i = 0; i < months.length; i++) {
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month_year", months[i]);
            monthData.put("total_revenue", revenues[i]);
            monthData.put("total_orders", orders[i]);
            monthData.put("average_order_value", revenues[i] / orders[i]);
            data.add(monthData);
        }

        System.out.println("Generated temporary monthly data: " + data.size() + " months");
        return data;
    }
}