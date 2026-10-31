package com.thomas.rgsga.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resumes", indexes = {
        @Index(name = "idx_resumes_user_id", columnList = "user_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(length = 150)
    private String title;

    @Column(length = 150)
    private String email;

    @Column(length = 50)
    private String phone;

    @Column(length = 150)
    private String location;

    @Column(length = 255)
    private String website;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "skills_csv", columnDefinition = "TEXT")
    private String skillsCsv;

    @Column(name = "template_name", length = 50)
    @Builder.Default
    private String templateName = "modern";

    @Column(name = "accent_color", length = 50)
    @Builder.Default
    private String accentColor = "purple";

    @Column(name = "photo_url", columnDefinition = "TEXT")
    private String photoUrl;

    @Column(name = "ats_score")
    @Builder.Default
    private Integer atsScore = 0;

    @Column(name = "version")
    @Builder.Default
    private Integer version = 1;

    @Column(name = "share_token", unique = true, length = 100)
    private String shareToken;

    @Column(name = "is_public")
    @Builder.Default
    private Boolean isPublic = false;

    @Column(name = "views_count")
    @Builder.Default
    private Integer viewsCount = 0;

    @Column(name = "download_count")
    @Builder.Default
    private Integer downloadCount = 0;

    @Column(name = "target_job_description", columnDefinition = "TEXT")
    private String targetJobDescription;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Experience> experiences = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Education> educations = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Project> projects = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.updatedAt == null) this.updatedAt = LocalDateTime.now();
        if (this.version == null) this.version = 1;
        if (this.isActive == null) this.isActive = true;
        if (this.atsScore == null) this.atsScore = 0;
        if (this.templateName == null) this.templateName = "modern";
        if (this.accentColor == null) this.accentColor = "purple";
        if (this.isPublic == null) this.isPublic = false;
        if (this.viewsCount == null) this.viewsCount = 0;
        if (this.downloadCount == null) this.downloadCount = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
