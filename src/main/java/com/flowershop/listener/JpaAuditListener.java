package com.flowershop.listener;

import com.flowershop.entity.*;
import com.flowershop.service.AuditService;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JpaAuditListener {

    private static AuditService auditService;

    @Autowired
    public void setAuditService(@Lazy AuditService auditService) {
        JpaAuditListener.auditService = auditService;
    }

    @PostPersist
    public void afterCreate(Object entity) {
        if (shouldAudit(entity)) {
            String username = getCurrentUsername();
            auditService.logAction(
                    getTableName(entity),
                    getId(entity),
                    "CREATE",
                    null,
                    getEntitySummary(entity),
                    username
            );
        }
    }

    @PostUpdate
    public void afterUpdate(Object entity) {
        if (shouldAudit(entity)) {
            String username = getCurrentUsername();
            auditService.logAction(
                    getTableName(entity),
                    getId(entity),
                    "UPDATE",
                    "Object updated",
                    getEntitySummary(entity),
                    username
            );
        }
    }

    @PostRemove
    public void afterDelete(Object entity) {
        if (shouldAudit(entity)) {
            String username = getCurrentUsername();
            auditService.logAction(
                    getTableName(entity),
                    getId(entity),
                    "DELETE",
                    getEntitySummary(entity),
                    null,
                    username
            );
        }
    }

    private boolean shouldAudit(Object entity) {
        // Аудируем все основные сущности
        return entity instanceof User ||
                entity instanceof Bouquet ||
                entity instanceof Order ||
                entity instanceof Category;
    }

    private String getTableName(Object entity) {
        if (entity instanceof User) return "users";
        if (entity instanceof Bouquet) return "bouquets";
        if (entity instanceof Order) return "orders";
        if (entity instanceof Category) return "categories";
        return entity.getClass().getSimpleName().toLowerCase();
    }

    private Long getId(Object entity) {
        try {
            if (entity instanceof User) return ((User) entity).getId();
            if (entity instanceof Bouquet) return ((Bouquet) entity).getId();
            if (entity instanceof Order) return ((Order) entity).getId();
            if (entity instanceof Category) return ((Category) entity).getId();
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private String getEntitySummary(Object entity) {
        try {
            if (entity instanceof User) {
                User user = (User) entity;
                return String.format("{\"username\": \"%s\", \"email\": \"%s\", \"firstName\": \"%s\", \"lastName\": \"%s\"}",
                        user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName());
            }
            if (entity instanceof Bouquet) {
                Bouquet bouquet = (Bouquet) entity;
                return String.format("{\"name\": \"%s\", \"price\": %.2f, \"inStock\": %s}",
                        bouquet.getName(), bouquet.getPrice(), bouquet.getInStock());
            }
            if (entity instanceof Order) {
                Order order = (Order) entity;
                return String.format("{\"orderNumber\": \"%s\", \"status\": \"%s\", \"totalAmount\": %.2f}",
                        order.getOrderNumber(), order.getStatus(), order.getTotalAmount());
            }
            if (entity instanceof Category) {
                Category category = (Category) entity;
                return String.format("{\"name\": \"%s\"}", category.getName());
            }
            return entity.toString();
        } catch (Exception e) {
            return "{\"error\": \"Failed to get entity summary\"}";
        }
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            } else {
                return principal.toString();
            }
        }
        return "system";
    }
}