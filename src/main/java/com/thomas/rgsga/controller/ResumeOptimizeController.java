package com.thomas.rgsga.controller;

import com.thomas.rgsga.dto.ApiResponse;
import com.thomas.rgsga.dto.BulletOptimizeRequest;
import com.thomas.rgsga.dto.BulletOptimizeResponse;
import com.thomas.rgsga.service.BulletOptimizeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/optimize")
@Tag(name = "Resume Optimization", description = "Enhance resume achievements and bullet points using the STAR method")
public class ResumeOptimizeController {

    private final BulletOptimizeService bulletOptimizeService;

    public ResumeOptimizeController(BulletOptimizeService bulletOptimizeService) {
        this.bulletOptimizeService = bulletOptimizeService;
    }

    @PostMapping("/bullet")
    @Operation(summary = "Rewrite resume bullet points for maximum recruiter impact")
    public ResponseEntity<ApiResponse<BulletOptimizeResponse>> optimizeBullet(@Valid @RequestBody BulletOptimizeRequest request) {
        BulletOptimizeResponse response = bulletOptimizeService.optimizeBullet(request);
        return ResponseEntity.ok(ApiResponse.success("Bullet point optimized successfully", response));
    }
}
