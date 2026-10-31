package com.thomas.rgsga.repository;

import com.thomas.rgsga.entity.InterviewPrep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewPrepRepository extends JpaRepository<InterviewPrep, Long> {

    List<InterviewPrep> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<InterviewPrep> findByIdAndUserId(Long id, Long userId);
}
