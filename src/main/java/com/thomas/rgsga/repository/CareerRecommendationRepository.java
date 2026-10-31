package com.thomas.rgsga.repository;

import com.thomas.rgsga.entity.CareerRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CareerRecommendationRepository extends JpaRepository<CareerRecommendation, Long> {

    List<CareerRecommendation> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<CareerRecommendation> findByIdAndUserId(Long id, Long userId);
}
