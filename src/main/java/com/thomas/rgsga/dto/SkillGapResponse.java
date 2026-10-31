package com.thomas.rgsga.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillGapResponse {

    @Builder.Default
    private Integer matchPercentage = 0;

    @Builder.Default
    private List<String> userSkills = new ArrayList<>();

    @Builder.Default
    private List<String> jobSkills = new ArrayList<>();

    @Builder.Default
    private List<String> missingSkills = new ArrayList<>();

    @Builder.Default
    private List<RoadmapStep> learningRoadmap = new ArrayList<>();

    @Builder.Default
    private RecommendedResources recommendedResources = new RecommendedResources();
}
