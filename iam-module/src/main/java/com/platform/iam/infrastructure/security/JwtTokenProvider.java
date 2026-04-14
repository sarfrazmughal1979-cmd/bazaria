package com.platform.iam.infrastructure.security;

import com.platform.core.security.UserContext;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtTokenProvider(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-token-expiration:3600000}") long accessTokenExpiration,
            @Value("${security.jwt.refresh-token-expiration:604800000}") long refreshTokenExpiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String generateAccessToken(UserContext userContext) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(userContext.getUserId().toString())
                .claim("email", userContext.getEmail())
                .claim("name", userContext.getFullName())
                .claim("roles", new ArrayList<>(userContext.getRoles()))
                .claim("permissions", new ArrayList<>(userContext.getPermissions()))
                .claim("vendorId", userContext.getVendorId() != null
                        ? userContext.getVendorId().toString() : null)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(UUID userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    @SuppressWarnings("unchecked")
    public UserContext parseToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String vendorIdStr = claims.get("vendorId", String.class);
        List<String> roles = claims.get("roles", List.class);
        List<String> permissions = claims.get("permissions", List.class);

        return UserContext.builder()
                .userId(UUID.fromString(claims.getSubject()))
                .email(claims.get("email", String.class))
                .fullName(claims.get("name", String.class))
                .roles(roles != null ? new HashSet<>(roles) : Set.of())
                .permissions(permissions != null ? new HashSet<>(permissions) : Set.of())
                .vendorId(vendorIdStr != null ? UUID.fromString(vendorIdStr) : null)
                .build();
    }

    public UUID getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return UUID.fromString(claims.getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token");
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token");
        } catch (SecurityException e) {
            log.warn("Invalid JWT signature");
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty");
        }
        return false;
    }

    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }
}