package com.thomas.rgsga.repository;

import com.thomas.rgsga.entity.AuthAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuthAuditLogRepository extends JpaRepository<AuthAuditLog, Long> {

    Long countByStatus(String status);

    Long countByStatusAndCreatedAtAfter(String status, LocalDateTime since);

    Long countByEventType(String eventType);

    @Query("SELECT a.email, COUNT(a) FROM AuthAuditLog a WHERE a.status = 'FAILURE' AND a.createdAt >= :since GROUP BY a.email HAVING COUNT(a) >= 5 ORDER BY COUNT(a) DESC")
    List<Object[]> findSuspiciousLoginAttempts(@Param("since") LocalDateTime since);

    @Query("SELECT a FROM AuthAuditLog a WHERE " +
           "(:query IS NULL OR LOWER(a.email) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(a.ipAddress) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:eventType IS NULL OR a.eventType = :eventType)")
    Page<AuthAuditLog> searchAuthLogs(@Param("query") String query,
                                      @Param("status") String status,
                                      @Param("eventType") String eventType,
                                      Pageable pageable);

    List<AuthAuditLog> findTop10ByOrderByCreatedAtDesc();

    List<AuthAuditLog> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);
}
