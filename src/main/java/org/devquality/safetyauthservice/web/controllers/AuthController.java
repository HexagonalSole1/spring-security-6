package org.devquality.safetyauthservice.web.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devquality.safetyauthservice.services.IAuthService;
import org.devquality.safetyauthservice.web.dtos.requests.AuthenticateRequest;
import org.devquality.safetyauthservice.web.dtos.requests.RefreshTokenRequest;
import org.devquality.safetyauthservice.web.dtos.responses.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<BaseResponse> authenticate(@Valid @RequestBody AuthenticateRequest request) {
        log.info("🔐 Authentication request for: {}", request.getEmail());
        BaseResponse baseResponse = authService.authenticate(request);
        return baseResponse.buildResponseEntity();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<BaseResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("🔄 Refresh token request");
        BaseResponse baseResponse = authService.refreshToken(request);
        return baseResponse.buildResponseEntity();
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse> logout() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                log.info("👋 User logout: {}", authentication.getName());
            }

            // Clear security context
            SecurityContextHolder.clearContext();

            BaseResponse response = BaseResponse.builder()
                    .success(true)
                    .message("Logout successful")
                    .data(null)
                    .HttpStatus(HttpStatus.OK)
                    .build();

            return response.buildResponseEntity();

        } catch (Exception e) {
            log.error("❌ Error during logout", e);

            BaseResponse response = BaseResponse.builder()
                    .success(false)
                    .message("An error occurred during logout")
                    .data(null)
                    .HttpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();

            return response.buildResponseEntity();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<BaseResponse> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                BaseResponse response = BaseResponse.builder()
                        .success(false)
                        .message("User not authenticated")
                        .data(null)
                        .HttpStatus(HttpStatus.UNAUTHORIZED)
                        .build();

                return response.buildResponseEntity();
            }

            String username = authentication.getName();

            BaseResponse response = BaseResponse.builder()
                    .success(true)
                    .message("Current user retrieved successfully")
                    .data(Map.of("username", username))
                    .HttpStatus(HttpStatus.OK)
                    .build();

            return response.buildResponseEntity();

        } catch (Exception e) {
            log.error("❌ Error retrieving current user", e);

            BaseResponse response = BaseResponse.builder()
                    .success(false)
                    .message("An unexpected error occurred")
                    .data(null)
                    .HttpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();

            return response.buildResponseEntity();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<BaseResponse> healthCheck() {
        BaseResponse response = BaseResponse.builder()
                .success(true)
                .message("Safety Auth Service is running")
                .data(Map.of(
                        "status", "UP",
                        "service", "safety-auth-service",
                        "timestamp", java.time.LocalDateTime.now()
                ))
                .HttpStatus(HttpStatus.OK)
                .build();

        return response.buildResponseEntity();
    }
}
