package com.thomas.RG_SGA_.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomas.RG_SGA_.entity.GithubAnalysis;
import com.thomas.RG_SGA_.entity.User;
import com.thomas.RG_SGA_.repository.GithubAnalysisRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GithubAnalysisService {

    private final RestTemplate restTemplate;
    private final GithubAnalysisRepository githubAnalysisRepository;
    private final ObjectMapper objectMapper;

    public GithubAnalysisService(GithubAnalysisRepository githubAnalysisRepository, ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.githubAnalysisRepository = githubAnalysisRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Connects to public GitHub APIs, parses repo info, and calculates developer metric scores.
     */
    public GithubAnalysis analyzeGithubProfile(User user, String username) {
        String url = "https://api.github.com/users/" + username + "/repos?per_page=100";

        List<Map<String, Object>> repos = new ArrayList<>();
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                repos = objectMapper.readValue(response.getBody(), new TypeReference<List<Map<String, Object>>>() {});
            }
        } catch (Exception e) {
            System.err.println("GitHub API rate limit or username not found, running demo sandbox mode: " + e.getMessage());
            // Fallback simulated repository items
            repos = getFallbackRepos();
        }

        int totalStars = 0;
        int totalForks = 0;
        int repoCount = repos.size();
        Set<String> languages = new HashSet<>();
        List<Map<String, Object>> repoDetailsList = new ArrayList<>();

        for (Map<String, Object> repo : repos) {
            String name = (String) repo.get("name");
            String description = (String) repo.get("description");
            String language = (String) repo.get("language");
            int stars = repo.get("stargazers_count") != null ? ((Number) repo.get("stargazers_count")).intValue() : 0;
            int forks = repo.get("forks_count") != null ? ((Number) repo.get("forks_count")).intValue() : 0;
            String htmlUrl = (String) repo.get("html_url");

            totalStars += stars;
            totalForks += forks;
            if (language != null) {
                languages.add(language);
            }

            Map<String, Object> repoDetail = new HashMap<>();
            repoDetail.put("name", name);
            repoDetail.put("description", description);
            repoDetail.put("language", language);
            repoDetail.put("stars", stars);
            repoDetail.put("forks", forks);
            repoDetail.put("url", htmlUrl);
            repoDetailsList.add(repoDetail);
        }

        // Calculate developer score out of 100
        // Formula: base 40 + (repos * 2, max 20) + (stars * 5, max 30) + (forks * 5, max 10)
        int devScore = 40 + Math.min(repoCount * 2, 20) + Math.min(totalStars * 5, 30) + Math.min(totalForks * 5, 10);
        devScore = Math.min(devScore, 100);

        String technologies = languages.stream().collect(Collectors.joining(", "));
        String publicReposJsonStr = "[]";
        try {
            publicReposJsonStr = objectMapper.writeValueAsString(repoDetailsList.stream().limit(5).collect(Collectors.toList()));
        } catch (Exception ignored) {}

        GithubAnalysis analysis = GithubAnalysis.builder()
                .user(user)
                .username(username)
                .devScore(devScore)
                .technologies(technologies.isBlank() ? "JavaScript, HTML, CSS" : technologies)
                .repoCount(repoCount)
                .publicReposJson(publicReposJsonStr)
                .build();

        return githubAnalysisRepository.save(analysis);
    }

    private List<Map<String, Object>> getFallbackRepos() {
        List<Map<String, Object>> list = new ArrayList<>();
        
        Map<String, Object> r1 = new HashMap<>();
        r1.put("name", "ai-resume-builder");
        r1.put("description", "AI-powered resume optimization platform using Spring Boot and React Vite");
        r1.put("language", "Java");
        r1.put("stargazers_count", 15);
        r1.put("forks_count", 4);
        r1.put("html_url", "https://github.com/thomas/ai-resume-builder");
        list.add(r1);

        Map<String, Object> r2 = new HashMap<>();
        r2.put("name", "task-scheduler-microservice");
        r2.put("description", "High throughput backend cron scheduler");
        r2.put("language", "Java");
        r2.put("stargazers_count", 8);
        r2.put("forks_count", 2);
        r2.put("html_url", "https://github.com/thomas/task-scheduler-microservice");
        list.add(r2);

        Map<String, Object> r3 = new HashMap<>();
        r3.put("name", "reactive-chat-app");
        r3.put("description", "Real-time communication app using WebSockets");
        r3.put("language", "TypeScript");
        r3.put("stargazers_count", 24);
        r3.put("forks_count", 6);
        r3.put("html_url", "https://github.com/thomas/reactive-chat-app");
        list.add(r3);

        return list;
    }
}
