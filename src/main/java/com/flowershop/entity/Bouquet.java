package com.flowershop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bouquets")
public class Bouquet extends BaseEntity {

    @NotBlank(message = "Название букета обязательно")
    @Size(max = 200, message = "Название не должно превышать 200 символов")
    @Column(nullable = false)
    private String name;

    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "Цена должна быть больше 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "in_stock", nullable = false)
    private Boolean inStock = true;

    @Column(name = "stock_quantity")
    private Integer stockQuantity = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "bouquet", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "bouquet", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    // Constructors
    public Bouquet() {}

    public Bouquet(String name, String description, BigDecimal price, Category category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Boolean getInStock() { return inStock; }
    public void setInStock(Boolean inStock) { this.inStock = inStock; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
}