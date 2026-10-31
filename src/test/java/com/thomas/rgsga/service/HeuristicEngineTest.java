package com.thomas.rgsga.service;

import com.thomas.rgsga.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeuristicEngineTest {

    private HeuristicEngine heuristicEngine;

    @BeforeEach
    void setUp() {
        heuristicEngine = new HeuristicEngine();
    }

    @Test
    void testComputeAtsAnalysis() {
        String resume = "Alex Mercer. Experienced Full Stack Engineer with Java, Spring Boot, React, and SQL.";
        String job = "We are seeking a Senior Developer proficient in Java, Spring Boot, Docker, and PostgreSQL.";

        AtsResponse response = heuristicEngine.computeAtsAnalysis(resume, job);

        assertNotNull(response);
        assertTrue(response.getScore() > 0);
        assertTrue(response.getMatchingSkills().contains("Java"));
        assertTrue(response.getMatchingSkills().contains("Spring Boot"));
        assertTrue(response.getMissingSkills().contains("Docker"));
        assertFalse(response.getRecommendations().isEmpty());
    }

    @Test
    void testComputeSkillGap() {
        SkillGapResponse response = heuristicEngine.computeSkillGap("React, JavaScript", "Looking for React, TypeScript, Docker");

        assertNotNull(response);
        assertTrue(response.getMissingSkills().contains("TypeScript"));
        assertFalse(response.getLearningRoadmap().isEmpty());
        assertNotNull(response.getRecommendedResources());
    }

    @Test
    void testOptimizeBullet() {
        BulletOptimizeRequest request = new BulletOptimizeRequest("Responsible for building customer REST APIs.", "Spring Boot developer");
        BulletOptimizeResponse response = heuristicEngine.optimizeBullet(request);

        assertNotNull(response);
        assertNotNull(response.getOptimized());
        assertNotNull(response.getExplanation());
        assertNotEquals(request.getBulletPoint(), response.getOptimized());
    }
}
