package com.thomas.RG_SGA_.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomas.RG_SGA_.dto.AtsRequest;
import com.thomas.RG_SGA_.dto.AtsResponse;
import com.thomas.RG_SGA_.entity.AtsAnalysis;
import com.thomas.RG_SGA_.entity.Resume;
import com.thomas.RG_SGA_.entity.User;
import com.thomas.RG_SGA_.repository.AtsAnalysisRepository;
import com.thomas.RG_SGA_.repository.ResumeRepository;
import com.thomas.RG_SGA_.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AtsAnalyzerService {

    private final GeminiService geminiService;
    private final AtsAnalysisRepository atsAnalysisRepository;
    private final ResumeRepository resumeRepository;
    private final ObjectMapper objectMapper;

    // A comprehensive set of technical keywords to check overlaps locally
    private static final List<String> TECH_KEYWORDS = Arrays.asList(
            "react", "typescript", "javascript", "node.js", "node", "express", "postgresql", "postgres", 
            "mysql", "mongodb", "nosql", "sql", "next.js", "nextjs", "vue", "angular", "python", "django", 
            "flask", "fastapi", "java", "spring", "springboot", "c#", "dotnet", "asp.net", "php", "laravel", 
            "ruby", "rails", "go", "golang", "rust", "aws", "azure", "gcp", "docker", "kubernetes", "git", 
            "github", "ci/cd", "jenkins", "graphql", "rest", "api", "tailwind", "css", "html", "sass", 
            "bootstrap", "redux", "redis", "elasticsearch", "firebase", "linux", "jest", "cypress", 
            "mocha", "agile", "scrum", "microservices", "serverless", "security", "ai"
    );

    public AtsAnalyzerService(GeminiService geminiService, AtsAnalysisRepository atsAnalysisRepository,
                               ResumeRepository resumeRepository, ObjectMapper objectMapper) {
        this.geminiService = geminiService;
        this.atsAnalysisRepository = atsAnalysisRepository;
        this.resumeRepository = resumeRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Performs a complete ATS scan by comparing a resume (either loaded from DB or pasted) with a job description.
     */
    public AtsResponse analyzeResume(User user, AtsRequest request) {
        String resumeText = "";
        Resume resume = null;

        // If resumeId is provided, load the resume from the database
        if (request.getResumeId() != null) {
            resume = resumeRepository.findById(request.getResumeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Resume not found with ID: " + request.getResumeId()));
            
            // Build text representation from the resume fields
            StringBuilder sb = new StringBuilder();
            sb.append(resume.getFullName()).append("\n");
            sb.append(resume.getTitle()).append("\n");
            sb.append(resume.getSummary()).append("\n");
            sb.append("Skills: ").append(resume.getSkillsCsv()).append("\n");
            
            resume.getExperiences().forEach(exp -> 
                sb.append(exp.getRole()).append(" at ").append(exp.getCompany()).append("\n").append(exp.getDescription()).append("\n")
            );
            resume.getEducations().forEach(edu -> 
                sb.append(edu.getDegree()).append(" in ").append(edu.getField()).append(" from ").append(edu.getSchool()).append("\n")
            );
            resume.getProjects().forEach(proj -> 
                sb.append(proj.getName()).append(": ").append(proj.getDescription()).append("\n")
            );
            
            resumeText = sb.toString();
        } else if (request.getResumeText() != null && !request.getResumeText().isBlank()) {
            resumeText = request.getResumeText();
        } else {
            throw new IllegalArgumentException("Either resumeId or resumeText must be provided");
        }

        // Perform local keyword matching
        String resumeLower = resumeText.toLowerCase();
        String jobLower = request.getJobDescription().toLowerCase();

        List<String> matchingSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String keyword : TECH_KEYWORDS) {
            boolean inJob = jobLower.contains(keyword);
            boolean inResume = resumeLower.contains(keyword);
            if (inJob) {
                if (inResume) {
                    matchingSkills.add(keyword);
                } else {
                    missingSkills.add(keyword);
                }
            }
        }

        int keywordScore = matchingSkills.isEmpty() && missingSkills.isEmpty() ? 75 : 
                (int) Math.round(((double) matchingSkills.size() / (matchingSkills.size() + missingSkills.size())) * 100);

        // Request Gemini to perform structural formatting, section completeness, and achievements analysis
        String systemInstruction = "You are an expert ATS (Applicant Tracking System) optimizer and professional recruiter. " +
                "Evaluate the resume text against the job description. " +
                "Generate structural formatting scores, section completeness scores, impact metrics scores, and custom recommendations. " +
                "Strictly return ONLY a JSON matching the following schema:\n" +
                "{\n" +
                "  \"formattingScore\": 85,\n" +
                "  \"sectionScore\": 90,\n" +
                "  \"bulletScore\": 70,\n" +
                "  \"recommendations\": [\n" +
                "    { \"id\": 1, \"type\": \"high\", \"text\": \"Actionable recommendation title\", \"description\": \"Detailed explanation...\" }\n" +
                "  ]\n" +
                "}";

        String prompt = "RESUME TEXT:\n" + resumeText + "\n\nJOB DESCRIPTION:\n" + request.getJobDescription();
        String aiJson = geminiService.generateJsonContent(systemInstruction, prompt);

        int formattingScore = 75;
        int sectionScore = 80;
        int bulletScore = 65;
        List<AtsResponse.RecommendationItem> recommendations = new ArrayList<>();

        try {
            AtsResponse aiData = objectMapper.readValue(aiJson, AtsResponse.class);
            formattingScore = aiData.getFormattingScore() != null ? aiData.getFormattingScore() : 75;
            sectionScore = aiData.getSectionScore() != null ? aiData.getSectionScore() : 80;
            bulletScore = aiData.getBulletScore() != null ? aiData.getBulletScore() : 65;
            recommendations = aiData.getRecommendations() != null ? aiData.getRecommendations() : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Failed to parse ATS AI response: " + e.getMessage());
            // Fallback mock recommendations if deserialization fails
            try {
                AtsResponse fallback = objectMapper.readValue(geminiService.generateJsonContent("", "demo_ats_analyses"), AtsResponse.class);
                formattingScore = fallback.getFormattingScore();
                sectionScore = fallback.getSectionScore();
                bulletScore = fallback.getBulletScore();
                recommendations = fallback.getRecommendations();
            } catch (Exception ignored) {}
        }

        // Add skill recommendations if there are missing skills
        if (!missingSkills.isEmpty()) {
            recommendations.add(0, AtsResponse.RecommendationItem.builder()
                    .id(99)
                    .type("high")
                    .text("Add missing tech skills: " + missingSkills.stream().limit(3).map(String::toUpperCase).collect(Collectors.joining(", ")))
                    .description("These technical skills are prominently required in the job description but seem to be missing from your resume.")
                    .build());
        }

        // Calculate overall score
        int overallScore = (int) Math.round((keywordScore * 0.4) + (formattingScore * 0.25) + (sectionScore * 0.15) + (bulletScore * 0.2));

        AtsResponse response = AtsResponse.builder()
                .score(overallScore)
                .keywordMatchScore(keywordScore)
                .formattingScore(formattingScore)
                .sectionScore(sectionScore)
                .bulletScore(bulletScore)
                .matchingSkills(matchingSkills)
                .missingSkills(missingSkills)
                .recommendations(recommendations)
                .build();

        // Save analysis record in database for history/analytics
        try {
            String recommendationsJsonStr = objectMapper.writeValueAsString(recommendations);
            AtsAnalysis analysis = AtsAnalysis.builder()
                    .user(user)
                    .resume(resume)
                    .jobTitle(resume != null ? resume.getTitle() : "Pasted Resume")
                    .jobDescription(request.getJobDescription())
                    .score(overallScore)
                    .keywordMatchScore(keywordScore)
                    .formattingScore(formattingScore)
                    .sectionScore(sectionScore)
                    .bulletScore(bulletScore)
                    .matchingSkills(String.join(",", matchingSkills))
                    .missingSkills(String.join(",", missingSkills))
                    .recommendationsJson(recommendationsJsonStr)
                    .build();
            atsAnalysisRepository.save(analysis);
        } catch (Exception e) {
            System.err.println("Could not save AtsAnalysis to DB: " + e.getMessage());
        }

        return response;
    }
}
