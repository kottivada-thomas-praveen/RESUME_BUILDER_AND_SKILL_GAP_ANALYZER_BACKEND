package com.thomas.RG_SGA_.controller;

import com.thomas.RG_SGA_.dto.ApiResponseWrapper;
import com.thomas.RG_SGA_.dto.CareerRequest;
import com.thomas.RG_SGA_.dto.CareerResponse;
import com.thomas.RG_SGA_.entity.User;
import com.thomas.RG_SGA_.service.CareerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/career")
public class CareerController {

    private final CareerService careerService;

    public CareerController(CareerService careerService) {
        this.careerService = careerService;
    }

    @PostMapping("/recommend")
    public ResponseEntity<ApiResponseWrapper<CareerResponse>> recommendCareers(@AuthenticationPrincipal User user, 
                                                                               @Valid @RequestBody CareerRequest request) {
        CareerResponse response = careerService.recommendCareers(user, request);
        return ResponseEntity.ok(ApiResponseWrapper.success("Career trajectory forecasts calculated successfully", response));
    }
}
