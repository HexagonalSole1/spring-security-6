
package org.devquality.safetyauthservice.utils.impls;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.devquality.safetyauthservice.types.JWTType;
import org.devquality.safetyauthservice.utils.IJWTUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class JWTUtilsImpl implements IJWTUtils {

    @Value("${jwt.access-token-secret-key}")
    private String ACCESS_TOKEN_SECRET_KEY;

    @Value("${jwt.access-token-expiration-in-ms}")
    private Long ACCESS_TOKEN_EXPIRATION_TIME;

    @Value("${jwt.refresh-token-secret-key}")
    private String REFRESH_TOKEN_SECRET_KEY;

    @Value("${jwt.refresh-token-expiration-in-ms}")
    private Long REFRESH_TOKEN_EXPIRATION_TIME;

    @PostConstruct
    public void logJwtConfig() {
        log.info("🔑 [SAFETY-AUTH-SERVICE] JWT Config Loaded:");
        log.info("🔑 Access Token Secret Length: {}", ACCESS_TOKEN_SECRET_KEY.length());
        log.info("🔑 Access Token Expiration: {}ms ({}h)", ACCESS_TOKEN_EXPIRATION_TIME, ACCESS_TOKEN_EXPIRATION_TIME / 3600000);
        log.info("🔑 Refresh Token Expiration: {}ms ({}d)", REFRESH_TOKEN_EXPIRATION_TIME, REFRESH_TOKEN_EXPIRATION_TIME / 86400000);
    }

    @Override
    public String generateToken(String email, Map<String, Object> payload, JWTType tokenType) {
        String secretKey = getTokenSecretKey(tokenType);
        Date expirationTime = getTokenExpirationTime(tokenType);

        log.debug("🟡 Generating {} token for: {}", tokenType, email);

        try {
            String token = Jwts.builder()
                    .subject(email)
                    .claims(payload != null ? payload : Map.of())
                    .expiration(expirationTime)
                    .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .compact();

            log.debug("✅ Token generated successfully for: {}", email);
            return token;

        } catch (Exception e) {
            log.error("❌ Error generating token for: {}", email, e);
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    @Override
    public String getSubjectFromToken(String token, JWTType tokenType) {
        String secretKey = getTokenSecretKey(tokenType);

        try {
            return Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            log.error("❌ Error extracting subject from token", e);
            throw new RuntimeException("Failed to extract subject from token", e);
        }
    }

    @Override
    public Map<String, Object> getClaimsFromToken(String token, JWTType tokenType) {
        String secretKey = getTokenSecretKey(tokenType);

        try {
            return Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("❌ Error extracting claims from token", e);
            throw new RuntimeException("Failed to extract claims from token", e);
        }
    }

    @Override
    public Boolean isTokenValid(String token, JWTType tokenType) {
        String secretKey = getTokenSecretKey(tokenType);

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Check if token is expired
            Date expiration = claims.getExpiration();
            boolean isValid = expiration.after(new Date());

            log.debug("Token validation result: {} (expires: {})", isValid, expiration);
            return isValid;

        } catch (Exception e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    private String getTokenSecretKey(JWTType type) {
        return switch (type) {
            case ACCESS_TOKEN -> ACCESS_TOKEN_SECRET_KEY;
            case REFRESH_TOKEN -> REFRESH_TOKEN_SECRET_KEY;
        };
    }

    private Date getTokenExpirationTime(JWTType type) {
        return switch (type) {
            case ACCESS_TOKEN -> new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME);
            case REFRESH_TOKEN -> new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME);
        };
    }

    public Long getAccessTokenExpirationTime() {
        return ACCESS_TOKEN_EXPIRATION_TIME;
    }

    public Long getRefreshTokenExpirationTime() {
        return REFRESH_TOKEN_EXPIRATION_TIME;
    }
}