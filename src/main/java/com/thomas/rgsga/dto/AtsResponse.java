package com.thomas.rgsga.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtsResponse {

    @Builder.Default
    private Integer score = 0;

    @Builder.Default
    private Integer keywordMatchScore = 0;

    @Builder.Default
    private Integer formattingScore = 0;

    @Builder.Default
    private Integer sectionScore = 0;

    @Builder.Default
    private Integer bulletScore = 0;

    @Builder.Default
    private List<String> matchingSkills = new ArrayList<>();

    @Builder.Default
    private List<String> missingSkills = new ArrayList<>();

    @Builder.Default
    private List<RecommendationItem> recommendations = new ArrayList<>();
}
