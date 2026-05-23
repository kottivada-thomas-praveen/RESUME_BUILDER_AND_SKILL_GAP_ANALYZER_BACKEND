package com.thomas.RG_SGA_.repository;

import com.thomas.RG_SGA_.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findByUserId(Long userId);
    List<Education> findByResumeId(Long resumeId);
}
