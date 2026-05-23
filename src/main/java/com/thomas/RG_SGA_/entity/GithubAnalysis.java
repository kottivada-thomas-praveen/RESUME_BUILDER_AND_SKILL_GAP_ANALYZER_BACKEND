package com.thomas.RG_SGA_.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "github_analyses")
public class GithubAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String username;

    @Column(name = "dev_score")
    @Builder.Default
    private Integer devScore = 0;

    @Column(columnDefinition = "TEXT")
    private String technologies; // Comma separated

    @Column(name = "repo_count")
    @Builder.Default
    private Integer repoCount = 0;

    @Column(name = "public_repos_json", columnDefinition = "TEXT")
    private String publicReposJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
