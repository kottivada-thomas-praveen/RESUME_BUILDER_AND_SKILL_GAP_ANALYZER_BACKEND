package com.thomas.rgsga.repository;

import com.thomas.rgsga.entity.SkillGapAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillGapAnalysisRepository extends JpaRepository<SkillGapAnalysis, Long> {

    List<SkillGapAnalysis> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<SkillGapAnalysis> findByIdAndUserId(Long id, Long userId);

    void deleteByUserId(Long userId);
}
