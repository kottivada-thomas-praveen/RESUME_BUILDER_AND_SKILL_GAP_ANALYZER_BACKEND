package com.thomas.rgsga;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomas.rgsga.dto.LoginRequest;
import com.thomas.rgsga.dto.SignupRequest;
import com.thomas.rgsga.entity.User;
import com.thomas.rgsga.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.thomas.rgsga.repository.UserProfileRepository userProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String testEmail = "test.senior.audit@example.com";

    @BeforeEach
    void setUp() {
        userRepository.findByEmailIgnoreCase(testEmail).ifPresent(user -> {
            userProfileRepository.findByUserId(user.getId()).ifPresent(userProfileRepository::delete);
            userRepository.delete(user);
        });
    }

    @Test
    @DisplayName("1. User registration succeeds and hashes password with BCrypt")
    void testSignupSuccessAndBCryptHashing() throws Exception {
        SignupRequest signupRequest = SignupRequest.builder()
                .name("Senior Tester")
                .email(testEmail)
                .password("SecureP@ssw0rd123")
                .build();

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.email").value(testEmail));

        // Verify user persisted and password hashed (not plaintext)
        User persistedUser = userRepository.findByEmailIgnoreCase(testEmail).orElseThrow();
        assertThat(persistedUser.getPassword()).isNotEqualTo("SecureP@ssw0rd123");
        assertThat(passwordEncoder.matches("SecureP@ssw0rd123", persistedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("2. Duplicate email registration is prevented with error")
    void testDuplicateRegistrationFails() throws Exception {
        SignupRequest signupRequest = SignupRequest.builder()
                .name("First User")
                .email(testEmail)
                .password("SecureP@ssw0rd123")
                .build();

        // Initial signup
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isCreated());

        // Duplicate signup with same email returns 409 Conflict
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("3. Registration fails with invalid email or weak short password")
    void testRegistrationValidationErrors() throws Exception {
        SignupRequest invalidRequest = SignupRequest.builder()
                .name("A") // Too short
                .email("not-an-email")
                .password("123") // Less than 6 characters
                .build();

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("4. Login succeeds with valid credentials and returns JWT token")
    void testLoginSuccess() throws Exception {
        // Register user
        SignupRequest signupRequest = SignupRequest.builder()
                .name("Login Test User")
                .email(testEmail)
                .password("ValidPass123!")
                .build();

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isCreated());

        // Login
        LoginRequest loginRequest = LoginRequest.builder()
                .email(testEmail)
                .password("ValidPass123!")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.email").value(testEmail));
    }

    @Test
    @DisplayName("5. Login fails with incorrect password returning 401 Unauthorized")
    void testLoginBadCredentials() throws Exception {
        SignupRequest signupRequest = SignupRequest.builder()
                .name("Wrong Pass User")
                .email(testEmail)
                .password("RealPass123")
                .build();

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isCreated());

        LoginRequest badRequest = LoginRequest.builder()
                .email(testEmail)
                .password("WrongPassword999")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("6. Protected /api/auth/me requires valid JWT token")
    void testGetCurrentUserWithAndWithoutToken() throws Exception {
        // Without token: 401 Unauthorized
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());

        // Register and get token
        SignupRequest signupRequest = SignupRequest.builder()
                .name("Auth Me User")
                .email(testEmail)
                .password("PassWord123")
                .build();

        MvcResult signupResult = mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseJson = signupResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseJson).path("data").path("token").asText();

        // With valid token: 200 OK
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value(testEmail));
    }
}
