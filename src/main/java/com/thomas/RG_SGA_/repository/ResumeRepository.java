package com.thomas.RG_SGA_.repository;

import com.thomas.RG_SGA_.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByUserId(Long userId);
    List<Resume> findByUserIdAndIsActiveTrue(Long userId);
}
