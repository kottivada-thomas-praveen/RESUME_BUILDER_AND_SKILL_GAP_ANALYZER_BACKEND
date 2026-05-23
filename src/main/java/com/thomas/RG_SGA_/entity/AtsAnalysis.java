package com.thomas.RG_SGA_.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ats_analyses")
public class AtsAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "job_description", columnDefinition = "TEXT", nullable = false)
    private String jobDescription;

    @Builder.Default
    private Integer score = 0;

    @Column(name = "keyword_match_score")
    @Builder.Default
    private Integer keywordMatchScore = 0;

    @Column(name = "formatting_score")
    @Builder.Default
    private Integer formattingScore = 0;

    @Column(name = "section_score")
    @Builder.Default
    private Integer sectionScore = 0;

    @Column(name = "bullet_score")
    @Builder.Default
    private Integer bulletScore = 0;

    @Column(name = "matching_skills", columnDefinition = "TEXT")
    private String matchingSkills; // Comma separated

    @Column(name = "missing_skills", columnDefinition = "TEXT")
    private String missingSkills; // Comma separated

    @Column(name = "recommendations_json", columnDefinition = "TEXT")
    private String recommendationsJson; // Stores array of tips/enhancements as JSON string

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
