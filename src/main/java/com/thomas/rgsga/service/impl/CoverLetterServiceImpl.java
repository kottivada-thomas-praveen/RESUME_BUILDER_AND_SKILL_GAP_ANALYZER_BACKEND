package com.thomas.rgsga.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomas.rgsga.dto.CoverLetterRequest;
import com.thomas.rgsga.dto.CoverLetterResponse;
import com.thomas.rgsga.entity.Resume;
import com.thomas.rgsga.entity.User;
import com.thomas.rgsga.repository.ResumeRepository;
import com.thomas.rgsga.service.CoverLetterService;
import com.thomas.rgsga.service.GeminiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CoverLetterServiceImpl implements CoverLetterService {

    private final GeminiClient geminiClient;
    private final ResumeRepository resumeRepository;
    private final ObjectMapper objectMapper;

    public CoverLetterServiceImpl(GeminiClient geminiClient,
                                  ResumeRepository resumeRepository,
                                  ObjectMapper objectMapper) {
        this.geminiClient = geminiClient;
        this.resumeRepository = resumeRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public CoverLetterResponse generateCoverLetter(User user, CoverLetterRequest request) {
        String applicantName = request.getApplicantName();
        String skills = request.getSkills();
        String expYears = request.getExperienceYears() != null && !request.getExperienceYears().isBlank()
                ? request.getExperienceYears() : "3+";
        String hiringManager = request.getHiringManager() != null && !request.getHiringManager().isBlank()
                ? request.getHiringManager() : "Hiring Manager";

        // If resumeId provided, pull details
        if (request.getResumeId() != null && user != null) {
            Optional<Resume> resumeOpt = resumeRepository.findByIdAndUserIdWithDetails(request.getResumeId(), user.getId());
            if (resumeOpt.isPresent()) {
                Resume r = resumeOpt.get();
                if (applicantName == null || applicantName.isBlank()) applicantName = r.getFullName();
                if (skills == null || skills.isBlank()) skills = r.getSkillsCsv();
            }
        }

        if (applicantName == null || applicantName.isBlank()) {
            applicantName = user != null ? user.getName() : "Candidate";
        }

        if (skills == null || skills.isBlank()) {
            skills = "Java, Spring Boot, React, TypeScript, PostgreSQL, Docker, AWS";
        }

        List<String> topSkills = Arrays.stream(skills.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .limit(5)
                .collect(Collectors.toList());

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));

        // 1. Try Gemini generation
        String systemInstruction = "You are a professional executive career coach. Generate a compelling, polished cover letter.";
        String prompt = "Write a tailored cover letter for " + applicantName + " applying for the position of " +
                request.getTargetRole() + " at " + request.getCompanyName() + ". Incorporate these key skills: " +
                String.join(", ", topSkills) + ". Experience: " + expYears + " years. Hiring Manager: " + hiringManager +
                ". Format professionally without markdown headers, ready to send.";

        Optional<String> aiGenerated = geminiClient.generateText(systemInstruction, prompt);
        if (aiGenerated.isPresent() && !aiGenerated.get().isBlank()) {
            return CoverLetterResponse.builder()
                    .coverLetterText(aiGenerated.get().trim())
                    .companyName(request.getCompanyName())
                    .targetRole(request.getTargetRole())
                    .highlightedSkills(topSkills)
                    .generatedDate(today)
                    .build();
        }

        // 2. Heuristic high-impact cover letter template
        String primarySkill = topSkills.isEmpty() ? "modern full stack technologies" : topSkills.get(0);
        String secondarySkills = topSkills.size() > 1
                ? String.join(", ", topSkills.subList(1, topSkills.size()))
                : "cloud architecture, API engineering, and automated testing";

        StringBuilder sb = new StringBuilder();
        sb.append(today).append("\n\n");
        sb.append("Dear ").append(hiringManager).append(",\n\n");
        sb.append("I am writing to express my strong enthusiasm for the ").append(request.getTargetRole())
                .append(" opportunity at ").append(request.getCompanyName()).append(". ")
                .append("With over ").append(expYears).append(" years of software engineering experience specializing in ")
                .append(primarySkill).append(", as well as deep hands-on expertise across ").append(secondarySkills)
                .append(", I am confident in my ability to make an immediate, positive impact on your engineering initiatives.\n\n");

        sb.append("Throughout my career, I have focused on designing scalable architectures, optimizing high-throughput workflows, ")
                .append("and collaborating across multidisciplinary teams to deliver resilient digital products. ")
                .append("I have consistently delivered measurable outcomes—improving application latency, streamlining CI/CD releases, ")
                .append("and architecting clean, maintainable systems that serve thousands of daily users.\n\n");

        sb.append("What excites me most about joining ").append(request.getCompanyName())
                .append(" is your commitment to technical innovation and engineering excellence. ")
                .append("My background aligning core technologies like ").append(String.join(", ", topSkills))
                .append(" with high-impact business requirements makes me an ideal fit for your current technical roadmap.\n\n");

        sb.append("Thank you for your time and consideration. I would welcome the opportunity to discuss how my skill set and passion ")
                .append("can contribute to the continued success of ").append(request.getCompanyName()).append(".\n\n");

        sb.append("Sincerely,\n");
        sb.append(applicantName);

        return CoverLetterResponse.builder()
                .coverLetterText(sb.toString())
                .companyName(request.getCompanyName())
                .targetRole(request.getTargetRole())
                .highlightedSkills(topSkills)
                .generatedDate(today)
                .build();
    }
}
