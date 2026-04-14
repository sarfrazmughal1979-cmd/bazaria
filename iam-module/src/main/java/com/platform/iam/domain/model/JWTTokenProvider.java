package com.platform.iam.domain.model;

import com.platform.core.security.UserContext;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class JWTTokenProvider {

    private final SecretKey key;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JWTTokenProvider(
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
                .claim("roles", userContext.getRoles())
                .claim("permissions", userContext.getPermissions())
                .claim("vendorId", userContext.getVendorId() != null ?
                        userContext.getVendorId().toString() : null)
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

        return UserContext.builder()
                .userId(UUID.fromString(claims.getSubject()))
                .email(claims.get("email", String.class))
                .fullName(claims.get("name", String.class))
                .roles(new HashSet<>((List<String>) claims.get("roles")))
                .permissions(new HashSet<>((List<String>) claims.get("permissions")))
                .vendorId(vendorIdStr != null ? UUID.fromString(vendorIdStr) : null)
                .build();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}