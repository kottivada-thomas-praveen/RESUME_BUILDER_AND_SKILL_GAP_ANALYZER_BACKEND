package com.thomas.rgsga.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "visitor_logs", indexes = {
        @Index(name = "idx_visitor_logs_created_at", columnList = "created_at"),
        @Index(name = "idx_visitor_logs_visitor_id", columnList = "visitor_id"),
        @Index(name = "idx_visitor_logs_page_url", columnList = "page_url"),
        @Index(name = "idx_visitor_logs_user_id", columnList = "user_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "visitor_id", nullable = false, length = 64)
    private String visitorId;

    @Column(name = "session_id", length = 64)
    private String sessionId;

    @Column(name = "page_url", nullable = false, length = 255)
    private String pageUrl;

    @Column(name = "page_title", length = 150)
    private String pageTitle;

    @Column(name = "referrer_url", length = 255)
    private String referrerUrl;

    @Column(name = "ip_hash", length = 64)
    private String ipHash;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "device_type", length = 50)
    private String deviceType;

    @Column(name = "browser", length = 50)
    private String browser;

    @Column(name = "os", length = 50)
    private String os;

    @Column(length = 100)
    private String country;

    @Column(length = 100)
    private String city;

    @Column(name = "duration_seconds")
    @Builder.Default
    private Integer durationSeconds = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.durationSeconds == null) this.durationSeconds = 0;
    }
}
