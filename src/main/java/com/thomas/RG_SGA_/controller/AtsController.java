package com.thomas.RG_SGA_.controller;

import com.thomas.RG_SGA_.dto.ApiResponseWrapper;
import com.thomas.RG_SGA_.dto.AtsRequest;
import com.thomas.RG_SGA_.dto.AtsResponse;
import com.thomas.RG_SGA_.entity.User;
import com.thomas.RG_SGA_.service.AtsAnalyzerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ats")
public class AtsController {

    private final AtsAnalyzerService atsAnalyzerService;

    public AtsController(AtsAnalyzerService atsAnalyzerService) {
        this.atsAnalyzerService = atsAnalyzerService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<ApiResponseWrapper<AtsResponse>> analyzeResume(@AuthenticationPrincipal User user, 
                                                                         @Valid @RequestBody AtsRequest request) {
        AtsResponse response = atsAnalyzerService.analyzeResume(user, request);
        return ResponseEntity.ok(ApiResponseWrapper.success("ATS Compatibility analysis completed successfully", response));
    }
}
