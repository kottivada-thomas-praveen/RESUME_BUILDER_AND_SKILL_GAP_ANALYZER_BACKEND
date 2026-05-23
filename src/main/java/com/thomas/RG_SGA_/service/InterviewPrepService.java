package com.thomas.RG_SGA_.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomas.RG_SGA_.dto.InterviewRequest;
import com.thomas.RG_SGA_.dto.InterviewResponse;
import com.thomas.RG_SGA_.entity.InterviewPrep;
import com.thomas.RG_SGA_.entity.User;
import com.thomas.RG_SGA_.repository.InterviewPrepRepository;
import org.springframework.stereotype.Service;

@Service
public class InterviewPrepService {

    private final GeminiService geminiService;
    private final InterviewPrepRepository interviewPrepRepository;
    private final ObjectMapper objectMapper;

    public InterviewPrepService(GeminiService geminiService, InterviewPrepRepository interviewPrepRepository,
                               ObjectMapper objectMapper) {
        this.geminiService = geminiService;
        this.interviewPrepRepository = interviewPrepRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Generates a fully detailed technical and behavioral mock interview guide based on target job and skills.
     */
    public InterviewResponse generateInterviewPrep(User user, InterviewRequest request) {
        String systemInstruction = "You are a senior tech manager and expert HR mock interviewer. " +
                "Generate custom technical and behavioral questions tailored to the candidate's skills and the target role.\n" +
                "Provide:\n" +
                "1. A list of specific interview questions (questions) containing id, type (technical/behavioral), question, sampleAnswer, and answerGuidelines.\n" +
                "2. General guidelines for technical preparation (technicalPrep).\n" +
                "3. Behavioral interview strategies (behavioralPrep).\n" +
                "Strictly return ONLY JSON in this schema:\n" +
                "{\n" +
                "  \"questions\": [\n" +
                "    { \"id\": 1, \"type\": \"technical\", \"question\": \"Question content?\", \"sampleAnswer\": \"Answer...\", \"answerGuidelines\": \"STAR tips...\" }\n" +
                "  ],\n" +
                "  \"technicalPrep\": \"Focus areas...\",\n" +
                "  \"behavioralPrep\": \"Behavioral focus...\"\n" +
                "}";

        String prompt = "TARGET JOB TITLE: " + request.getJobTitle() +
                "\nTARGET JOB DESCRIPTION:\n" + request.getJobDescription() +
                "\nCANDIDATE SKILLS: " + request.getUserSkills();

        String aiJson = geminiService.generateJsonContent(systemInstruction, prompt);

        InterviewResponse response;
        try {
            response = objectMapper.readValue(aiJson, InterviewResponse.class);
        } catch (Exception e) {
            System.err.println("Failed to parse Interview prep AI response: " + e.getMessage());
            try {
                response = objectMapper.readValue(geminiService.generateJsonContent("", "demo_interview_preparation"), InterviewResponse.class);
            } catch (Exception ex) {
                response = new InterviewResponse();
            }
        }

        // Save interview prep to DB
        try {
            InterviewPrep dbPrep = InterviewPrep.builder()
                    .user(user)
                    .questionsJson(objectMapper.writeValueAsString(response.getQuestions()))
                    .technicalPrep(response.getTechnicalPrep())
                    .behavioralPrep(response.getBehavioralPrep())
                    .build();
            interviewPrepRepository.save(dbPrep);
        } catch (Exception e) {
            System.err.println("Could not save InterviewPrep to DB: " + e.getMessage());
        }

        return response;
    }
}
