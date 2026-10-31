package com.thomas.rgsga.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomas.rgsga.dto.CareerRequest;
import com.thomas.rgsga.dto.CareerResponse;
import com.thomas.rgsga.entity.CareerRecommendation;
import com.thomas.rgsga.entity.User;
import com.thomas.rgsga.repository.CareerRecommendationRepository;
import com.thomas.rgsga.service.CareerService;
import com.thomas.rgsga.service.GeminiClient;
import com.thomas.rgsga.service.HeuristicEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
public class CareerServiceImpl implements CareerService {

    private final GeminiClient geminiClient;
    private final HeuristicEngine heuristicEngine;
    private final CareerRecommendationRepository careerRecommendationRepository;
    private final ObjectMapper objectMapper;

    public CareerServiceImpl(GeminiClient geminiClient,
                             HeuristicEngine heuristicEngine,
                             CareerRecommendationRepository careerRecommendationRepository,
                             ObjectMapper objectMapper) {
        this.geminiClient = geminiClient;
        this.heuristicEngine = heuristicEngine;
        this.careerRecommendationRepository = careerRecommendationRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public CareerResponse recommendCareers(User user, CareerRequest request) {
        CareerResponse response = null;

        String systemInstruction = "You are a senior tech recruiter and career architect. Recommend career trajectories based on skills. " +
                "Return JSON strictly matching: {\"recommendedRoles\": [{\"role\": \"\", \"matchScore\": 85, \"demandLevel\": \"High\", \"avgSalary\": \"\", \"requiredSkills\": [], \"transitionDifficulty\": \"\"}], " +
                "\"salaryInsights\": \"\", \"marketDemand\": \"\", \"roadmap\": [{\"stepNumber\": 1, \"title\": \"\", \"description\": \"\", \"timeline\": \"\", \"topics\": []}]}";

        String prompt = "SKILLS: " + request.getSkills() + "\nCURRENT TITLE: " + request.getCurrentTitle() + "\nEXPERIENCE LEVEL: " + request.getExperienceLevel();

        Optional<String> aiJson = geminiClient.generateJson(systemInstruction, prompt);
        if (aiJson.isPresent()) {
            try {
                response = objectMapper.readValue(aiJson.get(), CareerResponse.class);
            } catch (Exception ex) {
                log.warn("Failed to parse Gemini Career output: {}", ex.getMessage());
            }
        }

        if (response == null) {
            response = heuristicEngine.computeCareerRecommendations(request);
        }

        if (user != null) {
            try {
                CareerRecommendation entity = CareerRecommendation.builder()
                        .user(user)
                        .recommendedRoles(objectMapper.writeValueAsString(response.getRecommendedRoles()))
                        .salaryInsights(response.getSalaryInsights())
                        .marketDemand(response.getMarketDemand())
                        .roadmapJson(objectMapper.writeValueAsString(response.getRoadmap()))
                        .build();
                careerRecommendationRepository.save(entity);
            } catch (Exception ex) {
                log.warn("Failed to persist CareerRecommendation record: {}", ex.getMessage());
            }
        }

        return response;
    }
}
