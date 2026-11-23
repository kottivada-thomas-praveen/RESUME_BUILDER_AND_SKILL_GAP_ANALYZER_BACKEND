package com.thomas.rgsga.controller;

import com.thomas.rgsga.dto.ApiResponse;
import com.thomas.rgsga.dto.AtsRequest;
import com.thomas.rgsga.dto.AtsResponse;
import com.thomas.rgsga.entity.User;
import com.thomas.rgsga.service.AtsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ats")
@Tag(name = "ATS Analyzer", description = "Evaluate ATS compatibility and keyword matching against job descriptions")
public class AtsController {

    private final AtsService atsService;
    private final com.thomas.rgsga.service.KeywordExtractionService keywordExtractionService;
    private final com.thomas.rgsga.service.AnalyticsService analyticsService;

    public AtsController(AtsService atsService,
                         com.thomas.rgsga.service.KeywordExtractionService keywordExtractionService,
                         com.thomas.rgsga.service.AnalyticsService analyticsService) {
        this.atsService = atsService;
        this.keywordExtractionService = keywordExtractionService;
        this.analyticsService = analyticsService;
    }

    @PostMapping("/analyze")
    @Operation(summary = "Perform ATS compatibility score scan")
    public ResponseEntity<ApiResponse<AtsResponse>> analyze(@AuthenticationPrincipal User user,
                                                            @Valid @RequestBody AtsRequest request) {
        AtsResponse response = atsService.analyzeResume(user, request);
        analyticsService.logUserActivity(user, "ATS_ANALYSIS", "ATS", null,
                "ATS score: " + response.getScore() + "% (Keyword Match: " + response.getKeywordMatchScore() + "%)", null);
        return ResponseEntity.ok(ApiResponse.success("ATS analysis completed successfully", response));
    }

    @PostMapping("/extract-keywords")
    @Operation(summary = "Extract technical skills and noun phrases from target job description")
    public ResponseEntity<ApiResponse<com.thomas.rgsga.dto.ExtractedKeywordsResponse>> extractKeywords(@RequestBody java.util.Map<String, String> request) {
        String jobDescription = request.getOrDefault("jobDescription", "");
        com.thomas.rgsga.dto.ExtractedKeywordsResponse response = keywordExtractionService.extractKeywords(jobDescription);
        return ResponseEntity.ok(ApiResponse.success("Keywords and noun phrases extracted successfully", response));
    }
}
