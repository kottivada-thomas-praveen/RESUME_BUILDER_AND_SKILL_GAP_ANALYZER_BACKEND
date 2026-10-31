package com.thomas.rgsga.repository;

import com.thomas.rgsga.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    List<Skill> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Skill> findByIdAndUserId(Long id, Long userId);

    Optional<Skill> findByUserIdAndSkillNameIgnoreCase(Long userId, String skillName);

    boolean existsByUserIdAndSkillNameIgnoreCase(Long userId, String skillName);

    void deleteByUserId(Long userId);
}
