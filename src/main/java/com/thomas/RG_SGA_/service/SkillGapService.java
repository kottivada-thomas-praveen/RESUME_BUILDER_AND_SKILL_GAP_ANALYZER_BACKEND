package com.thomas.RG_SGA_.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomas.RG_SGA_.dto.SkillGapRequest;
import com.thomas.RG_SGA_.dto.SkillGapResponse;
import com.thomas.RG_SGA_.entity.Skill;
import com.thomas.RG_SGA_.entity.SkillGapAnalysis;
import com.thomas.RG_SGA_.entity.User;
import com.thomas.RG_SGA_.repository.SkillGapAnalysisRepository;
import com.thomas.RG_SGA_.repository.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillGapService {

    private final GeminiService geminiService;
    private final SkillRepository skillRepository;
    private final SkillGapAnalysisRepository skillGapAnalysisRepository;
    private final ObjectMapper objectMapper;

    public SkillGapService(GeminiService geminiService, SkillRepository skillRepository,
                           SkillGapAnalysisRepository skillGapAnalysisRepository, ObjectMapper objectMapper) {
        this.geminiService = geminiService;
        this.skillRepository = skillRepository;
        this.skillGapAnalysisRepository = skillGapAnalysisRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Conducts a detailed skill gap analysis between the user's profiles and a job description.
     */
    public SkillGapResponse analyzeSkillGap(User user, SkillGapRequest request) {
        String userSkillsStr = request.getUserSkills();
        
        // If not provided in request, read from the database skills table
        if (userSkillsStr == null || userSkillsStr.isBlank()) {
            List<Skill> dbSkills = skillRepository.findByUserId(user.getId());
            if (!dbSkills.isEmpty()) {
                userSkillsStr = dbSkills.stream().map(Skill::getSkillName).collect(Collectors.joining(", "));
            } else {
                userSkillsStr = "HTML, CSS, JavaScript"; // Default basic skills
            }
        }

        String systemInstruction = "You are an expert technical career advisor and skill gap analyst. " +
                "Evaluate the candidate's skills against the job description requirements. " +
                "Identify which job skills are missing, and generate: \n" +
                "1. A sequential learning roadmap (list of stepNumber, title, description, timeline).\n" +
                "2. Recommended resources including courses, youtubeVideos, and certifications with url, provider, and duration.\n" +
                "Strictly return ONLY JSON in this schema:\n" +
                "{\n" +
                "  \"userSkills\": [\"skill1\", \"skill2\"],\n" +
                "  \"jobSkills\": [\"skill1\", \"skill2\", \"skill3\"],\n" +
                "  \"missingSkills\": [\"skill3\"],\n" +
                "  \"learningRoadmap\": [\n" +
                "    { \"stepNumber\": 1, \"title\": \"Step Title\", \"description\": \"Step Desc\", \"timeline\": \"Week 1\" }\n" +
                "  ],\n" +
                "  \"recommendedResources\": {\n" +
                "    \"courses\": [ { \"title\": \"Course Title\", \"provider\": \"Coursera\", \"url\": \"http://...\", \"durationOrCost\": \"4 weeks\" } ],\n" +
                "    \"youtubeVideos\": [ { \"title\": \"Video Title\", \"provider\": \"Youtube Channel\", \"url\": \"http://...\", \"durationOrCost\": \"15 min\" } ],\n" +
                "    \"certifications\": [ { \"title\": \"Cert Name\", \"provider\": \"AWS\", \"url\": \"http://...\", \"durationOrCost\": \"$150\" } ]\n" +
                "  }\n" +
                "}";

        String prompt = "USER SKILLS:\n" + userSkillsStr + "\n\nJOB DESCRIPTION:\n" + request.getJobDescription();
        String aiJson = geminiService.generateJsonContent(systemInstruction, prompt);

        SkillGapResponse response;
        try {
            response = objectMapper.readValue(aiJson, SkillGapResponse.class);
        } catch (Exception e) {
            System.err.println("Failed to parse Skill Gap AI response: " + e.getMessage());
            // Parse fallback mock response
            try {
                response = objectMapper.readValue(geminiService.generateJsonContent("", "demo_skill_gap_analysis"), SkillGapResponse.class);
            } catch (Exception ex) {
                response = new SkillGapResponse();
            }
        }

        // Save analysis to DB
        try {
            SkillGapAnalysis analysis = SkillGapAnalysis.builder()
                    .user(user)
                    .userSkills(String.join(",", response.getUserSkills()))
                    .jobSkills(String.join(",", response.getJobSkills()))
                    .missingSkills(String.join(",", response.getMissingSkills()))
                    .learningRoadmapJson(objectMapper.writeValueAsString(response.getLearningRoadmap()))
                    .recommendedResourcesJson(objectMapper.writeValueAsString(response.getRecommendedResources()))
                    .build();
            skillGapAnalysisRepository.save(analysis);
        } catch (Exception e) {
            System.err.println("Could not save SkillGapAnalysis to DB: " + e.getMessage());
        }

        return response;
    }
}
