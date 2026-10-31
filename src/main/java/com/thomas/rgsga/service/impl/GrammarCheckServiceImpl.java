package com.thomas.rgsga.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomas.rgsga.dto.GrammarCheckResponse;
import com.thomas.rgsga.service.GrammarCheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class GrammarCheckServiceImpl implements GrammarCheckService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    private static final String LANGUAGETOOL_API_URL = "https://api.languagetool.org/v2/check";

    // Known common resume typos and suggestions
    private static final Map<String, String> COMMON_TYPOS = Map.ofEntries(
            Map.entry("teh", "the"),
            Map.entry("recieve", "receive"),
            Map.entry("recieved", "received"),
            Map.entry("seperate", "separate"),
            Map.entry("definately", "definitely"),
            Map.entry("responisble", "responsible"),
            Map.entry("manger", "manager"),
            Map.entry("developement", "development"),
            Map.entry("enviornment", "environment"),
            Map.entry("experiance", "experience"),
            Map.entry("implmented", "implemented"),
            Map.entry("colaborated", "collaborated"),
            Map.entry("mainatained", "maintained"),
            Map.entry("optmized", "optimized"),
            Map.entry("postgresl", "postgresql"),
            Map.entry("javascipt", "javascript"),
            Map.entry("databse", "database")
    );

    public GrammarCheckServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public GrammarCheckResponse checkGrammar(String text) {
        if (text == null || text.isBlank()) {
            return GrammarCheckResponse.builder()
                    .hasIssues(false)
                    .issueCount(0)
                    .issues(Collections.emptyList())
                    .cleanedText("")
                    .build();
        }

        // 1. Attempt LanguageTool External API
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("text", text);
            map.add("language", "en-US");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(LANGUAGETOOL_API_URL, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode matches = root.path("matches");

                List<GrammarCheckResponse.GrammarIssue> issues = new ArrayList<>();
                if (matches.isArray()) {
                    for (JsonNode match : matches) {
                        String message = match.path("message").asText();
                        String shortMessage = match.path("shortMessage").asText(message);
                        int offset = match.path("offset").asInt();
                        int length = match.path("length").asInt();
                        String context = match.path("context").path("text").asText("");
                        String ruleCategory = match.path("rule").path("category").path("name").asText("Grammar / Spelling");

                        List<String> replacements = new ArrayList<>();
                        JsonNode repNodes = match.path("replacements");
                        if (repNodes.isArray()) {
                            for (JsonNode r : repNodes) {
                                replacements.add(r.path("value").asText());
                                if (replacements.size() >= 3) break;
                            }
                        }

                        issues.add(GrammarCheckResponse.GrammarIssue.builder()
                                .message(message)
                                .shortMessage(shortMessage)
                                .offset(offset)
                                .length(length)
                                .context(context)
                                .replacements(replacements)
                                .ruleCategory(ruleCategory)
                                .build());
                    }
                }

                log.info("LanguageTool API returned {} issues for text snippet", issues.size());
                return GrammarCheckResponse.builder()
                        .hasIssues(!issues.isEmpty())
                        .issueCount(issues.size())
                        .issues(issues)
                        .cleanedText(text)
                        .build();
            }
        } catch (Exception ex) {
            log.warn("LanguageTool external API check failed ({}), falling back to local heuristic checker", ex.getMessage());
        }

        // 2. Fallback Heuristic Spell & Grammar Checker
        return checkWithLocalHeuristics(text);
    }

    private GrammarCheckResponse checkWithLocalHeuristics(String text) {
        List<GrammarCheckResponse.GrammarIssue> issues = new ArrayList<>();

        // Check common typos
        for (Map.Entry<String, String> entry : COMMON_TYPOS.entrySet()) {
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(entry.getKey()) + "\\b", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                issues.add(GrammarCheckResponse.GrammarIssue.builder()
                        .message("Possible spelling error: '" + matcher.group() + "'")
                        .shortMessage("Spelling error")
                        .offset(matcher.start())
                        .length(matcher.end() - matcher.start())
                        .context(text.substring(Math.max(0, matcher.start() - 10), Math.min(text.length(), matcher.end() + 10)))
                        .replacements(List.of(entry.getValue()))
                        .ruleCategory("Spelling")
                        .build());
            }
        }

        // Check repeated words (e.g. "in in", "the the")
        Pattern repeatedPattern = Pattern.compile("\\b(\\w+)\\s+\\1\\b", Pattern.CASE_INSENSITIVE);
        Matcher repMatcher = repeatedPattern.matcher(text);
        while (repMatcher.find()) {
            issues.add(GrammarCheckResponse.GrammarIssue.builder()
                    .message("Repeated word: '" + repMatcher.group(1) + "'")
                    .shortMessage("Duplicate word")
                    .offset(repMatcher.start())
                    .length(repMatcher.end() - repMatcher.start())
                    .context(repMatcher.group())
                    .replacements(List.of(repMatcher.group(1)))
                    .ruleCategory("Repetition")
                    .build());
        }

        return GrammarCheckResponse.builder()
                .hasIssues(!issues.isEmpty())
                .issueCount(issues.size())
                .issues(issues)
                .cleanedText(text)
                .build();
    }
}
