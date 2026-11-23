package com.thomas.rgsga.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuspiciousLoginDTO {
    private String email;
    private Long failedAttempts;
    private LocalDateTime lastAttempt;
    private String statusAlert; // e.g. "HIGH RISK: 5+ failed attempts in 24h"
}
