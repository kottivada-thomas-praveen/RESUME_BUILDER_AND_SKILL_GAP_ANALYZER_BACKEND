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
public class BulletOptimizeRequest {
    @NotBlank(message = "Bullet point content is required")
    private String bulletPoint;
    
    private String jobDescription;
}
