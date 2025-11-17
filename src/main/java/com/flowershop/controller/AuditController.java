package com.flowershop.controller;

import com.flowershop.dto.ApiResponse;
import com.flowershop.entity.AuditLog;
import com.flowershop.service.AuditService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/audit")
@PreAuthorize("hasRole('ADMIN')")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    public ApiResponse<Page<AuditLog>> getAllAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<AuditLog> auditLogs = auditService.getAllAuditLogs(pageable);
            return ApiResponse.success("Журнал аудита получен", auditLogs);
        } catch (Exception e) {
            return ApiResponse.error("Ошибка при получении журнала аудита: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<AuditLog> getAuditLogById(@PathVariable Long id) {
        try {
            Optional<AuditLog> auditLog = auditService.getAuditLogById(id);
            if (auditLog.isPresent()) {
                return ApiResponse.success("Запись аудита получена", auditLog.get());
            } else {
                return ApiResponse.error("Запись аудита не найдена");
            }
        } catch (Exception e) {
            return ApiResponse.error("Ошибка при получении записи аудита: " + e.getMessage());
        }
    }

    @GetMapping("/table/{tableName}")
    public ApiResponse<List<AuditLog>> getAuditByTable(@PathVariable String tableName) {
        try {
            List<AuditLog> auditLogs = auditService.getAuditLogByTable(tableName);
            return ApiResponse.success("Аудит таблицы " + tableName + " получен", auditLogs);
        } catch (Exception e) {
            return ApiResponse.error("Ошибка при получении аудита таблицы: " + e.getMessage());
        }
    }

    @GetMapping("/table/{tableName}/record/{recordId}")
    public ApiResponse<List<AuditLog>> getAuditByTableAndRecord(
            @PathVariable String tableName,
            @PathVariable Long recordId) {
        try {
            List<AuditLog> auditLogs = auditService.getAuditLogByTableAndRecord(tableName, recordId);
            return ApiResponse.success("Аудит записи получен", auditLogs);
        } catch (Exception e) {
            return ApiResponse.error("Ошибка при получении аудита записи: " + e.getMessage());
        }
    }

    @GetMapping("/user/{username}")
    public ApiResponse<List<AuditLog>> getAuditByUser(@PathVariable String username) {
        try {
            List<AuditLog> auditLogs = auditService.getAuditLogByUser(username);
            return ApiResponse.success("Аудит пользователя " + username + " получен", auditLogs);
        } catch (Exception e) {
            return ApiResponse.error("Ошибка при получении аудита пользователя: " + e.getMessage());
        }
    }

    @GetMapping("/period")
    public ApiResponse<List<AuditLog>> getAuditByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        try {
            List<AuditLog> auditLogs = auditService.getAuditLogByPeriod(start, end);
            return ApiResponse.success("Аудит за период получен", auditLogs);
        } catch (Exception e) {
            return ApiResponse.error("Ошибка при получении аудита за период: " + e.getMessage());
        }
    }
}