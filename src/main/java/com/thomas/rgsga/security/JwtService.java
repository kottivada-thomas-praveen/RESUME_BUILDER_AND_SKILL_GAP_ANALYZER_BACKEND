package com.thomas.rgsga.security;

import com.thomas.rgsga.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key:${JWT_SECRET_KEY:${JWT_SECRET:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}}}")
    private String secretKey;

    @Value("${application.security.jwt.expiration:900000}") // Default 15 mins (900,000 ms)
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-token.expiration:604800000}") // Default 7 days (604,800,000 ms)
    private long refreshExpiration;

    // Cache the signing key — it is derived from a fixed secret and never changes at runtime.
    // Previously getSigningKey() ran 2–3 try/catch blocks + a SHA-256 hash on every JWT operation.
    private volatile SecretKey cachedSigningKey;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User user) {
            claims.put("userId", user.getId());
            claims.put("role", user.getRole().name());
            claims.put("name", user.getName());
        }
        return buildToken(claims, userDetails.getUsername(), jwtExpiration);
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole().name());
        claims.put("name", user.getName());
        return buildToken(claims, user.getEmail(), jwtExpiration);
    }

    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        return buildToken(claims, user.getEmail(), refreshExpiration);
    }

    private String buildToken(Map<String, Object> claims, String subject, long expiration) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiration))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username != null && username.equalsIgnoreCase(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration != null && expiration.before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        if (cachedSigningKey != null) {
            return cachedSigningKey;
        }
        // Derive and cache the signing key. This runs only once per application lifecycle.
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            if (keyBytes.length >= 32) {
                cachedSigningKey = Keys.hmacShaKeyFor(keyBytes);
                return cachedSigningKey;
            }
        } catch (Exception ignored) {
            // Fall back if not pure Base64
        }

        try {
            byte[] hexBytes = Decoders.BASE64URL.decode(secretKey);
            if (hexBytes.length >= 32) {
                cachedSigningKey = Keys.hmacShaKeyFor(hexBytes);
                return cachedSigningKey;
            }
        } catch (Exception ignored) {
            // Fall back
        }

        // Resilient fallback: SHA-256 hash of secret string guarantees exact 32 bytes (256 bits)
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyHash = digest.digest(secretKey.getBytes(StandardCharsets.UTF_8));
            cachedSigningKey = Keys.hmacShaKeyFor(keyHash);
            return cachedSigningKey;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize cryptographic JWT signing key", e);
        }
    }
}
