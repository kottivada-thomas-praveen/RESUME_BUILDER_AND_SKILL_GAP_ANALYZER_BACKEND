package com.thomas.RG_SGA_.repository;

import com.thomas.RG_SGA_.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUserId(Long userId);
    List<AuditLog> findFirst50ByOrderByCreatedAtDesc();
}
