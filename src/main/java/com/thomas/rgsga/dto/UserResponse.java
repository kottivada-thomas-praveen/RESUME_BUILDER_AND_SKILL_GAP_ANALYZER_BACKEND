package com.thomas.rgsga.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String fullName;
    private String name;
    private String email;
    private String role;
    private Boolean accountEnabled;
    private Boolean emailVerified;
    private LocalDateTime lastLoginAt;
    private Integer loginCount;
    private Integer failedLoginCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
