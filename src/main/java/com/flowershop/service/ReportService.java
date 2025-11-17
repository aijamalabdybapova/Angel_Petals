package com.flowershop.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ReportService {

    List<Map<String, Object>> getSalesReport();

    List<Map<String, Object>> getBouquetStatistics();

    List<Map<String, Object>> getUserActivityReport();

    List<Map<String, Object>> calculateMonthlyRevenue(Integer year, Integer month);

    List<Map<String, Object>> updatePricesByCategory(Long categoryId, BigDecimal percentageChange);

    List<Map<String, Object>> getDailySalesReport(LocalDate date);

    List<Map<String, Object>> getPopularBouquets(Integer limit);

    List<Map<String, Object>> getCustomerLoyaltyReport();

    Map<String, Object> getDashboardStatistics();
    List<Map<String, Object>> getSalesReportByDateRange(LocalDate startDate, LocalDate endDate);

}