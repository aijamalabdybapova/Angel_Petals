package com.flowershop.service;

import com.flowershop.entity.AuditLog;
import com.flowershop.repository.AuditRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AuditService {

    private final AuditRepository auditRepository;

    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Transactional(readOnly = true)
    public Optional<AuditLog> getAuditLogById(Long id) {
        return auditRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogByTableAndRecord(String tableName, Long recordId) {
        return auditRepository.findByTableNameAndRecordId(tableName, recordId);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogByTable(String tableName) {
        return auditRepository.findByTableName(tableName);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogByUser(String username) {
        return auditRepository.findByChangedBy(username);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogByPeriod(LocalDateTime start, LocalDateTime end) {
        return auditRepository.findByChangedAtBetween(start, end);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getAllAuditLogs(Pageable pageable) {
        return auditRepository.findAll(pageable);
    }

    public void logAction(String tableName, Long recordId, String action,
                          String oldData, String newData, String changedBy) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setTableName(tableName);
            auditLog.setRecordId(recordId);
            auditLog.setAction(action);

            // Преобразуем данные в JSON формат
            auditLog.setOldData(convertToJson(oldData));
            auditLog.setNewData(convertToJson(newData));

            auditLog.setChangedBy(changedBy);

            auditRepository.save(auditLog);
            System.out.println("Audit logged: " + tableName + " " + action + " by " + changedBy);
        } catch (Exception e) {
            System.err.println("Failed to log audit: " + e.getMessage());
            // Создаем запись без JSON данных в случае ошибки
            logSimpleAction(tableName, recordId, action, changedBy);
        }
    }

    private String convertToJson(String data) {
        if (data == null) {
            return null;
        }

        try {
            // Если данные уже в JSON формате, возвращаем как есть
            if (data.trim().startsWith("{") || data.trim().startsWith("[")) {
                return data;
            }

            // Иначе создаем простой JSON объект
            return "{\"message\": \"" + escapeJson(data) + "\"}";
        } catch (Exception e) {
            // В случае ошибки возвращаем простой JSON
            return "{\"error\": \"Failed to parse data\", \"original\": \"" + escapeJson(data.substring(0, Math.min(100, data.length()))) + "\"}";
        }
    }
    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private void logSimpleAction(String tableName, Long recordId, String action, String changedBy) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setTableName(tableName);
            auditLog.setRecordId(recordId);
            auditLog.setAction(action);
            auditLog.setOldData(null);
            auditLog.setNewData(null);
            auditLog.setChangedBy(changedBy);

            auditRepository.save(auditLog);
            System.out.println("Simple audit logged: " + tableName + " " + action + " by " + changedBy);
        } catch (Exception e) {
            System.err.println("Failed to log simple audit: " + e.getMessage());
        }
    }

}