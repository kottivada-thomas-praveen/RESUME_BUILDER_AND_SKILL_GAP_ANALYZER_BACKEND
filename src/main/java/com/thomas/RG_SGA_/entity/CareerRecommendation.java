package com.thomas.RG_SGA_.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "career_recommendations")
public class CareerRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "recommended_roles", columnDefinition = "TEXT")
    private String recommendedRoles;

    @Column(name = "salary_insights", columnDefinition = "TEXT")
    private String salaryInsights;

    @Column(name = "market_demand", columnDefinition = "TEXT")
    private String marketDemand;

    @Column(name = "roadmap_json", columnDefinition = "TEXT")
    private String roadmapJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
