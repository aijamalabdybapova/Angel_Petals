package com.flowershop.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public class AddToCartRequest {

    @NotNull(message = "ID букета обязателен")
    private Long bouquetId;

    @NotNull(message = "Количество обязательно")
    @Min(value = 1, message = "Количество должно быть не менее 1")
    private Integer quantity;

    // Конструкторы
    public AddToCartRequest() {}

    public AddToCartRequest(Long bouquetId, Integer quantity) {
        this.bouquetId = bouquetId;
        this.quantity = quantity;
    }

    // Геттеры и сеттеры
    public Long getBouquetId() {
        return bouquetId;
    }

    public void setBouquetId(Long bouquetId) {
        this.bouquetId = bouquetId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}