package com.flowershop.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartDto {
    private Long id;
    private Long userId;
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private List<CartItemDto> items = new ArrayList<>();
    private Integer totalItems = 0;

    // Constructors
    public CartDto() {}

    public CartDto(Long userId) {
        this.userId = userId;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public List<CartItemDto> getItems() { return items; }
    public void setItems(List<CartItemDto> items) {
        this.items = items;
        this.totalItems = items.stream()
                .mapToInt(CartItemDto::getQuantity)
                .sum();
    }

    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }
}