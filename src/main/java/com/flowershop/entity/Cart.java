package com.flowershop.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
public class Cart extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    public Cart() {}

    public Cart(User user) {
        this.user = user;
    }

    public void calculateTotal() {
        this.totalAmount = items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addItem(CartItem item) {
        // Проверяем, есть ли уже такой букет в корзине
        CartItem existingItem = items.stream()
                .filter(i -> i.getBouquet().getId().equals(item.getBouquet().getId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Увеличиваем количество
            existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
            existingItem.calculateSubtotal();
        } else {
            // Добавляем новый элемент
            items.add(item);
            item.setCart(this);
        }
        calculateTotal();
    }

    public void removeItem(Long bouquetId) {
        items.removeIf(item -> item.getBouquet().getId().equals(bouquetId));
        calculateTotal();
    }

    public void clear() {
        items.clear();
        totalAmount = BigDecimal.ZERO;
    }
        public Integer getTotalItems() {
        if (items == null) return 0;
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
    // Getters and Setters
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}