package com.thomas.rgsga.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtractedKeywordsResponse {
    private List<String> technicalSkills;
    private List<String> softSkills;
    private List<String> highFrequencyNounPhrases;
    private Map<String, Integer> keywordFrequencies;
    private String inferredRoleCategory;
    private int totalKeywordsFound;
}
