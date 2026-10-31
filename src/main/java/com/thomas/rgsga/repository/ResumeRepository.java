package com.thomas.rgsga.repository;

import com.thomas.rgsga.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    List<Resume> findByUserIdAndIsActiveTrueOrderByUpdatedAtDesc(Long userId);

    Optional<Resume> findByIdAndUserId(Long id, Long userId);

    default Optional<Resume> findByIdAndUserIdWithDetails(Long id, Long userId) {
        return findByIdAndUserId(id, userId);
    }

    default Optional<Resume> findByShareTokenWithDetails(String shareToken) {
        return findByShareTokenAndIsActiveTrue(shareToken);
    }

    Optional<Resume> findByShareTokenAndIsActiveTrue(String shareToken);

    @Query("SELECT r.templateName, COUNT(r) FROM Resume r WHERE r.isActive = true GROUP BY r.templateName")
    List<Object[]> countByTemplateNameGroup();

    long countByUserId(Long userId);

    void deleteByUserId(Long userId);
}
