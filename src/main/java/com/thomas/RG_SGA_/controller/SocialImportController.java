package com.thomas.RG_SGA_.controller;

import com.thomas.RG_SGA_.dto.ApiResponseWrapper;
import com.thomas.RG_SGA_.entity.GithubAnalysis;
import com.thomas.RG_SGA_.entity.User;
import com.thomas.RG_SGA_.service.GithubAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/import")
public class SocialImportController {

    private final GithubAnalysisService githubAnalysisService;

    public SocialImportController(GithubAnalysisService githubAnalysisService) {
        this.githubAnalysisService = githubAnalysisService;
    }

    @PostMapping("/github")
    public ResponseEntity<ApiResponseWrapper<GithubAnalysis>> importGithub(@AuthenticationPrincipal User user, 
                                                                           @RequestParam String username) {
        GithubAnalysis analysis = githubAnalysisService.analyzeGithubProfile(user, username);
        return ResponseEntity.ok(ApiResponseWrapper.success("GitHub developer profile scanned successfully", analysis));
    }
}
