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
public class CoverLetterResponse {
    private String coverLetterText;
    private String companyName;
    private String targetRole;
    private List<String> highlightedSkills;
    private String generatedDate;
}
