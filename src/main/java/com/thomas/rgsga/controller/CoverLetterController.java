package com.thomas.rgsga.controller;

import com.thomas.rgsga.dto.ApiResponse;
import com.thomas.rgsga.dto.CoverLetterRequest;
import com.thomas.rgsga.dto.CoverLetterResponse;
import com.thomas.rgsga.entity.User;
import com.thomas.rgsga.service.CoverLetterService;
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
@RequestMapping("/api/cover-letter")
@Tag(name = "Cover Letter Generator", description = "Auto-generate tailored cover letters using resume skills and company context")
public class CoverLetterController {

    private final CoverLetterService coverLetterService;

    public CoverLetterController(CoverLetterService coverLetterService) {
        this.coverLetterService = coverLetterService;
    }

    @PostMapping("/generate")
    @Operation(summary = "Generate customized cover letter draft")
    public ResponseEntity<ApiResponse<CoverLetterResponse>> generateCoverLetter(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CoverLetterRequest request) {
        CoverLetterResponse response = coverLetterService.generateCoverLetter(user, request);
        return ResponseEntity.ok(ApiResponse.success("Cover letter generated successfully", response));
    }
}
