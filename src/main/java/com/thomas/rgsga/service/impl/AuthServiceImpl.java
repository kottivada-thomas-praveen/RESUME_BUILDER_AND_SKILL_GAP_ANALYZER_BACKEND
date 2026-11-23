package com.thomas.rgsga.service.impl;

import com.thomas.rgsga.dto.*;
import com.thomas.rgsga.entity.Role;
import com.thomas.rgsga.entity.User;
import com.thomas.rgsga.entity.UserProfile;
import com.thomas.rgsga.exception.BadRequestException;
import com.thomas.rgsga.exception.EmailAlreadyExistsException;
import com.thomas.rgsga.exception.ResourceNotFoundException;
import com.thomas.rgsga.exception.UnauthorizedException;
import com.thomas.rgsga.mapper.UserMapper;
import com.thomas.rgsga.repository.UserProfileRepository;
import com.thomas.rgsga.repository.UserRepository;
import com.thomas.rgsga.security.JwtService;
import com.thomas.rgsga.service.AnalyticsService;
import com.thomas.rgsga.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final AnalyticsService analyticsService;

    public AuthServiceImpl(UserRepository userRepository,
                           UserProfileRepository userProfileRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           AuthenticationManager authenticationManager,
                           UserMapper userMapper,
                           AnalyticsService analyticsService) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
        this.analyticsService = analyticsService;
    }

    private HttpServletRequest getCurrentHttpRequest() {
        try {
            var attributes = RequestContextHolder.getRequestAttributes();
            if (attributes instanceof ServletRequestAttributes sra) {
                return sra.getRequest();
            }
        } catch (Exception ignored) {}
        return null;
    }

    @Override
    public AuthResponse register(SignupRequest request) {
        return signup(request);
    }

    @Override
    public AuthResponse signup(SignupRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyExistsException("An account with email " + email + " already exists");
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .accountEnabled(true)
                .emailVerified(false)
                .build();

        user = userRepository.save(user);

        UserProfile profile = UserProfile.builder()
                .user(user)
                .bio("Professional software developer.")
                .build();
        userProfileRepository.save(profile);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        analyticsService.logAuthEvent(email, "REGISTER", "SUCCESS", null, getCurrentHttpRequest(), user);
        analyticsService.logUserActivity(user, "USER_REGISTERED", "USER", user.getId(), "New account registered: " + user.getName(), getCurrentHttpRequest());

        log.info("New user registered successfully: {}", email);
        return userMapper.toAuthResponse(user, accessToken, refreshToken);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );

            User user = (User) authentication.getPrincipal();

            if (!user.isEnabled()) {
                analyticsService.logAuthEvent(email, "LOGIN_FAILURE", "FAILURE", "Account is disabled", getCurrentHttpRequest(), user);
                throw new UnauthorizedException("Account is disabled. Please contact support.");
            }

            user.setLastLoginAt(LocalDateTime.now());
            user.setLoginCount((user.getLoginCount() != null ? user.getLoginCount() : 0) + 1);
            user.setFailedLoginCount(0);

            String accessToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            user.setRefreshToken(refreshToken);
            userRepository.save(user);

            analyticsService.logAuthEvent(email, "LOGIN_SUCCESS", "SUCCESS", null, getCurrentHttpRequest(), user);
            analyticsService.logUserActivity(user, "LOGIN", "AUTH", user.getId(), "User logged in", getCurrentHttpRequest());

            log.info("User logged in successfully: {}", email);
            return userMapper.toAuthResponse(user, accessToken, refreshToken);
        } catch (Exception e) {
            userRepository.findByEmailIgnoreCase(email).ifPresent(u -> {
                u.setFailedLoginCount((u.getFailedLoginCount() != null ? u.getFailedLoginCount() : 0) + 1);
                userRepository.save(u);
            });
            analyticsService.logAuthEvent(email, "LOGIN_FAILURE", "FAILURE", e.getMessage(), getCurrentHttpRequest(), null);
            throw e;
        }
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String token = request.getRefreshToken();
        User user = userRepository.findByRefreshToken(token)
                .orElseThrow(() -> new UnauthorizedException("Invalid or expired refresh token"));

        if (jwtService.isTokenExpired(token)) {
            throw new UnauthorizedException("Refresh token has expired. Please log in again.");
        }

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return userMapper.toAuthResponse(user, newAccessToken, newRefreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        String newEmail = request.getEmail().trim().toLowerCase();
        if (!newEmail.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmailIgnoreCase(newEmail)) {
            throw new EmailAlreadyExistsException("Email is already taken by another account");
        }

        user.setName(request.getFullName().trim());
        user.setEmail(newEmail);
        user = userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @Override
    public void logout(String email) {
        userRepository.findByEmailIgnoreCase(email).ifPresent(user -> {
            user.setRefreshToken(null);
            userRepository.save(user);
            analyticsService.logAuthEvent(email, "LOGOUT", "SUCCESS", null, getCurrentHttpRequest(), user);
            log.info("User logged out: {}", email);
        });
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        userRepository.findByEmailIgnoreCase(email).ifPresent(user -> {
            String token = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            user.setResetToken(token);
            user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
            userRepository.save(user);
            analyticsService.logAuthEvent(email, "PASSWORD_RESET_REQUEST", "SUCCESS", null, getCurrentHttpRequest(), user);
            log.info("Generated password reset token [{}] for email [{}]", token, email);
        });
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.getToken().trim())
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            analyticsService.logAuthEvent(user.getEmail(), "PASSWORD_RESET_FAILURE", "FAILURE", "Token expired", getCurrentHttpRequest(), user);
            throw new BadRequestException("Reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        analyticsService.logAuthEvent(user.getEmail(), "PASSWORD_RESET_SUCCESS", "SUCCESS", null, getCurrentHttpRequest(), user);
        log.info("Password successfully reset for user: {}", user.getEmail());
    }

    @Override
    public void verifyEmail(String token) {
        // Verification hook
        log.info("Verify email triggered with token: {}", token);
    }

    @Override
    public AuthResponse googleLogin(GoogleLoginRequest request) {
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "google.user@example.com";
        String name = request.getName() != null ? request.getName() : "Google User";

        User user = userRepository.findByEmailIgnoreCase(email).orElseGet(() -> {
            User newUser = User.builder()
                    .name(name)
                    .email(email)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .role(Role.USER)
                    .oauthProvider("google")
                    .oauthId(request.getGoogleId() != null ? request.getGoogleId() : UUID.randomUUID().toString())
                    .emailVerified(true)
                    .accountEnabled(true)
                    .build();
            newUser = userRepository.save(newUser);
            userProfileRepository.save(UserProfile.builder().user(newUser).build());
            return newUser;
        });

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return userMapper.toAuthResponse(user, accessToken, refreshToken);
    }

    @Override
    public AuthResponse githubLogin(GithubLoginRequest request) {
        String mockGithubEmail = "github." + Math.abs(request.getCode().hashCode()) + "@developer.io";
        String mockGithubName = "GitHub Developer";

        User user = userRepository.findByEmailIgnoreCase(mockGithubEmail).orElseGet(() -> {
            User newUser = User.builder()
                    .name(mockGithubName)
                    .email(mockGithubEmail)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .role(Role.USER)
                    .oauthProvider("github")
                    .oauthId(request.getCode())
                    .emailVerified(true)
                    .accountEnabled(true)
                    .build();
            newUser = userRepository.save(newUser);
            userProfileRepository.save(UserProfile.builder().user(newUser).build());
            return newUser;
        });

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return userMapper.toAuthResponse(user, accessToken, refreshToken);
    }
}
