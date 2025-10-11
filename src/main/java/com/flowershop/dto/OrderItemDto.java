package com.flowershop.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class OrderItemDto {

    @NotNull
    private Long bouquetId;

    @NotNull
    private Integer quantity;

    private String bouquetName;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    // Constructors
    public OrderItemDto() {}

    // Getters and Setters
    public Long getBouquetId() { return bouquetId; }
    public void setBouquetId(Long bouquetId) { this.bouquetId = bouquetId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getBouquetName() { return bouquetName; }
    public void setBouquetName(String bouquetName) { this.bouquetName = bouquetName; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}