package com.flowershop.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

public class CartItemDto {
    private Long id;

    @NotNull(message = "Букет обязателен")
    private Long bouquetId;

    @NotNull(message = "Количество обязательно")
    @Min(value = 1, message = "Количество должно быть не менее 1")
    private Integer quantity;

    private String bouquetName;
    private String bouquetImage;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    // Constructors
    public CartItemDto() {}

    public CartItemDto(Long bouquetId, Integer quantity) {
        this.bouquetId = bouquetId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBouquetId() { return bouquetId; }
    public void setBouquetId(Long bouquetId) { this.bouquetId = bouquetId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getBouquetName() { return bouquetName; }
    public void setBouquetName(String bouquetName) { this.bouquetName = bouquetName; }

    public String getBouquetImage() { return bouquetImage; }
    public void setBouquetImage(String bouquetImage) { this.bouquetImage = bouquetImage; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}