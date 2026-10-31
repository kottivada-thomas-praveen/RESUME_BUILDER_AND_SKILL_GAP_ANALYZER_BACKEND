package com.thomas.rgsga.repository;

import com.thomas.rgsga.entity.GithubAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GithubAnalysisRepository extends JpaRepository<GithubAnalysis, Long> {

    List<GithubAnalysis> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<GithubAnalysis> findByIdAndUserId(Long id, Long userId);

    Optional<GithubAnalysis> findByUserIdAndUsernameIgnoreCase(Long userId, String username);
}
