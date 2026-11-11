package com.thomas.rgsga.security;

import com.thomas.rgsga.entity.Role;
import com.thomas.rgsga.entity.User;
import com.thomas.rgsga.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsServiceTest {

    @Test
    @DisplayName("loadUserByUsername should hit DB once and subsequent calls should hit in-memory cache")
    void testCachingBehavior() {
        AtomicInteger dbCallCount = new AtomicInteger(0);
        User sampleUser = User.builder()
                .id(1L)
                .name("Alex Mercer")
                .email("alex@example.com")
                .password("encoded_pass")
                .role(Role.USER)
                .accountEnabled(true)
                .build();

        UserRepository fakeRepo = (UserRepository) Proxy.newProxyInstance(
                UserRepository.class.getClassLoader(),
                new Class<?>[]{UserRepository.class},
                (proxy, method, args) -> {
                    if ("findByEmailIgnoreCase".equals(method.getName())) {
                        dbCallCount.incrementAndGet();
                        String email = (String) args[0];
                        if ("alex@example.com".equalsIgnoreCase(email)) {
                            return Optional.of(sampleUser);
                        }
                        return Optional.empty();
                    }
                    return null;
                }
        );

        CustomUserDetailsService service = new CustomUserDetailsService(fakeRepo);

        // First call: Should query database (dbCallCount == 1)
        UserDetails firstResult = service.loadUserByUsername("alex@example.com");
        assertNotNull(firstResult);
        assertEquals("alex@example.com", firstResult.getUsername());
        assertEquals(1, dbCallCount.get(), "First call should invoke database");

        // Second call: Should return from in-memory cache with 0 DB queries
        UserDetails secondResult = service.loadUserByUsername("alex@example.com");
        assertNotNull(secondResult);
        assertEquals(1, dbCallCount.get(), "Second call should be served from memory cache without hitting DB");

        // Third call with different casing: Should normalize and still hit cache
        UserDetails thirdResult = service.loadUserByUsername("ALEX@EXAMPLE.COM");
        assertNotNull(thirdResult);
        assertEquals(1, dbCallCount.get(), "Case-insensitive lookup should hit cache");

        // Fourth call after eviction: Should query database again (dbCallCount == 2)
        service.evictUser("alex@example.com");
        UserDetails fourthResult = service.loadUserByUsername("alex@example.com");
        assertNotNull(fourthResult);
        assertEquals(2, dbCallCount.get(), "Call after evictUser must re-query database");
    }
}
