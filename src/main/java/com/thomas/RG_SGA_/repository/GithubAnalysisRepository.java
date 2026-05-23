package com.thomas.RG_SGA_.repository;

import com.thomas.RG_SGA_.entity.GithubAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GithubAnalysisRepository extends JpaRepository<GithubAnalysis, Long> {
    List<GithubAnalysis> findByUserId(Long userId);
    Optional<GithubAnalysis> findByUserIdAndUsername(Long userId, String username);
}
