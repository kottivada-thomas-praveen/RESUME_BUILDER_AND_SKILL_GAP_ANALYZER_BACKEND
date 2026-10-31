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
public class GrammarCheckRequest {
    @NotBlank(message = "Text for grammar check cannot be blank")
    private String text;
}
