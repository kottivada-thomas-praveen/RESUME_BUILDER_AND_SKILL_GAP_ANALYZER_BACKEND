package com.thomas.RG_SGA_.service;

import com.thomas.RG_SGA_.dto.*;
import com.thomas.RG_SGA_.entity.Role;
import com.thomas.RG_SGA_.entity.User;
import com.thomas.RG_SGA_.repository.UserRepository;
import com.thomas.RG_SGA_.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @org.springframework.beans.factory.annotation.Value("${github.client.id}")
    private String githubClientId;

    @org.springframework.beans.factory.annotation.Value("${github.client.secret}")
    private String githubClientSecret;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // Default role for registrations
                .refreshToken(UUID.randomUUID().toString())
                .emailVerified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);
        String jwtToken = jwtService.generateToken(savedUser);

        return AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(savedUser.getRefreshToken())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

        // Ensure user has a refresh token
        if (user.getRefreshToken() == null || user.getRefreshToken().isBlank()) {
            user.setRefreshToken(UUID.randomUUID().toString());
            user = userRepository.save(user);
        }

        String jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(user.getRefreshToken())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        User user = userRepository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        // Generate a new JWT token
        String jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(user.getRefreshToken())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1)); // 1 hour expiry
        userRepository.save(user);

        // In a real production environment, we would trigger an email.
        // For local development, we print to standard error / logs for easy testing!
        System.out.println("========== PASSWORD RESET REQUEST ==========");
        System.out.println("User: " + user.getEmail());
        System.out.println("Token: " + token);
        System.out.println("Link: http://localhost:8080/api/auth/reset-password?token=" + token);
        System.out.println("============================================");
    }

    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    public void verifyEmail(String token) {
        User user = userRepository.findByResetToken(token) // We can reuse resetToken as verificationToken
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));

        user.setEmailVerified(true);
        user.setResetToken(null);
        userRepository.save(user);
    }

    public AuthResponse googleLogin(GoogleLoginRequest request) {
        // Find existing user or register a new one automatically
        User user = userRepository.findByEmail(request.getEmail())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .name(request.getName() != null ? request.getName() : "Google User")
                            .email(request.getEmail())
                            .password(passwordEncoder.encode(UUID.randomUUID().toString())) // Random password
                            .role(Role.USER)
                            .oauthProvider("GOOGLE")
                            .oauthId(request.getIdToken())
                            .emailVerified(true)
                            .refreshToken(UUID.randomUUID().toString())
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    return userRepository.save(newUser);
                });

        String jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(user.getRefreshToken())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse githubLogin(GithubLoginRequest request) {
        org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();

        // 1. Exchange authorization code for GitHub access token
        String tokenUrl = "https://github.com/login/oauth/access_token";
        java.util.Map<String, String> tokenParams = new java.util.HashMap<>();
        tokenParams.put("client_id", githubClientId);
        tokenParams.put("client_secret", githubClientSecret);
        tokenParams.put("code", request.getCode());

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(org.springframework.http.MediaType.APPLICATION_JSON));

        org.springframework.http.HttpEntity<java.util.Map<String, String>> entity = new org.springframework.http.HttpEntity<>(tokenParams, headers);

        try {
            org.springframework.http.ResponseEntity<java.util.Map> tokenResponse = restTemplate.postForEntity(tokenUrl, entity, java.util.Map.class);
            java.util.Map<String, Object> tokenResponseBody = tokenResponse.getBody();
            if (tokenResponseBody == null || !tokenResponseBody.containsKey("access_token")) {
                throw new IllegalArgumentException("Failed to retrieve access token from GitHub: " + tokenResponseBody);
            }

            String accessToken = (String) tokenResponseBody.get("access_token");

            // 2. Fetch User Profile
            String profileUrl = "https://api.github.com/user";
            org.springframework.http.HttpHeaders profileHeaders = new org.springframework.http.HttpHeaders();
            profileHeaders.setBearerAuth(accessToken);
            profileHeaders.setAccept(java.util.List.of(org.springframework.http.MediaType.APPLICATION_JSON));
            profileHeaders.set("User-Agent", "Careercraft-AI");

            org.springframework.http.HttpEntity<Void> profileRequest = new org.springframework.http.HttpEntity<>(profileHeaders);
            org.springframework.http.ResponseEntity<java.util.Map> profileResponse = restTemplate.exchange(profileUrl, org.springframework.http.HttpMethod.GET, profileRequest, java.util.Map.class);
            java.util.Map<String, Object> profileBody = profileResponse.getBody();

            if (profileBody == null) {
                throw new IllegalArgumentException("Failed to retrieve GitHub profile");
            }

            String githubId = String.valueOf(profileBody.get("id"));
            String name = (String) profileBody.get("name");
            if (name == null || name.isBlank()) {
                name = (String) profileBody.get("login");
            }
            if (name == null) {
                name = "GitHub User";
            }

            String email = (String) profileBody.get("email");

            // 3. Fetch private emails if profile email is null
            if (email == null || email.isBlank()) {
                String emailUrl = "https://api.github.com/user/emails";
                try {
                    org.springframework.http.ResponseEntity<java.util.List> emailsResponse = restTemplate.exchange(emailUrl, org.springframework.http.HttpMethod.GET, profileRequest, java.util.List.class);
                    java.util.List<java.util.Map<String, Object>> emailsList = emailsResponse.getBody();
                    if (emailsList != null) {
                        for (java.util.Map<String, Object> emailObj : emailsList) {
                            Boolean isPrimary = (Boolean) emailObj.get("primary");
                            Boolean isVerified = (Boolean) emailObj.get("verified");
                            if (isPrimary != null && isPrimary) {
                                email = (String) emailObj.get("email");
                                break;
                            }
                        }
                        if (email == null && !emailsList.isEmpty()) {
                            email = (String) emailsList.get(0).get("email");
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Failed to fetch private GitHub emails: " + ex.getMessage());
                }
            }

            if (email == null || email.isBlank()) {
                email = githubId + "@github.noreply.com"; // Fallback email
            }

            final String finalEmail = email;
            final String finalName = name;

            User user = userRepository.findByEmail(finalEmail)
                    .orElseGet(() -> {
                        User newUser = User.builder()
                                .name(finalName)
                                .email(finalEmail)
                                .password(passwordEncoder.encode(UUID.randomUUID().toString())) // Random password
                                .role(Role.USER)
                                .oauthProvider("GITHUB")
                                .oauthId(githubId)
                                .emailVerified(true)
                                .refreshToken(UUID.randomUUID().toString())
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
                        return userRepository.save(newUser);
                    });

            // Ensure oauth fields are populated if they signed up previously via normal email but are now logging in with GitHub
            if (user.getOauthProvider() == null || user.getOauthProvider().isBlank()) {
                user.setOauthProvider("GITHUB");
                user.setOauthId(githubId);
                user.setEmailVerified(true);
                user = userRepository.save(user);
            }

            String jwtToken = jwtService.generateToken(user);

            return AuthResponse.builder()
                    .token(jwtToken)
                    .refreshToken(user.getRefreshToken())
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .build();

        } catch (org.springframework.web.client.RestClientException e) {
            throw new IllegalArgumentException("GitHub authentication failed: " + e.getMessage(), e);
        }
    }
}

