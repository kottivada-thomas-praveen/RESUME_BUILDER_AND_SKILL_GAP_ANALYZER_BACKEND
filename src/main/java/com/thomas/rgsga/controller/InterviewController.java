package com.thomas.rgsga.controller;

import com.thomas.rgsga.dto.ApiResponse;
import com.thomas.rgsga.dto.InterviewRequest;
import com.thomas.rgsga.dto.InterviewResponse;
import com.thomas.rgsga.entity.User;
import com.thomas.rgsga.service.InterviewService;
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
@RequestMapping("/api/interview")
@Tag(name = "Interview Prep", description = "Targeted technical questions and STAR behavioral preparation guides")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping("/prep")
    @Operation(summary = "Generate interview mock questions and guidance")
    public ResponseEntity<ApiResponse<InterviewResponse>> prep(@AuthenticationPrincipal User user,
                                                               @Valid @RequestBody InterviewRequest request) {
        InterviewResponse response = interviewService.generateInterviewPrep(user, request);
        return ResponseEntity.ok(ApiResponse.success("Interview mock preparation checklist generated", response));
    }
}
