package com.thomas.rgsga.controller;

import com.thomas.rgsga.dto.ApiResponse;
import com.thomas.rgsga.dto.CareerRequest;
import com.thomas.rgsga.dto.CareerResponse;
import com.thomas.rgsga.entity.User;
import com.thomas.rgsga.service.CareerService;
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
@RequestMapping("/api/career")
@Tag(name = "Career Recommendations", description = "Target role recommendations, market demand forecasts, and salary benchmarks")
public class CareerController {

    private final CareerService careerService;

    public CareerController(CareerService careerService) {
        this.careerService = careerService;
    }

    @PostMapping("/recommend")
    @Operation(summary = "Get career trajectory and role recommendations")
    public ResponseEntity<ApiResponse<CareerResponse>> recommend(@AuthenticationPrincipal User user,
                                                                 @Valid @RequestBody CareerRequest request) {
        CareerResponse response = careerService.recommendCareers(user, request);
        return ResponseEntity.ok(ApiResponse.success("Career trajectory forecasts calculated successfully", response));
    }
}
