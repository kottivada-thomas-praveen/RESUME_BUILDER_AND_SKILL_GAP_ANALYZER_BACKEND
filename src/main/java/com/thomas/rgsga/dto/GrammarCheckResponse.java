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
public class GrammarCheckResponse {
    private boolean hasIssues;
    private int issueCount;
    private List<GrammarIssue> issues;
    private String cleanedText;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrammarIssue {
        private String message;
        private String shortMessage;
        private int offset;
        private int length;
        private String context;
        private List<String> replacements;
        private String ruleCategory;
    }
}
