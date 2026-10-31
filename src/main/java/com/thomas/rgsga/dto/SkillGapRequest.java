package com.thomas.rgsga.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillGapRequest {

    private String userSkills;

    @NotBlank(message = "Job description is required")
    private String jobDescription;
}
