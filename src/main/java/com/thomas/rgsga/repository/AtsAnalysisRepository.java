package com.thomas.rgsga.repository;

import com.thomas.rgsga.entity.AtsAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AtsAnalysisRepository extends JpaRepository<AtsAnalysis, Long> {

    List<AtsAnalysis> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<AtsAnalysis> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT AVG(a.score) FROM AtsAnalysis a")
    Double findAverageScore();

    long countByUserId(Long userId);

    void deleteByUserId(Long userId);
}
