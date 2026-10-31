package com.thomas.rgsga.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewRequest {

    @NotBlank(message = "Job title is required")
    private String jobTitle;

    private String jobDescription;
    private String userSkills;
}
