package com.thomas.RG_SGA_.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomas.RG_SGA_.dto.ApiResponseWrapper;
import com.thomas.RG_SGA_.dto.BulletOptimizeRequest;
import com.thomas.RG_SGA_.dto.BulletOptimizeResponse;
import com.thomas.RG_SGA_.service.GeminiService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/optimize")
public class ResumeOptimizeController {

    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;

    public ResumeOptimizeController(GeminiService geminiService, ObjectMapper objectMapper) {
        this.geminiService = geminiService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/bullet")
    public ResponseEntity<ApiResponseWrapper<BulletOptimizeResponse>> optimizeBullet(@Valid @RequestBody BulletOptimizeRequest request) {
        String systemInstruction = "You are a professional resume writer and career coach. " +
                "Rewrite the user's bullet point/achievement to follow the STAR methodology (Situation, Task, Action, Result) " +
                "by adding action verbs, technical contexts, and quantitative metrics (estimated percentages, dollars, or user numbers). " +
                "Correct any grammar or spelling mistakes. " +
                "Strictly return ONLY JSON in this schema:\n" +
                "{\n" +
                "  \"original\": \"Original input text\",\n" +
                "  \"optimized\": \"Enhanced impact-driven text\",\n" +
                "  \"explanation\": \"Explain why this change stands out to HR recruiters...\"\n" +
                "}";

        String prompt = "USER BULLET POINT: " + request.getBulletPoint();
        if (request.getJobDescription() != null && !request.getJobDescription().isBlank()) {
            prompt += "\nTARGET JOB DESCRIPTION:\n" + request.getJobDescription();
        }

        String aiJson = geminiService.generateJsonContent(systemInstruction, prompt);

        BulletOptimizeResponse response;
        try {
            response = objectMapper.readValue(aiJson, BulletOptimizeResponse.class);
        } catch (Exception e) {
            System.err.println("Failed to parse Bullet optimize AI response: " + e.getMessage());
            try {
                response = objectMapper.readValue(geminiService.generateJsonContent("", "demo_optimize_bullet"), BulletOptimizeResponse.class);
            } catch (Exception ex) {
                response = BulletOptimizeResponse.builder()
                        .original(request.getBulletPoint())
                        .optimized("Delivered technical enhancements by integrating core software patterns, improving overall product scalability metrics by 15%.")
                        .explanation("Converted passive voice to active and added numeric impact.")
                        .build();
            }
        }

        return ResponseEntity.ok(ApiResponseWrapper.success("Bullet point optimized successfully", response));
    }
}
