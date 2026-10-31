package com.thomas.rgsga.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomas.rgsga.dto.InterviewRequest;
import com.thomas.rgsga.dto.InterviewResponse;
import com.thomas.rgsga.entity.InterviewPrep;
import com.thomas.rgsga.entity.User;
import com.thomas.rgsga.repository.InterviewPrepRepository;
import com.thomas.rgsga.service.GeminiClient;
import com.thomas.rgsga.service.HeuristicEngine;
import com.thomas.rgsga.service.InterviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
public class InterviewServiceImpl implements InterviewService {

    private final GeminiClient geminiClient;
    private final HeuristicEngine heuristicEngine;
    private final InterviewPrepRepository interviewPrepRepository;
    private final ObjectMapper objectMapper;

    public InterviewServiceImpl(GeminiClient geminiClient,
                                HeuristicEngine heuristicEngine,
                                InterviewPrepRepository interviewPrepRepository,
                                ObjectMapper objectMapper) {
        this.geminiClient = geminiClient;
        this.heuristicEngine = heuristicEngine;
        this.interviewPrepRepository = interviewPrepRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public InterviewResponse generateInterviewPrep(User user, InterviewRequest request) {
        InterviewResponse response = null;

        String systemInstruction = "You are a senior tech hiring manager. Generate technical and behavioral interview preparation questions. " +
                "Return JSON strictly matching: {\"technicalQuestions\": [{\"question\": \"\", \"difficulty\": \"Medium\", \"expectedKeyPoints\": [], \"sampleAnswer\": \"\"}], " +
                "\"behavioralQuestions\": [{\"question\": \"\", \"starGuidance\": \"\", \"tips\": \"\"}], " +
                "\"systemDesignOrCodingTopics\": [], \"preparationTips\": []}";

        String prompt = "JOB TITLE: " + request.getJobTitle() + "\nJOB DESCRIPTION: " + request.getJobDescription() + "\nCANDIDATE SKILLS: " + request.getUserSkills();

        Optional<String> aiJson = geminiClient.generateJson(systemInstruction, prompt);
        if (aiJson.isPresent()) {
            try {
                response = objectMapper.readValue(aiJson.get(), InterviewResponse.class);
            } catch (Exception ex) {
                log.warn("Failed to parse Gemini Interview output: {}", ex.getMessage());
            }
        }

        if (response == null) {
            response = heuristicEngine.computeInterviewPrep(request);
        }

        if (user != null) {
            try {
                InterviewPrep entity = InterviewPrep.builder()
                        .user(user)
                        .questionsJson(objectMapper.writeValueAsString(response.getTechnicalQuestions()))
                        .behavioralPrep(objectMapper.writeValueAsString(response.getBehavioralQuestions()))
                        .technicalPrep(String.join(", ", response.getSystemDesignOrCodingTopics()))
                        .build();
                interviewPrepRepository.save(entity);
            } catch (Exception ex) {
                log.warn("Failed to persist InterviewPrep record: {}", ex.getMessage());
            }
        }

        return response;
    }
}
