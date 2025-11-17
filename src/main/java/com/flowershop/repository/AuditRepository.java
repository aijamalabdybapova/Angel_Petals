// src/main/java/com/flowershop/repository/AuditRepository.java
package com.flowershop.repository;

import com.flowershop.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByTableNameAndRecordId(String tableName, Long recordId);

    List<AuditLog> findByTableName(String tableName);

    List<AuditLog> findByChangedBy(String changedBy);

    List<AuditLog> findByChangedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT a FROM AuditLog a WHERE a.action = :action AND a.changedAt BETWEEN :start AND :end")
    List<AuditLog> findByActionAndPeriod(@Param("action") String action,
                                         @Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end);
}