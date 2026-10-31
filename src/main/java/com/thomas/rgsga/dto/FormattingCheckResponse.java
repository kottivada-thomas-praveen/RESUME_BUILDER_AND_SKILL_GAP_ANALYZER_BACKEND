package com.thomas.rgsga.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormattingCheckResponse {
    private int estimatedPages;
    private int wordCount;
    private int characterCount;
    private boolean isLengthOptimal;
    private List<String> warnings;
    private List<String> suggestions;
    private int formattingScore;
}
