package org.devquality.safetyauthservice.utils;

import org.devquality.safetyauthservice.types.JWTType;
import java.util.Map;

public interface IJWTUtils {
    String generateToken(String email, Map<String, Object> payload, JWTType tokenType);
    String getSubjectFromToken(String token, JWTType tokenType);
    Map<String, Object> getClaimsFromToken(String token, JWTType tokenType);
    Boolean isTokenValid(String token, JWTType tokenType);
}
