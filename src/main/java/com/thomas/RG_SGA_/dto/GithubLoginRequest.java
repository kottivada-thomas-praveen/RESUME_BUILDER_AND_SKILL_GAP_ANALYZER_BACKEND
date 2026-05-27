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
public class GithubLoginRequest {
    @NotBlank(message = "GitHub authorization code is required")
    private String code;
}
