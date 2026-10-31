package com.thomas.rgsga.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomas.rgsga.config.AppConfig;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DtoValidationAndSerializationTest {

    private ObjectMapper objectMapper;
    private Validator validator;

    @BeforeEach
    void setUp() {
        AppConfig appConfig = new AppConfig();
        this.objectMapper = appConfig.objectMapper();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void testSignupRequest_withFullName() throws Exception {
        String json = """
            {
              "fullName": "Alex Mercer",
              "email": "alex.mercer@email.com",
              "password": "password123"
            }
            """;
        SignupRequest request = objectMapper.readValue(json, SignupRequest.class);
        assertNotNull(request);
        assertEquals("Alex Mercer", request.getName());
        assertEquals("Alex Mercer", request.getFullName());
        assertEquals("alex.mercer@email.com", request.getEmail());
        assertEquals("password123", request.getPassword());

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Should have no validation violations when fullName is sent");
    }

    @Test
    void testSignupRequest_withName() throws Exception {
        String json = """
            {
              "name": "Alex Mercer",
              "email": "alex.mercer@email.com",
              "password": "password123"
            }
            """;
        SignupRequest request = objectMapper.readValue(json, SignupRequest.class);
        assertNotNull(request);
        assertEquals("Alex Mercer", request.getName());
        assertEquals("Alex Mercer", request.getFullName());

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testSignupRequest_invalidFields() throws Exception {
        String json = """
            {
              "name": "",
              "email": "invalid-email",
              "password": "123"
            }
            """;
        SignupRequest request = objectMapper.readValue(json, SignupRequest.class);
        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        // name fails both @NotBlank and @Size(min=2), email fails @Email, password fails @Size(min=6)
        assertEquals(4, violations.size());
    }

    @Test
    void testUpdateProfileRequest_withBothNameAndFullName() throws Exception {
        String jsonWithFullName = """
            {
              "fullName": "Jane Doe",
              "email": "jane@example.com"
            }
            """;
        UpdateProfileRequest req1 = objectMapper.readValue(jsonWithFullName, UpdateProfileRequest.class);
        assertEquals("Jane Doe", req1.getFullName());
        assertEquals("Jane Doe", req1.getName());
        assertTrue(validator.validate(req1).isEmpty());

        String jsonWithName = """
            {
              "name": "Jane Doe",
              "email": "jane@example.com"
            }
            """;
        UpdateProfileRequest req2 = objectMapper.readValue(jsonWithName, UpdateProfileRequest.class);
        assertEquals("Jane Doe", req2.getFullName());
        assertEquals("Jane Doe", req2.getName());
        assertTrue(validator.validate(req2).isEmpty());
    }

    @Test
    void testResetPasswordRequest_withOtpAndResetTokenAliases() throws Exception {
        String jsonOtp = """
            {
              "otp": "123456",
              "password": "newSecurePassword123"
            }
            """;
        ResetPasswordRequest req = objectMapper.readValue(jsonOtp, ResetPasswordRequest.class);
        assertEquals("123456", req.getToken());
        assertEquals("newSecurePassword123", req.getNewPassword());
        assertTrue(validator.validate(req).isEmpty());
    }

    @Test
    void testAtsRequest_withAliases() throws Exception {
        String json = """
            {
              "resume": "Alex Mercer resume text...",
              "jobDesc": "Senior Full Stack Java Engineer"
            }
            """;
        AtsRequest request = objectMapper.readValue(json, AtsRequest.class);
        assertEquals("Alex Mercer resume text...", request.getResumeText());
        assertEquals("Senior Full Stack Java Engineer", request.getJobDescription());
        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void testSkillGapRequest_withAliases() throws Exception {
        String json = """
            {
              "skills": "Java, Spring Boot, React",
              "targetJobDescription": "Looking for Java and Docker"
            }
            """;
        SkillGapRequest request = objectMapper.readValue(json, SkillGapRequest.class);
        assertEquals("Java, Spring Boot, React", request.getUserSkills());
        assertEquals("Looking for Java and Docker", request.getJobDescription());
        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void testBulletOptimizeRequest_withAliases() throws Exception {
        String json = """
            {
              "text": "Wrote backend endpoints in Java",
              "jobDesc": "Experienced backend architect"
            }
            """;
        BulletOptimizeRequest request = objectMapper.readValue(json, BulletOptimizeRequest.class);
        assertEquals("Wrote backend endpoints in Java", request.getBulletPoint());
        assertEquals("Experienced backend architect", request.getJobDescription());
        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void testCoverLetterRequest_withAliases() throws Exception {
        String json = """
            {
              "company": "Acme Inc",
              "role": "Full Stack Engineer",
              "fullName": "Alex Mercer",
              "userSkills": "Java, React"
            }
            """;
        CoverLetterRequest request = objectMapper.readValue(json, CoverLetterRequest.class);
        assertEquals("Acme Inc", request.getCompanyName());
        assertEquals("Full Stack Engineer", request.getTargetRole());
        assertEquals("Alex Mercer", request.getApplicantName());
        assertEquals("Java, React", request.getSkills());
        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void testInterviewRequest_withAliases() throws Exception {
        String json = """
            {
              "role": "Frontend Developer",
              "skills": "React, TypeScript"
            }
            """;
        InterviewRequest request = objectMapper.readValue(json, InterviewRequest.class);
        assertEquals("Frontend Developer", request.getJobTitle());
        assertEquals("React, TypeScript", request.getUserSkills());
        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void testGrammarCheckRequest_withAliases() throws Exception {
        String json = """
            {
              "bulletPoint": "Developed microservices with Spring Cloud."
            }
            """;
        GrammarCheckRequest request = objectMapper.readValue(json, GrammarCheckRequest.class);
        assertEquals("Developed microservices with Spring Cloud.", request.getText());
        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void testResumeDTO_withExtraFrontendFields() throws Exception {
        String json = """
            {
              "fullName": "Alex Mercer",
              "title": "Full Stack Engineer",
              "skills": "Java, React, SQL",
              "projects": [
                {
                  "name": "AI Career Builder",
                  "technologies": "Spring Boot, React",
                  "githubLink": "https://github.com/alex/builder",
                  "liveDemoLink": "https://builder.demo.io",
                  "description": "Interactive career roadmap"
                }
              ]
            }
            """;
        ResumeDTO dto = objectMapper.readValue(json, ResumeDTO.class);
        assertNotNull(dto);
        assertEquals("Alex Mercer", dto.getFullName());
        assertEquals(1, dto.getProjects().size());
        assertEquals("https://github.com/alex/builder", dto.getProjects().get(0).getLink());
    }
}
