package com.thomas.RG_SGA_.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewRequest {
    @NotBlank(message = "Job title is required")
    private String jobTitle;
    
    private String jobDescription;
    private String userSkills;
}
