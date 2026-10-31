package com.thomas.rgsga.repository;

import com.thomas.rgsga.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    List<Experience> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Experience> findByIdAndUserId(Long id, Long userId);

    List<Experience> findByResumeId(Long resumeId);
}
