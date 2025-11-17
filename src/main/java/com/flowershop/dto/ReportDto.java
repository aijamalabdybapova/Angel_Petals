// src/main/java/com/flowershop/dto/ReportDto.java
package com.flowershop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReportDto {

    public static class SalesReport {
        private String orderNumber;
        private LocalDateTime orderDate;
        private String customer;
        private BigDecimal totalAmount;
        private String status;
        private Integer itemsCount;
        private String bouquetNames;

        // Constructors, getters, setters
        public SalesReport() {}

        public SalesReport(String orderNumber, LocalDateTime orderDate, String customer,
                           BigDecimal totalAmount, String status, Integer itemsCount, String bouquetNames) {
            this.orderNumber = orderNumber;
            this.orderDate = orderDate;
            this.customer = customer;
            this.totalAmount = totalAmount;
            this.status = status;
            this.itemsCount = itemsCount;
            this.bouquetNames = bouquetNames;
        }

        // Getters and setters
        public String getOrderNumber() { return orderNumber; }
        public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

        public LocalDateTime getOrderDate() { return orderDate; }
        public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

        public String getCustomer() { return customer; }
        public void setCustomer(String customer) { this.customer = customer; }

        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public Integer getItemsCount() { return itemsCount; }
        public void setItemsCount(Integer itemsCount) { this.itemsCount = itemsCount; }

        public String getBouquetNames() { return bouquetNames; }
        public void setBouquetNames(String bouquetNames) { this.bouquetNames = bouquetNames; }
    }

    public static class BouquetStats {
        private Long id;
        private String name;
        private BigDecimal price;
        private String category;
        private Boolean inStock;
        private Integer stockQuantity;
        private Double averageRating;
        private Long reviewsCount;
        private Long timesOrdered;
        private Long totalOrderedQuantity;

        // Constructors, getters, setters
        public BouquetStats() {}

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public Boolean getInStock() { return inStock; }
        public void setInStock(Boolean inStock) { this.inStock = inStock; }

        public Integer getStockQuantity() { return stockQuantity; }
        public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

        public Double getAverageRating() { return averageRating; }
        public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

        public Long getReviewsCount() { return reviewsCount; }
        public void setReviewsCount(Long reviewsCount) { this.reviewsCount = reviewsCount; }

        public Long getTimesOrdered() { return timesOrdered; }
        public void setTimesOrdered(Long timesOrdered) { this.timesOrdered = timesOrdered; }

        public Long getTotalOrderedQuantity() { return totalOrderedQuantity; }
        public void setTotalOrderedQuantity(Long totalOrderedQuantity) { this.totalOrderedQuantity = totalOrderedQuantity; }
    }

    public static class MonthlyRevenue {
        private String monthYear;
        private BigDecimal totalRevenue;
        private Long totalOrders;
        private BigDecimal averageOrderValue;

        // Constructors, getters, setters
        public MonthlyRevenue() {}

        // Getters and setters
        public String getMonthYear() { return monthYear; }
        public void setMonthYear(String monthYear) { this.monthYear = monthYear; }

        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

        public Long getTotalOrders() { return totalOrders; }
        public void setTotalOrders(Long totalOrders) { this.totalOrders = totalOrders; }

        public BigDecimal getAverageOrderValue() { return averageOrderValue; }
        public void setAverageOrderValue(BigDecimal averageOrderValue) { this.averageOrderValue = averageOrderValue; }
    }
}