package com.thomas.RG_SGA_.repository;

import com.thomas.RG_SGA_.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    List<Experience> findByUserId(Long userId);
    List<Experience> findByResumeId(Long resumeId);
}
