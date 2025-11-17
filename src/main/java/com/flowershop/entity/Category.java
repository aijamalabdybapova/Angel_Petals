package com.flowershop.entity;

import com.flowershop.listener.JpaAuditListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@EntityListeners(JpaAuditListener.class)
public class Category extends BaseEntity {

    @NotBlank(message = "Название категории обязательно")
    @Size(max = 100, message = "Название категории не должно превышать 100 символов")
    @Column(unique = true, nullable = false)
    private String name;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false; // значение по умолчанию

    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    private String description;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Bouquet> bouquets = new ArrayList<>();

    // Constructors
    public Category() {}

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Bouquet> getBouquets() { return bouquets; }
    public void setBouquets(List<Bouquet> bouquets) { this.bouquets = bouquets; }
}