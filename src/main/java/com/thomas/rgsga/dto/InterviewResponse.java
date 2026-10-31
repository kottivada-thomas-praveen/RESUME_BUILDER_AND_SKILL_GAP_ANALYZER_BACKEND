package com.thomas.rgsga.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewResponse {

    @Builder.Default
    private List<TechnicalQuestion> technicalQuestions = new ArrayList<>();

    @Builder.Default
    private List<BehavioralQuestion> behavioralQuestions = new ArrayList<>();

    @Builder.Default
    private List<String> systemDesignOrCodingTopics = new ArrayList<>();

    @Builder.Default
    private List<String> preparationTips = new ArrayList<>();
}
