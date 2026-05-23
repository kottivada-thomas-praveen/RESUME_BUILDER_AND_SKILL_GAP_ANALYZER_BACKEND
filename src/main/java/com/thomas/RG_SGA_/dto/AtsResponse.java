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
public class AtsResponse {
    
    private Integer score;
    private Integer keywordMatchScore;
    private Integer formattingScore;
    private Integer sectionScore;
    private Integer bulletScore;
    
    @Builder.Default
    private List<String> matchingSkills = new ArrayList<>();
    
    @Builder.Default
    private List<String> missingSkills = new ArrayList<>();
    
    @Builder.Default
    private List<RecommendationItem> recommendations = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendationItem {
        private Integer id;
        private String type; // "high" | "medium" | "low"
        private String text;
        private String description;
    }
}
