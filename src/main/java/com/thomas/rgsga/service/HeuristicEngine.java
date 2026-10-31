package com.thomas.rgsga.service;

import com.thomas.rgsga.dto.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class HeuristicEngine {

    public static final List<String> KNOWN_TECH_SKILLS = List.of(
            "Java", "Spring Boot", "Spring", "Microservices", "REST API", "GraphQL", "PostgreSQL", "MySQL",
            "MongoDB", "Redis", "Kafka", "Docker", "Kubernetes", "AWS", "Azure", "GCP", "CI/CD", "Git", "GitHub",
            "Linux", "React", "TypeScript", "JavaScript", "HTML", "CSS", "Tailwind CSS", "Node.js", "Express.js",
            "Next.js", "Vue.js", "Angular", "Python", "Django", "FastAPI", "SQL", "Hibernate", "JPA", "Maven",
            "Gradle", "Jest", "JUnit", "Unit Testing", "System Design", "Agile", "Scrum"
    );

    private static final List<String> ACTION_VERBS = List.of(
            "Architected", "Engineered", "Implemented", "Spearheaded", "Optimized", "Scaled", "Automated",
            "Streamlined", "Accelerated", "Delivered", "Transformed", "Pioneered", "Designed", "Revamped"
    );

    public Set<String> extractSkills(String text) {
        if (text == null || text.isBlank()) return Collections.emptySet();
        String lower = text.toLowerCase();
        Set<String> matched = new LinkedHashSet<>();
        for (String skill : KNOWN_TECH_SKILLS) {
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(skill.toLowerCase()) + "\\b");
            if (pattern.matcher(lower).find()) {
                matched.add(skill);
            }
        }
        return matched;
    }

    public AtsResponse computeAtsAnalysis(String resumeText, String jobDescription) {
        Set<String> resumeSkills = extractSkills(resumeText);
        Set<String> jobSkills = extractSkills(jobDescription);

        List<String> matching = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String skill : jobSkills) {
            if (resumeSkills.contains(skill)) {
                matching.add(skill);
            } else {
                missing.add(skill);
            }
        }

        int keywordScore = jobSkills.isEmpty() ? 75 : Math.min(100, Math.round((float) matching.size() / jobSkills.size() * 100));

        String lowerResume = resumeText.toLowerCase();
        int sectionCount = 0;
        if (lowerResume.contains("experience") || lowerResume.contains("work")) sectionCount++;
        if (lowerResume.contains("education") || lowerResume.contains("degree")) sectionCount++;
        if (lowerResume.contains("skill")) sectionCount++;
        if (lowerResume.contains("project")) sectionCount++;
        int sectionScore = Math.min(100, sectionCount * 25);

        int formattingScore = (resumeText.length() > 200 && resumeText.contains("@")) ? 90 : 70;
        int bulletScore = lowerResume.contains("%") || lowerResume.matches(".*\\b\\d+\\b.*") ? 85 : 65;

        int overallScore = Math.round(keywordScore * 0.45f + sectionScore * 0.25f + formattingScore * 0.15f + bulletScore * 0.15f);

        List<RecommendationItem> recs = new ArrayList<>();
        if (!missing.isEmpty()) {
            recs.add(new RecommendationItem("Missing Keywords",
                    "Add targeted keywords required by the job: " + String.join(", ", missing.stream().limit(5).toList()), "High"));
        }
        if (bulletScore < 80) {
            recs.add(new RecommendationItem("Measurable Metrics",
                    "Quantify your bullet points with metrics (e.g. 'Improved API response time by 35%').", "Medium"));
        }
        if (sectionScore < 100) {
            recs.add(new RecommendationItem("Section Completeness",
                    "Ensure clear dedicated headers for Experience, Skills, Education, and Projects.", "High"));
        }
        recs.add(new RecommendationItem("ATS Formatting",
                "Maintain clean single-column structure and standard font formatting for optimal ATS scanner parsing.", "Low"));

        return AtsResponse.builder()
                .score(overallScore)
                .keywordMatchScore(keywordScore)
                .formattingScore(formattingScore)
                .sectionScore(sectionScore)
                .bulletScore(bulletScore)
                .matchingSkills(matching)
                .missingSkills(missing)
                .recommendations(recs)
                .build();
    }

    public SkillGapResponse computeSkillGap(String userSkillsInput, String jobDescription) {
        Set<String> userSkills = extractSkills(userSkillsInput);
        if (userSkills.isEmpty() && userSkillsInput != null) {
            Arrays.stream(userSkillsInput.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .forEach(userSkills::add);
        }

        Set<String> jobSkills = extractSkills(jobDescription);
        if (jobSkills.isEmpty()) {
            jobSkills = new LinkedHashSet<>(List.of("TypeScript", "Docker", "PostgreSQL", "REST API", "Microservices"));
        }

        List<String> matching = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String skill : jobSkills) {
            if (userSkills.stream().anyMatch(us -> us.equalsIgnoreCase(skill))) {
                matching.add(skill);
            } else {
                missing.add(skill);
            }
        }

        int matchPercent = jobSkills.isEmpty() ? 60 : Math.round((float) matching.size() / jobSkills.size() * 100);

        List<RoadmapStep> steps = new ArrayList<>();
        int stepNum = 1;
        for (String m : missing) {
            steps.add(RoadmapStep.builder()
                    .stepNumber(stepNum++)
                    .title("Master " + m)
                    .description("Core concepts, best practices, and integration patterns for " + m + " in enterprise applications.")
                    .timeline("Week " + (stepNum * 2 - 1) + " - Week " + (stepNum * 2))
                    .topics(List.of(m + " Architecture", "Hands-on implementation", "Testing & Deployment"))
                    .build());
            if (stepNum > 5) break;
        }

        RecommendedResources resources = new RecommendedResources();
        for (String m : missing.stream().limit(3).toList()) {
            resources.getCourses().add(new ResourceItem(
                    m + " Complete Masterclass", "Coursera / Udemy",
                    "https://www.coursera.org/search?query=" + m.replace(" ", "+"),
                    "4 Weeks", "Comprehensive deep-dive covering industry real-world applications."
            ));
            resources.getYoutubeVideos().add(new ResourceItem(
                    m + " in 100 Seconds & Full Tutorial", "freeCodeCamp",
                    "https://www.youtube.com/results?search_query=" + m.replace(" ", "+") + "+crash+course",
                    "45 mins", "Rapid architectural overview and syntax crash course."
            ));
            resources.getCertifications().add(new ResourceItem(
                    "Certified " + m + " Practitioner", "Industry Credential",
                    "https://www.google.com/search?q=" + m.replace(" ", "+") + "+official+certification",
                    "$150 - $300", "Recognized professional credential to validate proficiency."
            ));
        }

        return SkillGapResponse.builder()
                .matchPercentage(matchPercent)
                .userSkills(new ArrayList<>(userSkills))
                .jobSkills(new ArrayList<>(jobSkills))
                .missingSkills(missing)
                .learningRoadmap(steps)
                .recommendedResources(resources)
                .build();
    }

    public CareerResponse computeCareerRecommendations(CareerRequest request) {
        String currentTitle = request.getCurrentTitle() != null ? request.getCurrentTitle() : "Software Engineer";
        String skills = request.getSkills() != null ? request.getSkills() : "Java, React, SQL";

        List<CareerRoleItem> roles = List.of(
                new CareerRoleItem("Senior Full Stack Engineer", 92, "High", "$135,000 - $175,000",
                        List.of("Distributed Systems", "Cloud Architecture", "System Design"), "Moderate"),
                new CareerRoleItem("Cloud / DevOps Solutions Architect", 84, "Very High", "$145,000 - $190,000",
                        List.of("Kubernetes", "Terraform", "AWS Solutions Architecture"), "Challenging"),
                new CareerRoleItem("Technical Lead / Engineering Manager", 78, "High", "$155,000 - $210,000",
                        List.of("Team Leadership", "Cross-functional Strategy", "System Architecture"), "Moderate")
        );

        List<RoadmapStep> roadmap = List.of(
                new RoadmapStep(1, "Deepen Distributed Systems Mastery", "Focus on event-driven design, caching, and resiliency.", "Month 1 - 3", List.of("Kafka", "Redis", "CAP Theorem")),
                new RoadmapStep(2, "Cloud Native & Container Orchestration", "Achieve certified practitioner status on cloud platforms.", "Month 4 - 6", List.of("Kubernetes", "AWS EKS", "CI/CD")),
                new RoadmapStep(3, "Lead Cross-team Technical Initiatives", "Drive architectural decisions and mentor junior engineers.", "Month 7 - 12", List.of("System Design", "RFCs", "Mentorship"))
        );

        return CareerResponse.builder()
                .recommendedRoles(roles)
                .salaryInsights("Market median for mid-to-senior profiles with your tech stack ranges from $120k to $180k depending on region.")
                .marketDemand("Enterprise demand for modern full stack and cloud native engineers remains exceptionally robust (+18% YoY growth).")
                .roadmap(roadmap)
                .build();
    }

    public InterviewResponse computeInterviewPrep(InterviewRequest request) {
        String title = request.getJobTitle();
        List<TechnicalQuestion> techQuestions = List.of(
                TechnicalQuestion.builder()
                        .question("How do you design a scalable and resilient REST API with authentication and rate limiting?")
                        .difficulty("Medium")
                        .expectedKeyPoints(List.of("JWT token lifecycle & rotation", "Idempotency", "Redis token bucket rate limiter", "Error contract consistency"))
                        .sampleAnswer("Implement stateless token validation with Spring Security filters, offload rate limiting to a Redis bucket filter, and enforce structured error wrappers.")
                        .build(),
                TechnicalQuestion.builder()
                        .question("Explain database transaction isolation levels and how you handle concurrent updates in high-throughput tables.")
                        .difficulty("Hard")
                        .expectedKeyPoints(List.of("READ COMMITTED vs REPEATABLE READ", "Optimistic locking (@Version)", "Pessimistic locking", "Deadlock resolution"))
                        .sampleAnswer("Use optimistic locking with versioning for write-heavy entities and select appropriate isolation levels to prevent phantom reads without blocking read operations.")
                        .build(),
                TechnicalQuestion.builder()
                        .question("How do you troubleshoot high latency or memory leaks in a microservices deployment?")
                        .difficulty("Hard")
                        .expectedKeyPoints(List.of("Distributed tracing (OpenTelemetry)", "Heap dump analysis (JProfiler/MAT)", "Connection pool sizing", "GC pause monitoring"))
                        .sampleAnswer("Analyze distributed traces to isolate the bottleneck service, inspect HikariCP connection pool metrics, and generate heap dumps to trace object retention.")
                        .build()
        );

        List<BehavioralQuestion> behavioralQuestions = List.of(
                new BehavioralQuestion(
                        "Tell me about a time you resolved a major production bug under high pressure.",
                        "Situation: System issue impacting active users. Task: Identify root cause quickly. Action: Rolled back release, triaged logs, reproduced in staging. Result: Zero data loss and post-mortem safeguards deployed.",
                        "Emphasize calm methodical triage, stakeholder communication, and prevention of recurrence."
                ),
                new BehavioralQuestion(
                        "Describe a situation where you had a technical disagreement with a colleague or lead.",
                        "Situation: Competing database schema proposals. Task: Align on the best architecture. Action: Benchmarked query performance with mock data and held an objective review. Result: Consensus reached on hybrid approach.",
                        "Focus on data-driven decision making, humility, and maintaining strong collaborative relationships."
                )
        );

        return InterviewResponse.builder()
                .technicalQuestions(techQuestions)
                .behavioralQuestions(behavioralQuestions)
                .systemDesignOrCodingTopics(List.of("Microservices Architecture", "Caching Strategies", "Database Sharding", "JWT & OAuth2 Security", "Concurrency & Thread Safety"))
                .preparationTips(List.of(
                        "Practice explaining your thought process out loud using the STAR method for behavioral questions.",
                        "Review core data structures and system design fundamentals before the technical round.",
                        "Prepare 2-3 thoughtful questions about the company's engineering culture and technical roadmap."
                ))
                .build();
    }

    public BulletOptimizeResponse optimizeBullet(BulletOptimizeRequest request) {
        String original = request.getBulletPoint().trim();
        String verb = ACTION_VERBS.get(Math.abs(original.hashCode()) % ACTION_VERBS.size());
        String cleaned = original.replaceAll("^(I\\s+|Responsible for\\s+|Worked on\\s+|Handled\\s+)", "");
        if (cleaned.length() > 0) {
            cleaned = Character.toLowerCase(cleaned.charAt(0)) + cleaned.substring(1);
        }

        String optimized = verb + " " + cleaned + ", boosting throughput by 28% and cutting latency across production workflows.";
        String explanation = "Converted passive phrasing to strong action verb ('" + verb + "') and added quantitative business outcome metrics.";

        return BulletOptimizeResponse.builder()
                .original(original)
                .optimized(optimized)
                .explanation(explanation)
                .build();
    }
}
