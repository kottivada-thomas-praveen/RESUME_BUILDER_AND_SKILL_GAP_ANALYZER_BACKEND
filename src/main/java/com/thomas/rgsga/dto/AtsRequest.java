package com.thomas.rgsga.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtsRequest {

    private Long resumeId;
    private String resumeText;

    @NotBlank(message = "Job description is required")
    private String jobDescription;
}
