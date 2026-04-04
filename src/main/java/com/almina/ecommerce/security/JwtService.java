package com.almina.ecommerce.security;

import com.almina.ecommerce.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final String USER_ID_CLAIM = "userId";
    private static final String EMAIL_CLAIM = "email";
    private static final String ROLE_CLAIM = "role";

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration}")
    private long expiration;

    public String generateToken(User user) {
        return Jwts.builder()
                .claims(Map.of(
                        USER_ID_CLAIM, user.getId(),
                        EMAIL_CLAIM, user.getEmail(),
                        ROLE_CLAIM, user.getRole().name()
                ))
                .subject(String.valueOf(user.getId()))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey())
                .compact();
    }

    public String generateToken(UserDetails userDetails) {
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .findFirst()
                .map(authority -> authority.substring(5))
                .orElse("USER");

        return Jwts.builder()
                .claims(Map.of(
                        USER_ID_CLAIM, userDetails.getUsername(),
                        ROLE_CLAIM, role
                ))
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).get(EMAIL_CLAIM, String.class);
    }

    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        Object userId = claims.get(USER_ID_CLAIM);
        if (userId instanceof Number number) {
            return number.longValue();
        }
        if (userId instanceof String value && !value.isBlank()) {
            return Long.parseLong(value);
        }

        String subject = claims.getSubject();
        if (subject == null || subject.isBlank()) {
            return null;
        }
        return Long.parseLong(subject);
    }

    public boolean isValid(String token, UserDetails userDetails) {
        try {
            Long userId = extractUserId(token);
            String role = extractRole(token);
            boolean roleMatches = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(authority -> authority.equals("ROLE_" + role));
            return userId != null
                    && userDetails.getUsername().equals(String.valueOf(userId))
                    && roleMatches
                    && extractAllClaims(token).getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get(ROLE_CLAIM, String.class);
    }

    public String extractRoleSafely(String token) {
        try {
            return extractRole(token);
        } catch (JwtException | IllegalArgumentException exception) {
            return null;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractAllClaims(token).getExpiration().before(new Date());
        } catch (io.jsonwebtoken.ExpiredJwtException exception) {
            return true;
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    public String extractUsernameSafely(String token) {
        try {
            return extractUsername(token);
        } catch (JwtException | IllegalArgumentException exception) {
            return null;
        }
    }

    public Long extractUserIdSafely(String token) {
        try {
            return extractUserId(token);
        } catch (JwtException | IllegalArgumentException exception) {
            return null;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
