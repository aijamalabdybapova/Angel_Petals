package com.flowershop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "reviews")
public class Review extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bouquet_id", nullable = false)
    private Bouquet bouquet;

    @Min(value = 1, message = "Рейтинг должен быть от 1 до 5")
    @Max(value = 5, message = "Рейтинг должен быть от 1 до 5")
    @Column(nullable = false)
    private Integer rating;

    @NotBlank(message = "Текст отзыва обязателен")
    @Size(max = 1000, message = "Текст отзыва не должен превышать 1000 символов")
    @Column(nullable = false, length = 1000)
    private String comment;

    @Column(name = "is_approved")
    private Boolean isApproved = false;

    // Constructors
    public Review() {}

    public Review(User user, Bouquet bouquet, Integer rating, String comment) {
        this.user = user;
        this.bouquet = bouquet;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and Setters
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Bouquet getBouquet() { return bouquet; }
    public void setBouquet(Bouquet bouquet) { this.bouquet = bouquet; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Boolean getIsApproved() { return isApproved; }
    public void setIsApproved(Boolean isApproved) { this.isApproved = isApproved; }
}