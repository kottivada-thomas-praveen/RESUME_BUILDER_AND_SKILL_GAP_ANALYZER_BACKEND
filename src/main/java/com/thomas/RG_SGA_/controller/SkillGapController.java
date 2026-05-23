package com.thomas.RG_SGA_.controller;

import com.thomas.RG_SGA_.dto.ApiResponseWrapper;
import com.thomas.RG_SGA_.dto.SkillGapRequest;
import com.thomas.RG_SGA_.dto.SkillGapResponse;
import com.thomas.RG_SGA_.entity.User;
import com.thomas.RG_SGA_.service.SkillGapService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/skill-gap")
public class SkillGapController {

    private final SkillGapService skillGapService;

    public SkillGapController(SkillGapService skillGapService) {
        this.skillGapService = skillGapService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<ApiResponseWrapper<SkillGapResponse>> analyzeSkillGap(@AuthenticationPrincipal User user, 
                                                                               @Valid @RequestBody SkillGapRequest request) {
        SkillGapResponse response = skillGapService.analyzeSkillGap(user, request);
        return ResponseEntity.ok(ApiResponseWrapper.success("Skill gap study pathway generated successfully", response));
    }
}
