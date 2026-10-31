package com.thomas.rgsga.service.impl;

import com.thomas.rgsga.dto.ExtractedKeywordsResponse;
import com.thomas.rgsga.service.HeuristicEngine;
import com.thomas.rgsga.service.KeywordExtractionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class KeywordExtractionServiceImpl implements KeywordExtractionService {

    private final HeuristicEngine heuristicEngine;

    private static final Set<String> STOP_WORDS = Set.of(
            "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren't",
            "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by",
            "can't", "cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing", "don't",
            "down", "during", "each", "few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have",
            "haven't", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself", "him",
            "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is",
            "isn't", "it", "it's", "its", "itself", "let's", "me", "more", "most", "mustn't", "my", "myself",
            "no", "nor", "not", "of", "off", "on", "once", "only", "or", "other", "ought", "our", "ours",
            "ourselves", "out", "over", "own", "same", "shan't", "she", "she'd", "she'll", "she's", "should",
            "shouldn't", "so", "some", "such", "than", "that", "that's", "the", "their", "theirs", "them",
            "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll", "they're", "they've",
            "this", "those", "through", "to", "too", "under", "until", "up", "very", "was", "wasn't", "we",
            "we'd", "we'll", "we're", "we've", "were", "weren't", "what", "what's", "when", "when's", "where",
            "where's", "which", "while", "who", "who's", "whom", "why", "why's", "with", "won't", "would",
            "wouldn't", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves",
            "looking", "seeking", "join", "team", "experience", "years", "candidate", "must", "plus", "work", "role"
    );

    private static final List<String> COMMON_SOFT_SKILLS = List.of(
            "Leadership", "Communication", "Problem Solving", "Collaboration", "Critical Thinking",
            "Agile", "Mentorship", "Adaptability", "Time Management", "Attention to Detail", "Cross-Functional"
    );

    public KeywordExtractionServiceImpl(HeuristicEngine heuristicEngine) {
        this.heuristicEngine = heuristicEngine;
    }

    @Override
    public ExtractedKeywordsResponse extractKeywords(String jobDescription) {
        if (jobDescription == null || jobDescription.isBlank()) {
            return ExtractedKeywordsResponse.builder()
                    .technicalSkills(Collections.emptyList())
                    .softSkills(Collections.emptyList())
                    .highFrequencyNounPhrases(Collections.emptyList())
                    .keywordFrequencies(Collections.emptyMap())
                    .inferredRoleCategory("General Software Engineering")
                    .totalKeywordsFound(0)
                    .build();
        }

        // 1. Technical Skills extraction
        Set<String> techSkills = heuristicEngine.extractSkills(jobDescription);

        // 2. Soft Skills extraction
        String lowerText = jobDescription.toLowerCase();
        List<String> matchedSoftSkills = COMMON_SOFT_SKILLS.stream()
                .filter(soft -> lowerText.contains(soft.toLowerCase()))
                .collect(Collectors.toList());

        // 3. High-frequency noun phrase extraction (2-3 word sequences)
        List<String> nounPhrases = extractNounPhrases(jobDescription);

        // 4. Word frequency calculation
        Map<String, Integer> freqMap = calculateWordFrequencies(jobDescription);

        // 5. Inferred role category
        String roleCategory = inferRole(lowerText);

        return ExtractedKeywordsResponse.builder()
                .technicalSkills(new ArrayList<>(techSkills))
                .softSkills(matchedSoftSkills)
                .highFrequencyNounPhrases(nounPhrases)
                .keywordFrequencies(freqMap)
                .inferredRoleCategory(roleCategory)
                .totalKeywordsFound(techSkills.size() + matchedSoftSkills.size())
                .build();
    }

    private List<String> extractNounPhrases(String text) {
        List<String> phrases = new ArrayList<>();
        Pattern requirementPattern = Pattern.compile(
                "(?:experience with|proficiency in|responsible for|knowledge of|working knowledge of|expertise in|skills in)\\s+([a-zA-Z0-9+#.\\s]{3,35})(?:[,.;\\n]|$)",
                Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = requirementPattern.matcher(text);
        while (matcher.find()) {
            String phrase = matcher.group(1).trim();
            if (phrase.length() > 2 && !phrase.toLowerCase().startsWith("and ")) {
                phrases.add(phrase);
            }
        }

        // Bi-gram frequency extraction for technical noun phrases
        String[] words = text.replaceAll("[^a-zA-Z0-9+#.-]", " ")
                .toLowerCase()
                .split("\\s+");
        Map<String, Integer> biGrams = new HashMap<>();
        for (int i = 0; i < words.length - 1; i++) {
            String w1 = words[i];
            String w2 = words[i + 1];
            if (!STOP_WORDS.contains(w1) && !STOP_WORDS.contains(w2) && w1.length() > 2 && w2.length() > 2) {
                String bg = w1 + " " + w2;
                biGrams.put(bg, biGrams.getOrDefault(bg, 0) + 1);
            }
        }

        biGrams.entrySet().stream()
                .filter(e -> e.getValue() >= 2)
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(6)
                .map(Map.Entry::getKey)
                .forEach(phrases::add);

        return phrases.stream().distinct().limit(10).collect(Collectors.toList());
    }

    private Map<String, Integer> calculateWordFrequencies(String text) {
        String[] words = text.replaceAll("[^a-zA-Z0-9+#.-]", " ")
                .toLowerCase()
                .split("\\s+");
        Map<String, Integer> map = new HashMap<>();
        for (String w : words) {
            if (w.length() > 2 && !STOP_WORDS.contains(w)) {
                map.put(w, map.getOrDefault(w, 0) + 1);
            }
        }
        return map.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(15)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private String inferRole(String lower) {
        if (lower.contains("frontend") || lower.contains("react") || lower.contains("vue") || lower.contains("angular") || lower.contains("ui/ux")) {
            return "Frontend Engineer";
        } else if (lower.contains("backend") || lower.contains("spring") || lower.contains("microservices") || lower.contains("api")) {
            return "Backend / Systems Engineer";
        } else if (lower.contains("devops") || lower.contains("kubernetes") || lower.contains("cloud") || lower.contains("aws") || lower.contains("terraform")) {
            return "DevOps / Cloud Architect";
        } else if (lower.contains("data") || lower.contains("machine learning") || lower.contains("python") || lower.contains("ai")) {
            return "Data & AI Engineer";
        }
        return "Full Stack Engineer";
    }
}
