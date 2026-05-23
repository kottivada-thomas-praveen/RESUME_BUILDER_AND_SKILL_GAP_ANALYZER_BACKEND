package com.thomas.RG_SGA_.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "interview_preps")
public class InterviewPrep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "questions_json", columnDefinition = "TEXT")
    private String questionsJson; // JSON array of questions, responses, tips

    @Column(name = "technical_prep", columnDefinition = "TEXT")
    private String technicalPrep;

    @Column(name = "behavioral_prep", columnDefinition = "TEXT")
    private String behavioralPrep;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
