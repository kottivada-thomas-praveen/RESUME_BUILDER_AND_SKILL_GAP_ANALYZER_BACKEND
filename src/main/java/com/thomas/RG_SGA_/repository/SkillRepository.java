package com.thomas.RG_SGA_.repository;

import com.thomas.RG_SGA_.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findByUserId(Long userId);
    Optional<Skill> findByUserIdAndSkillName(Long userId, String skillName);
}
