package com.thomas.rgsga.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class GeminiClient {

    @Value("${application.security.gemini.api-key:${GEMINI_API_KEY:}}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeminiClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank() && !apiKey.contains("your_gemini_api_key");
    }

    /**
     * Executes prompt on Gemini 1.5 Flash requesting strict JSON output.
     * Returns Optional.empty() if API key is not configured or on network/quota failure.
     */
    public Optional<String> generateJson(String systemInstruction, String prompt) {
        if (!isConfigured()) {
            log.info("Gemini API key is not configured. Falling back to local intelligent heuristic engine.");
            return Optional.empty();
        }

        String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();

            if (systemInstruction != null && !systemInstruction.isBlank()) {
                body.put("systemInstruction", Map.of("parts", List.of(Map.of("text", systemInstruction))));
            }

            body.put("contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))));
            body.put("generationConfig", Map.of("responseMimeType", "application/json"));

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(endpoint, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode candidates = root.path("candidates");
                if (candidates.isArray() && !candidates.isEmpty()) {
                    JsonNode textNode = candidates.get(0).path("content").path("parts").get(0).path("text");
                    if (!textNode.isMissingNode()) {
                        return Optional.of(textNode.asText());
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("Gemini API invocation failed ({}). Switching to deterministic heuristic engine.", ex.getMessage());
        }

        return Optional.empty();
    }

    public Optional<String> generateText(String systemInstruction, String prompt) {
        if (!isConfigured()) {
            return Optional.empty();
        }

        String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            if (systemInstruction != null && !systemInstruction.isBlank()) {
                body.put("systemInstruction", Map.of("parts", List.of(Map.of("text", systemInstruction))));
            }
            body.put("contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))));

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(endpoint, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode candidates = root.path("candidates");
                if (candidates.isArray() && !candidates.isEmpty()) {
                    JsonNode textNode = candidates.get(0).path("content").path("parts").get(0).path("text");
                    if (!textNode.isMissingNode()) {
                        return Optional.of(textNode.asText());
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("Gemini text generation failed ({}).", ex.getMessage());
        }

        return Optional.empty();
    }
}
