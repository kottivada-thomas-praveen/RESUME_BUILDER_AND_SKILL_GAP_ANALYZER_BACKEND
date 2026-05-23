package com.thomas.RG_SGA_.repository;

import com.thomas.RG_SGA_.entity.AtsAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AtsAnalysisRepository extends JpaRepository<AtsAnalysis, Long> {
    List<AtsAnalysis> findByUserId(Long userId);
    List<AtsAnalysis> findByResumeId(Long resumeId);
}
