package com.flowershop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDto {

    private Long userId; // Добавьте это поле

    @NotBlank(message = "Имя получателя обязательно")
    @Size(max = 100, message = "Имя получателя не должно превышать 100 символов")
    private String recipientName;

    @NotBlank(message = "Телефон получателя обязателен")
    @Size(max = 20, message = "Телефон не должен превышать 20 символов")
    private String recipientPhone;

    @Email(message = "Некорректный формат email")
    @Size(max = 255, message = "Email не должен превышать 255 символов")
    private String recipientEmail;

    @NotBlank(message = "Адрес доставки обязателен")
    @Size(max = 500, message = "Адрес доставки не должен превышать 500 символов")
    private String deliveryAddress;

    private LocalDateTime deliveryDate;

    @Size(max = 1000, message = "Примечания не должны превышать 1000 символов")
    private String notes;

    private List<OrderItemDto> orderItems = new ArrayList<>(); // Добавьте это поле

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getRecipientPhone() { return recipientPhone; }
    public void setRecipientPhone(String recipientPhone) { this.recipientPhone = recipientPhone; }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public LocalDateTime getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDateTime deliveryDate) { this.deliveryDate = deliveryDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<OrderItemDto> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItemDto> orderItems) { this.orderItems = orderItems; }
}