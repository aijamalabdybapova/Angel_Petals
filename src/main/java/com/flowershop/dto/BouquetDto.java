package com.flowershop.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class BouquetDto {

    private Long id;

    @NotBlank(message = "Название букета обязательно")
    @Size(max = 200, message = "Название не должно превышать 200 символов")
    private String name;

    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    private String description;

    @NotNull(message = "Цена обязательна")
    @DecimalMin(value = "0.0", inclusive = false, message = "Цена должна быть больше 0")
    private BigDecimal price;

    private String imageUrl;

    @NotNull(message = "Категория обязательна")
    private Long categoryId;

    private Boolean inStock = true;
    private Integer stockQuantity = 0;

    // Constructors
    public BouquetDto() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public Boolean getInStock() { return inStock; }
    public void setInStock(Boolean inStock) { this.inStock = inStock; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
}