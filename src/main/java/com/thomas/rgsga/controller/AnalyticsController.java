package com.thomas.rgsga.controller;

import com.thomas.rgsga.dto.ApiResponse;
import com.thomas.rgsga.dto.VisitorTrackRequest;
import com.thomas.rgsga.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics", description = "Privacy-compliant visitor telemetry and session tracking")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @PostMapping("/track")
    @Operation(summary = "Record privacy-compliant website visitor telemetry and page view")
    public ResponseEntity<ApiResponse<Map<String, Object>>> trackVisitor(@Valid @RequestBody VisitorTrackRequest request,
                                                                         HttpServletRequest httpRequest) {
        analyticsService.trackVisitor(request, httpRequest);
        return ResponseEntity.ok(ApiResponse.success("Visitor telemetry captured", Map.of("recorded", true)));
    }
}
