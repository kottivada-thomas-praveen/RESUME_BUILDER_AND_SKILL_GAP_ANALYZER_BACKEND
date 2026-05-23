package com.thomas.RG_SGA_.repository;

import com.thomas.RG_SGA_.entity.SkillGapAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SkillGapAnalysisRepository extends JpaRepository<SkillGapAnalysis, Long> {
    List<SkillGapAnalysis> findByUserId(Long userId);
}
