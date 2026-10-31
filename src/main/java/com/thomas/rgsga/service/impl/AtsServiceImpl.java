package com.thomas.rgsga.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomas.rgsga.dto.AtsRequest;
import com.thomas.rgsga.dto.AtsResponse;
import com.thomas.rgsga.entity.AtsAnalysis;
import com.thomas.rgsga.entity.Resume;
import com.thomas.rgsga.entity.User;
import com.thomas.rgsga.exception.BadRequestException;
import com.thomas.rgsga.exception.ResourceNotFoundException;
import com.thomas.rgsga.repository.AtsAnalysisRepository;
import com.thomas.rgsga.repository.ResumeRepository;
import com.thomas.rgsga.service.AtsService;
import com.thomas.rgsga.service.GeminiClient;
import com.thomas.rgsga.service.HeuristicEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
public class AtsServiceImpl implements AtsService {

    private final GeminiClient geminiClient;
    private final HeuristicEngine heuristicEngine;
    private final ResumeRepository resumeRepository;
    private final AtsAnalysisRepository atsAnalysisRepository;
    private final ObjectMapper objectMapper;

    public AtsServiceImpl(GeminiClient geminiClient,
                          HeuristicEngine heuristicEngine,
                          ResumeRepository resumeRepository,
                          AtsAnalysisRepository atsAnalysisRepository,
                          ObjectMapper objectMapper) {
        this.geminiClient = geminiClient;
        this.heuristicEngine = heuristicEngine;
        this.resumeRepository = resumeRepository;
        this.atsAnalysisRepository = atsAnalysisRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public AtsResponse analyzeResume(User user, AtsRequest request) {
        String resumeText = "";
        Resume resume = null;

        if (request.getResumeId() != null) {
            resume = resumeRepository.findById(request.getResumeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Resume not found: " + request.getResumeId()));

            StringBuilder sb = new StringBuilder();
            sb.append(resume.getFullName()).append("\n");
            sb.append(resume.getTitle()).append("\n");
            sb.append(resume.getSummary()).append("\n");
            sb.append("Skills: ").append(resume.getSkillsCsv()).append("\n");

            if (resume.getExperiences() != null) {
                resume.getExperiences().forEach(exp ->
                        sb.append(exp.getRole()).append(" at ").append(exp.getCompany()).append("\n").append(exp.getDescription()).append("\n")
                );
            }
            if (resume.getEducations() != null) {
                resume.getEducations().forEach(edu ->
                        sb.append(edu.getDegree()).append(" in ").append(edu.getField()).append(" from ").append(edu.getSchool()).append("\n")
                );
            }
            if (resume.getProjects() != null) {
                resume.getProjects().forEach(p ->
                        sb.append(p.getName()).append(": ").append(p.getDescription()).append("\n")
                );
            }
            resumeText = sb.toString();
        } else if (request.getResumeText() != null && !request.getResumeText().isBlank()) {
            resumeText = request.getResumeText();
        } else {
            throw new BadRequestException("Either resumeId or resumeText must be provided");
        }

        AtsResponse response = null;

        // 1. Attempt Gemini analysis if configured
        String prompt = "RESUME TEXT:\n" + resumeText + "\n\nTARGET JOB DESCRIPTION:\n" + request.getJobDescription();
        String systemInstruction = "You are an ATS compatibility evaluator. Analyze the resume against the job description. " +
                "Return JSON matching: {\"score\": 85, \"keywordMatchScore\": 80, \"formattingScore\": 90, \"sectionScore\": 90, \"bulletScore\": 75, " +
                "\"matchingSkills\": [], \"missingSkills\": [], \"recommendations\": [{\"category\": \"\", \"suggestion\": \"\", \"impact\": \"High\"}]}";

        Optional<String> aiJson = geminiClient.generateJson(systemInstruction, prompt);
        if (aiJson.isPresent()) {
            try {
                response = objectMapper.readValue(aiJson.get(), AtsResponse.class);
            } catch (Exception ex) {
                log.warn("Failed to parse Gemini ATS output: {}", ex.getMessage());
            }
        }

        // 2. Fallback to intelligent heuristic algorithm
        if (response == null) {
            response = heuristicEngine.computeAtsAnalysis(resumeText, request.getJobDescription());
        }

        // 3. Persist record if user is authenticated
        if (user != null) {
            try {
                AtsAnalysis analysis = AtsAnalysis.builder()
                        .user(user)
                        .resume(resume)
                        .jobTitle(resume != null ? resume.getTitle() : "Software Engineer")
                        .jobDescription(request.getJobDescription())
                        .score(response.getScore())
                        .keywordMatchScore(response.getKeywordMatchScore())
                        .formattingScore(response.getFormattingScore())
                        .sectionScore(response.getSectionScore())
                        .bulletScore(response.getBulletScore())
                        .matchingSkills(String.join(",", response.getMatchingSkills()))
                        .missingSkills(String.join(",", response.getMissingSkills()))
                        .recommendationsJson(objectMapper.writeValueAsString(response.getRecommendations()))
                        .build();
                atsAnalysisRepository.save(analysis);

                if (resume != null) {
                    resume.setAtsScore(response.getScore());
                    resumeRepository.save(resume);
                }
            } catch (Exception ex) {
                log.warn("Failed to persist ATS analysis record: {}", ex.getMessage());
            }
        }

        return response;
    }
}
