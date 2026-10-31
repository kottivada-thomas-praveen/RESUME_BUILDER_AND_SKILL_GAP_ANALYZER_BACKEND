package com.thomas.rgsga.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoverLetterRequest {
    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Target role is required")
    private String targetRole;

    private String applicantName;
    private String skills;
    private String experienceYears;
    private String hiringManager;
    private Long resumeId;
}
