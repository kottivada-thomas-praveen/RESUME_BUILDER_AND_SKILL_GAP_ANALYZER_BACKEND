package com.thomas.rgsga.security;

import com.thomas.rgsga.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // High-performance thread-safe in-memory cache for loaded UserDetails with a 5-minute TTL.
    // Eliminates redundant remote DB round-trips to NeonDB on every single authenticated request.
    private static final long CACHE_TTL_MS = 5 * 60 * 1000L;
    private final ConcurrentHashMap<String, CachedUserDetails> userCache = new ConcurrentHashMap<>();

    private record CachedUserDetails(UserDetails userDetails, long expiresAt) {
        boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }
    }

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (email == null) {
            throw new UsernameNotFoundException("Email cannot be null");
        }
        String key = email.trim().toLowerCase();
        CachedUserDetails cached = userCache.get(key);
        if (cached != null && !cached.isExpired()) {
            return cached.userDetails();
        }

        UserDetails userDetails = userRepository.findByEmailIgnoreCase(key)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        userCache.put(key, new CachedUserDetails(userDetails, System.currentTimeMillis() + CACHE_TTL_MS));
        return userDetails;
    }

    public void evictUser(String email) {
        if (email != null) {
            userCache.remove(email.trim().toLowerCase());
        }
    }

    public void clearCache() {
        userCache.clear();
    }
}
