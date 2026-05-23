package com.thomas.RG_SGA_.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "skill_gap_analyses")
public class SkillGapAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "user_skills", columnDefinition = "TEXT")
    private String userSkills;

    @Column(name = "job_skills", columnDefinition = "TEXT")
    private String jobSkills;

    @Column(name = "missing_skills", columnDefinition = "TEXT")
    private String missingSkills;

    @Column(name = "learning_roadmap_json", columnDefinition = "TEXT")
    private String learningRoadmapJson; // JSON representation of sequential steps to study

    @Column(name = "recommended_resources_json", columnDefinition = "TEXT")
    private String recommendedResourcesJson; // JSON lists of YouTube videos, courses, and certifications

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
