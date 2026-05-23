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
public class AtsRequest {
    private Long resumeId;
    private String resumeText;
    
    @NotBlank(message = "Job description is required")
    private String jobDescription;
}
