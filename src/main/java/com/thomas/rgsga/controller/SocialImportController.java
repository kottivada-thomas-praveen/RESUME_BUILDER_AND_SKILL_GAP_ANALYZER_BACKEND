package com.thomas.rgsga.controller;

import com.thomas.rgsga.dto.ApiResponse;
import com.thomas.rgsga.entity.GithubAnalysis;
import com.thomas.rgsga.entity.User;
import com.thomas.rgsga.service.GithubImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/import")
@Tag(name = "Social Integrations", description = "Import and analyze developer profiles from GitHub")
public class SocialImportController {

    private final GithubImportService githubImportService;

    public SocialImportController(GithubImportService githubImportService) {
        this.githubImportService = githubImportService;
    }

    @PostMapping("/github")
    @Operation(summary = "Analyze public GitHub developer profile and repositories")
    public ResponseEntity<ApiResponse<GithubAnalysis>> importGithub(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String username,
            @RequestBody(required = false) java.util.Map<String, String> body) {
        String resolvedUsername = username;
        if ((resolvedUsername == null || resolvedUsername.isBlank()) && body != null) {
            resolvedUsername = body.getOrDefault("username", body.get("user"));
        }
        if (resolvedUsername == null || resolvedUsername.isBlank()) {
            throw new com.thomas.rgsga.exception.BadRequestException("Username is required");
        }
        GithubAnalysis analysis = githubImportService.analyzeGithubProfile(user, resolvedUsername.trim());
        return ResponseEntity.ok(ApiResponse.success("GitHub developer profile scanned successfully", analysis));
    }
}
