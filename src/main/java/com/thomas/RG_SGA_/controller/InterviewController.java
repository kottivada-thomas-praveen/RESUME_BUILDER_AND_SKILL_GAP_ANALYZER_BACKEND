package com.thomas.RG_SGA_.controller;

import com.thomas.RG_SGA_.dto.ApiResponseWrapper;
import com.thomas.RG_SGA_.dto.InterviewRequest;
import com.thomas.RG_SGA_.dto.InterviewResponse;
import com.thomas.RG_SGA_.entity.User;
import com.thomas.RG_SGA_.service.InterviewPrepService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interview")
public class InterviewController {

    private final InterviewPrepService interviewPrepService;

    public InterviewController(InterviewPrepService interviewPrepService) {
        this.interviewPrepService = interviewPrepService;
    }

    @PostMapping("/prep")
    public ResponseEntity<ApiResponseWrapper<InterviewResponse>> getInterviewPrep(@AuthenticationPrincipal User user, 
                                                                                  @Valid @RequestBody InterviewRequest request) {
        InterviewResponse response = interviewPrepService.generateInterviewPrep(user, request);
        return ResponseEntity.ok(ApiResponseWrapper.success("Interview mock preparation checklist generated", response));
    }
}
