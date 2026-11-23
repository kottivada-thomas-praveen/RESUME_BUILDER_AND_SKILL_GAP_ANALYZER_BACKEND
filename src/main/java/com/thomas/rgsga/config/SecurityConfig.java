package com.thomas.rgsga.config;

import com.thomas.rgsga.security.JwtAuthenticationEntryPoint;
import com.thomas.rgsga.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint jwtAuthEntryPoint;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          JwtAuthenticationEntryPoint jwtAuthEntryPoint,
                          CorsConfigurationSource corsConfigurationSource) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(jwtAuthEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Permit all CORS preflight OPTIONS requests unconditionally
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        // Public Auth endpoints
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/signup",
                                "/api/auth/login",
                                "/api/auth/refresh",
                                "/api/auth/refresh-token",
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password",
                                "/api/auth/verify-email",
                                "/api/auth/google-login",
                                "/api/auth/github-login"
                        ).permitAll()
                        // Public tools / parsers
                        .requestMatchers(
                                "/api/public/resumes/**",
                                "/api/resumes/parse",
                                "/api/resumes/validate-format",
                                "/api/optimize/bullet",
                                "/api/grammar/check",
                                "/api/ats/analyze",
                                "/api/ats/extract-keywords",
                                "/api/skill-gap/analyze",
                                "/api/interview/prep",
                                "/api/career/recommend",
                                "/api/import/github"
                        ).permitAll()
                        // Health and Swagger documentation
                        .requestMatchers(
                                "/test",
                                "/api/health",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        // Admin restricted endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // All other API endpoints require authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
