package com.thomas.rgsga.repository;

import com.thomas.rgsga.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {

    List<Education> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Education> findByIdAndUserId(Long id, Long userId);

    List<Education> findByResumeId(Long resumeId);
}
