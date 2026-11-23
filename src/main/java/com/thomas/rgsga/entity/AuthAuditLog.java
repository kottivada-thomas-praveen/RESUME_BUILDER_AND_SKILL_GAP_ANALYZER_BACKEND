package com.thomas.rgsga.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auth_audit_logs", indexes = {
        @Index(name = "idx_auth_logs_created_at", columnList = "created_at"),
        @Index(name = "idx_auth_logs_email", columnList = "email"),
        @Index(name = "idx_auth_logs_event_type", columnList = "event_type"),
        @Index(name = "idx_auth_logs_status", columnList = "status")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(length = 150)
    private String email;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType; // LOGIN_SUCCESS, LOGIN_FAILURE, LOGOUT, PASSWORD_RESET_REQUEST, PASSWORD_RESET_SUCCESS, REFRESH_TOKEN

    @Column(nullable = false, length = 20)
    private String status; // SUCCESS, FAILURE

    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    }
}
