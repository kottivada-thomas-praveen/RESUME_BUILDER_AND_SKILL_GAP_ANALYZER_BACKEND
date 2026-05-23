package com.thomas.RG_SGA_.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomas.RG_SGA_.dto.CareerRequest;
import com.thomas.RG_SGA_.dto.CareerResponse;
import com.thomas.RG_SGA_.entity.CareerRecommendation;
import com.thomas.RG_SGA_.entity.User;
import com.thomas.RG_SGA_.repository.CareerRecommendationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CareerService {

    private final GeminiService geminiService;
    private final CareerRecommendationRepository careerRecommendationRepository;
    private final ObjectMapper objectMapper;

    public CareerService(GeminiService geminiService, CareerRecommendationRepository careerRecommendationRepository,
                         ObjectMapper objectMapper) {
        this.geminiService = geminiService;
        this.careerRecommendationRepository = careerRecommendationRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Generates personalized career roles and market recommendations based on a user's skills and tier.
     */
    public CareerResponse recommendCareers(User user, CareerRequest request) {
        String systemInstruction = "You are a senior executive tech recruiter and talent strategist. " +
                "Based on the user's skills, title, and seniority level, generate customized career recommendations.\n" +
                "Provide:\n" +
                "1. A list of recommended roles (recommendedRoles).\n" +
                "2. Salary range projections and averages (salaryInsights).\n" +
                "3. Year-on-Year growth demand and market health assessments (marketDemand).\n" +
                "4. A structured roadmap for transitions (roadmap).\n" +
                "Strictly return ONLY JSON in this schema:\n" +
                "{\n" +
                "  \"recommendedRoles\": [\"Role A\", \"Role B\"],\n" +
                "  \"salaryInsights\": [\n" +
                "    { \"role\": \"Role A\", \"salaryRange\": \"$100k - $120k\", \"averageSalary\": \"$110k\" }\n" +
                "  ],\n" +
                "  \"marketDemand\": [\n" +
                "    { \"role\": \"Role A\", \"demandLevel\": \"High\", \"growthRate\": \"12% YoY\" }\n" +
                "  ],\n" +
                "  \"roadmap\": [\n" +
                "    { \"stepNumber\": 1, \"title\": \"Step Title\", \"description\": \"Step description...\", \"timeline\": \"Month 1\" }\n" +
                "  ]\n" +
                "}";

        String prompt = "USER CURRENT TITLE: " + request.getCurrentTitle() +
                "\nSENIORITY LEVEL: " + request.getExperienceLevel() +
                "\nUSER CURRENT SKILLS: " + request.getSkills();

        String aiJson = geminiService.generateJsonContent(systemInstruction, prompt);

        CareerResponse response;
        try {
            response = objectMapper.readValue(aiJson, CareerResponse.class);
        } catch (Exception e) {
            System.err.println("Failed to parse Career recommendation AI response: " + e.getMessage());
            try {
                response = objectMapper.readValue(geminiService.generateJsonContent("", "demo_career_recommendation"), CareerResponse.class);
            } catch (Exception ex) {
                response = new CareerResponse();
            }
        }

        // Save career recommendation to database
        try {
            CareerRecommendation dbRec = CareerRecommendation.builder()
                    .user(user)
                    .recommendedRoles(String.join(",", response.getRecommendedRoles()))
                    .salaryInsights(objectMapper.writeValueAsString(response.getSalaryInsights()))
                    .marketDemand(objectMapper.writeValueAsString(response.getMarketDemand()))
                    .roadmapJson(objectMapper.writeValueAsString(response.getRoadmap()))
                    .build();
            careerRecommendationRepository.save(dbRec);
        } catch (Exception e) {
            System.err.println("Could not save CareerRecommendation to DB: " + e.getMessage());
        }

        return response;
    }
}
