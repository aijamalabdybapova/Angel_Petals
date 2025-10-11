package com.flowershop.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bouquet_id", nullable = false)
    private Bouquet bouquet;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;

    // Constructors
    public OrderItem() {}

    public OrderItem(Order order, Bouquet bouquet, Integer quantity) {
        this.order = order;
        this.bouquet = bouquet;
        this.quantity = quantity;
        this.unitPrice = bouquet.getPrice();
        calculateSubtotal();
    }

    // Getters and Setters
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Bouquet getBouquet() { return bouquet; }
    public void setBouquet(Bouquet bouquet) {
        this.bouquet = bouquet;
        if (this.unitPrice == null) {
            this.unitPrice = bouquet.getPrice();
        }
    }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateSubtotal();
    }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateSubtotal();
    }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    // Helper method
    public void calculateSubtotal() {
        if (this.unitPrice != null && this.quantity != null) {
            this.subtotal = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        }
    }
}