package com.thomas.rgsga.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GithubLoginRequest {

    @NotBlank(message = "GitHub authorization code is required")
    private String code;
}
