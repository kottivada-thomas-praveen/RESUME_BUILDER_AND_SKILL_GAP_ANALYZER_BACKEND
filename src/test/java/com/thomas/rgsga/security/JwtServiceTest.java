package com.thomas.rgsga.security;

import com.thomas.rgsga.entity.Role;
import com.thomas.rgsga.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 900000L);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 604800000L);
    }

    @Test
    void testTokenLifecycle() {
        User user = User.builder()
                .id(1L)
                .name("Alex Mercer")
                .email("alex@example.com")
                .role(Role.USER)
                .build();

        String token = jwtService.generateToken(user);
        assertNotNull(token);

        String username = jwtService.extractUsername(token);
        assertEquals("alex@example.com", username);

        Long userId = jwtService.extractUserId(token);
        assertEquals(1L, userId);

        String role = jwtService.extractRole(token);
        assertEquals("USER", role);

        assertTrue(jwtService.isTokenValid(token, user));
        assertFalse(jwtService.isTokenExpired(token));
    }
}
