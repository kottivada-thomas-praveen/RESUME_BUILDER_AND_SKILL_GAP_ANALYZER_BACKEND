package com.thomas.RG_SGA_.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewResponse {
    
    @Builder.Default
    private List<InterviewQuestion> questions = new ArrayList<>();
    
    private String technicalPrep;
    private String behavioralPrep;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InterviewQuestion {
        private Integer id;
        private String type; // "technical" | "behavioral"
        private String question;
        private String sampleAnswer;
        private String answerGuidelines;
    }
}
