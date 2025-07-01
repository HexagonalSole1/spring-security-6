package org.devquality.safetyauthservice.services.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devquality.safetyauthservice.mappers.IAuthMapper;
import org.devquality.safetyauthservice.mappers.IUserMapper;
import org.devquality.safetyauthservice.persistence.entities.User;
import org.devquality.safetyauthservice.services.IAuthService;
import org.devquality.safetyauthservice.services.IUserService;
import org.devquality.safetyauthservice.types.JWTType;
import org.devquality.safetyauthservice.utils.IJWTUtils;
import org.devquality.safetyauthservice.web.dtos.requests.AuthenticateRequest;
import org.devquality.safetyauthservice.web.dtos.requests.RefreshTokenRequest;
import org.devquality.safetyauthservice.web.dtos.responses.BaseResponse;
import org.devquality.safetyauthservice.web.dtos.responses.AuthenticateResponse;
import org.devquality.safetyauthservice.web.dtos.responses.RefreshTokenResponse;
import org.devquality.safetyauthservice.web.exceptions.InvalidCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements IAuthService {

    private final IUserService userService;
    private final IJWTUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final IAuthMapper authMapper;
    private final IUserMapper userMapper;

    public AuthServiceImpl(IUserService userService, IJWTUtils jwtUtils, PasswordEncoder passwordEncoder, IAuthMapper authMapper, IUserMapper userMapper) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.authMapper = authMapper;
        this.userMapper = userMapper;
    }

    @Override
    public BaseResponse authenticate(AuthenticateRequest request) {
        log.info("🔐 Authentication attempt for: {}", request.getEmail());

        try {
            // Verificar si el usuario existe
            User user = userService.getOptionalUserByEmail(request.getEmail())
                    .orElseThrow(() -> {
                        log.warn("❌ User not found: {}", request.getEmail());
                        return new InvalidCredentialsException();
                    });

            // Verificar si la contraseña es correcta
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.warn("❌ Invalid password for user: {}", request.getEmail());
                throw new InvalidCredentialsException();
            }

            // Verificar si el usuario está activo
            if (!user.getActive()) {
                log.warn("❌ Inactive user tried to login: {}", request.getEmail());
                throw new InvalidCredentialsException();
            }

            // Crear claims para el token
            Map<String, Object> claims = Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "username", user.getUsername(),
                    "role", user.getRole().name(),
                    "verified", user.getVerified()
            );

            // Generar tokens JWT
            String accessToken = jwtUtils.generateToken(user.getEmail(), claims, JWTType.ACCESS_TOKEN);
            String refreshToken = jwtUtils.generateToken(user.getEmail(), null, JWTType.REFRESH_TOKEN);

            // Crear respuesta
            AuthenticateResponse authResponse = authMapper.toAuthenticateResponse(user);
            authResponse.setAccessToken(accessToken);
            authResponse.setRefreshToken(refreshToken);
            authResponse.setExpiresIn(86400000L); // 24 hours - use constant or configuration
            authResponse.setUser(userMapper.toUserInfoResponse(user));

            log.info("✅ User authenticated successfully: {}", request.getEmail());

            return BaseResponse.builder()
                    .data(authResponse)
                    .message("Successfully authenticated")
                    .success(true)
                    .HttpStatus(HttpStatus.OK)
                    .build();

        } catch (InvalidCredentialsException e) {
            throw e;
        } catch (Exception e) {
            log.error("❌ Unexpected error during authentication for: {}", request.getEmail(), e);
            throw new RuntimeException("Authentication failed", e);
        }
    }

    @Override
    public BaseResponse refreshToken(RefreshTokenRequest request) {
        log.info("🔄 Refresh token attempt");

        try {
            // Validar el refresh token
            Boolean isTokenValid = jwtUtils.isTokenValid(request.getRefreshToken(), JWTType.REFRESH_TOKEN);

            if (!isTokenValid) {
                log.warn("❌ Invalid refresh token");
                throw new InvalidCredentialsException();
            }

            // Extraer información del token
            String email = jwtUtils.getSubjectFromToken(request.getRefreshToken(), JWTType.REFRESH_TOKEN);

            // Verificar que el usuario aún existe y está activo
            User user = userService.getOptionalUserByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("❌ User not found during token refresh: {}", email);
                        return new InvalidCredentialsException();
                    });

            if (!user.getActive()) {
                log.warn("❌ Inactive user tried to refresh token: {}", email);
                throw new InvalidCredentialsException();
            }

            // Crear nuevos claims
            Map<String, Object> claims = Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "username", user.getUsername(),
                    "role", user.getRole().name(),
                    "verified", user.getVerified()
            );

            // Generar nuevos tokens
            String accessToken = jwtUtils.generateToken(email, claims, JWTType.ACCESS_TOKEN);
            String newRefreshToken = jwtUtils.generateToken(email, null, JWTType.REFRESH_TOKEN);

            // Crear respuesta
            RefreshTokenResponse refreshResponse = authMapper.toRefreshTokenResponse();
            refreshResponse.setAccessToken(accessToken);
            refreshResponse.setRefreshToken(newRefreshToken);
            refreshResponse.setExpiresIn(86400000L); // 24 hours - use constant or configuration

            log.info("✅ Token refreshed successfully for: {}", email);

            return BaseResponse.builder()
                    .data(refreshResponse)
                    .message("Successfully refreshed token")
                    .success(true)
                    .HttpStatus(HttpStatus.OK)
                    .build();

        } catch (InvalidCredentialsException e) {
            throw e;
        } catch (Exception e) {
            log.error("❌ Unexpected error during token refresh", e);
            throw new RuntimeException("Token refresh failed", e);
        }
    }
}
