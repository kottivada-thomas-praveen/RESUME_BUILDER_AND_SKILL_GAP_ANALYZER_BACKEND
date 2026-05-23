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
public class SkillGapResponse {
    
    @Builder.Default
    private List<String> userSkills = new ArrayList<>();
    
    @Builder.Default
    private List<String> jobSkills = new ArrayList<>();
    
    @Builder.Default
    private List<String> missingSkills = new ArrayList<>();
    
    @Builder.Default
    private List<RoadmapStep> learningRoadmap = new ArrayList<>();
    
    private RecommendedResources recommendedResources;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoadmapStep {
        private Integer stepNumber;
        private String title;
        private String description;
        private String timeline;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendedResources {
        @Builder.Default
        private List<ResourceItem> courses = new ArrayList<>();
        
        @Builder.Default
        private List<ResourceItem> youtubeVideos = new ArrayList<>();
        
        @Builder.Default
        private List<ResourceItem> certifications = new ArrayList<>();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResourceItem {
        private String title;
        private String provider;
        private String url;
        private String durationOrCost;
    }
}
