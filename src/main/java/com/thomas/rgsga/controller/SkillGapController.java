package com.thomas.rgsga.controller;

import com.thomas.rgsga.dto.ApiResponse;
import com.thomas.rgsga.dto.SkillGapRequest;
import com.thomas.rgsga.dto.SkillGapResponse;
import com.thomas.rgsga.entity.SkillGapAnalysis;
import com.thomas.rgsga.entity.User;
import com.thomas.rgsga.service.SkillGapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skill-gap")
@Tag(name = "Skill Gap Analyzer", description = "Assess skill match against job requirements and generate structured learning roadmaps")
public class SkillGapController {

    private final SkillGapService skillGapService;

    public SkillGapController(SkillGapService skillGapService) {
        this.skillGapService = skillGapService;
    }

    @PostMapping("/analyze")
    @Operation(summary = "Analyze skill gaps and generate personalized learning roadmap")
    public ResponseEntity<ApiResponse<SkillGapResponse>> analyze(@AuthenticationPrincipal User user,
                                                                 @Valid @RequestBody SkillGapRequest request) {
        SkillGapResponse response = skillGapService.analyzeSkillGap(user, request);
        return ResponseEntity.ok(ApiResponse.success("Skill gap study pathway generated successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get historical skill gap analyses for user")
    public ResponseEntity<ApiResponse<List<SkillGapAnalysis>>> getUserAnalyses(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("Skill gap analyses fetched", skillGapService.getUserAnalyses(user)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get specific skill gap analysis by ID")
    public ResponseEntity<ApiResponse<SkillGapAnalysis>> getById(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Skill gap analysis fetched", skillGapService.getAnalysisById(user, id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete skill gap analysis record")
    public ResponseEntity<ApiResponse<Void>> delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        skillGapService.deleteAnalysis(user, id);
        return ResponseEntity.ok(ApiResponse.success("Skill gap analysis deleted successfully", null));
    }
}
