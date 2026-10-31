package com.thomas.rgsga.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulletOptimizeRequest {

    @NotBlank(message = "Bullet point text is required")
    private String bulletPoint;

    private String jobDescription;
}
