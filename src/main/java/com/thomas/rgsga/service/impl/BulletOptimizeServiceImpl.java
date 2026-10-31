package com.thomas.rgsga.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomas.rgsga.dto.BulletOptimizeRequest;
import com.thomas.rgsga.dto.BulletOptimizeResponse;
import com.thomas.rgsga.service.BulletOptimizeService;
import com.thomas.rgsga.service.GeminiClient;
import com.thomas.rgsga.service.HeuristicEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class BulletOptimizeServiceImpl implements BulletOptimizeService {

    private final GeminiClient geminiClient;
    private final HeuristicEngine heuristicEngine;
    private final ObjectMapper objectMapper;

    public BulletOptimizeServiceImpl(GeminiClient geminiClient,
                                    HeuristicEngine heuristicEngine,
                                    ObjectMapper objectMapper) {
        this.geminiClient = geminiClient;
        this.heuristicEngine = heuristicEngine;
        this.objectMapper = objectMapper;
    }

    @Override
    public BulletOptimizeResponse optimizeBullet(BulletOptimizeRequest request) {
        String systemInstruction = "You are a professional resume writer. Rewrite the bullet point following the STAR methodology " +
                "by adding active verbs, technical context, and quantitative metrics (e.g. percentages, latency, users). " +
                "Return JSON strictly matching: {\"original\": \"\", \"optimized\": \"\", \"explanation\": \"\"}";

        String prompt = "USER BULLET POINT: " + request.getBulletPoint();
        if (request.getJobDescription() != null && !request.getJobDescription().isBlank()) {
            prompt += "\nTARGET JOB DESCRIPTION: " + request.getJobDescription();
        }

        Optional<String> aiJson = geminiClient.generateJson(systemInstruction, prompt);
        if (aiJson.isPresent()) {
            try {
                return objectMapper.readValue(aiJson.get(), BulletOptimizeResponse.class);
            } catch (Exception ex) {
                log.warn("Failed to parse Gemini Bullet Optimize output: {}", ex.getMessage());
            }
        }

        return heuristicEngine.optimizeBullet(request);
    }
}
