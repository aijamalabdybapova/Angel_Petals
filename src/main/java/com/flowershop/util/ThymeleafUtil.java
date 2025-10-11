package com.flowershop.util;

import com.flowershop.entity.Order;
import org.springframework.stereotype.Component;

@Component
public class ThymeleafUtil {

    public String getStatusBadgeClass(Order.OrderStatus status) {
        if (status == null) {
            return "bg-secondary";
        }
        switch (status) {
            case PENDING: return "bg-warning";
            case CONFIRMED: return "bg-info";
            case IN_PROGRESS: return "bg-primary";
            case COMPLETED: return "bg-success";
            case CANCELLED: return "bg-danger";
            default: return "bg-secondary";
        }
    }

    public String getStatusText(Order.OrderStatus status) {
        if (status == null) {
            return "Неизвестно";
        }
        switch (status) {
            case PENDING: return "Ожидает";
            case CONFIRMED: return "Подтвержден";
            case IN_PROGRESS: return "В процессе";
            case COMPLETED: return "Завершен";
            case CANCELLED: return "Отменен";
            default: return status.toString();
        }
    }

    public String formatPrice(Double price) {
        if (price == null) return "0.00";
        return String.format("%,.2f", price);
    }
}