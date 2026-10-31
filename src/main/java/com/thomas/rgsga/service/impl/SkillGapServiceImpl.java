package com.thomas.rgsga.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomas.rgsga.dto.SkillGapRequest;
import com.thomas.rgsga.dto.SkillGapResponse;
import com.thomas.rgsga.entity.Skill;
import com.thomas.rgsga.entity.SkillGapAnalysis;
import com.thomas.rgsga.entity.User;
import com.thomas.rgsga.exception.ResourceNotFoundException;
import com.thomas.rgsga.repository.SkillGapAnalysisRepository;
import com.thomas.rgsga.repository.SkillRepository;
import com.thomas.rgsga.service.GeminiClient;
import com.thomas.rgsga.service.HeuristicEngine;
import com.thomas.rgsga.service.SkillGapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class SkillGapServiceImpl implements SkillGapService {

    private final GeminiClient geminiClient;
    private final HeuristicEngine heuristicEngine;
    private final SkillRepository skillRepository;
    private final SkillGapAnalysisRepository skillGapAnalysisRepository;
    private final ObjectMapper objectMapper;

    public SkillGapServiceImpl(GeminiClient geminiClient,
                               HeuristicEngine heuristicEngine,
                               SkillRepository skillRepository,
                               SkillGapAnalysisRepository skillGapAnalysisRepository,
                               ObjectMapper objectMapper) {
        this.geminiClient = geminiClient;
        this.heuristicEngine = heuristicEngine;
        this.skillRepository = skillRepository;
        this.skillGapAnalysisRepository = skillGapAnalysisRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public SkillGapResponse analyzeSkillGap(User user, SkillGapRequest request) {
        String userSkillsStr = request.getUserSkills();
        if ((userSkillsStr == null || userSkillsStr.isBlank()) && user != null) {
            List<Skill> dbSkills = skillRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
            if (!dbSkills.isEmpty()) {
                userSkillsStr = dbSkills.stream().map(Skill::getSkillName).collect(Collectors.joining(", "));
            }
        }
        if (userSkillsStr == null || userSkillsStr.isBlank()) {
            userSkillsStr = "HTML, CSS, JavaScript, SQL";
        }

        SkillGapResponse response = null;

        // 1. Attempt Gemini analysis
        String systemInstruction = "You are a career advisor. Compare user skills with the job description and identify gaps. " +
                "Return JSON matching: {\"matchPercentage\": 70, \"userSkills\": [], \"jobSkills\": [], \"missingSkills\": [], " +
                "\"learningRoadmap\": [{\"stepNumber\": 1, \"title\": \"\", \"description\": \"\", \"timeline\": \"\", \"topics\": []}], " +
                "\"recommendedResources\": {\"courses\": [{\"title\": \"\", \"provider\": \"\", \"url\": \"\", \"durationOrCost\": \"\", \"description\": \"\"}], " +
                "\"youtubeVideos\": [], \"certifications\": []}}";
        String prompt = "USER SKILLS:\n" + userSkillsStr + "\n\nTARGET JOB DESCRIPTION:\n" + request.getJobDescription();

        Optional<String> aiJson = geminiClient.generateJson(systemInstruction, prompt);
        if (aiJson.isPresent()) {
            try {
                response = objectMapper.readValue(aiJson.get(), SkillGapResponse.class);
            } catch (Exception ex) {
                log.warn("Failed to parse Gemini SkillGap output: {}", ex.getMessage());
            }
        }

        // 2. Local heuristic fallback
        if (response == null) {
            response = heuristicEngine.computeSkillGap(userSkillsStr, request.getJobDescription());
        }

        // 3. Persist record if user is authenticated
        if (user != null) {
            try {
                SkillGapAnalysis entity = SkillGapAnalysis.builder()
                        .user(user)
                        .userSkills(String.join(", ", response.getUserSkills()))
                        .jobSkills(String.join(", ", response.getJobSkills()))
                        .missingSkills(String.join(", ", response.getMissingSkills()))
                        .learningRoadmapJson(objectMapper.writeValueAsString(response.getLearningRoadmap()))
                        .recommendedResourcesJson(objectMapper.writeValueAsString(response.getRecommendedResources()))
                        .build();
                skillGapAnalysisRepository.save(entity);
            } catch (Exception ex) {
                log.warn("Failed to persist SkillGapAnalysis record: {}", ex.getMessage());
            }
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillGapAnalysis> getUserAnalyses(User user) {
        return skillGapAnalysisRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public SkillGapAnalysis getAnalysisById(User user, Long id) {
        return skillGapAnalysisRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill gap analysis not found: " + id));
    }

    @Override
    public void deleteAnalysis(User user, Long id) {
        skillGapAnalysisRepository.findByIdAndUserId(id, user.getId())
                .ifPresent(skillGapAnalysisRepository::delete);
    }
}
