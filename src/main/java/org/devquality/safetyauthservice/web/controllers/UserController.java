package org.devquality.safetyauthservice.web.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devquality.safetyauthservice.persistence.entities.User;
import org.devquality.safetyauthservice.persistence.repositories.IUserRepository;
import org.devquality.safetyauthservice.web.dtos.responses.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final IUserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<BaseResponse> getCurrentUserProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                BaseResponse response = BaseResponse.builder()
                        .success(false)
                        .message("User not found")
                        .data(null)
                        .HttpStatus(HttpStatus.NOT_FOUND)
                        .build();
                return response.buildResponseEntity();
            }

            User user = userOpt.get();
            UserProfileDto profileDto = mapToProfileDto(user);

            BaseResponse response = BaseResponse.builder()
                    .success(true)
                    .message("User profile retrieved successfully")
                    .data(profileDto)
                    .HttpStatus(HttpStatus.OK)
                    .build();

            return response.buildResponseEntity();

        } catch (Exception e) {
            log.error("Error retrieving user profile", e);

            BaseResponse response = BaseResponse.builder()
                    .success(false)
                    .message("An error occurred while retrieving user profile")
                    .data(null)
                    .HttpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();

            return response.buildResponseEntity();
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            List<UserProfileDto> userDtos = users.stream()
                    .map(this::mapToProfileDto)
                    .toList();

            BaseResponse response = BaseResponse.builder()
                    .success(true)
                    .message("Users retrieved successfully")
                    .data(userDtos)
                    .HttpStatus(HttpStatus.OK)
                    .build();

            return response.buildResponseEntity();

        } catch (Exception e) {
            log.error("Error retrieving users", e);

            BaseResponse response = BaseResponse.builder()
                    .success(false)
                    .message("An error occurred while retrieving users")
                    .data(null)
                    .HttpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();

            return response.buildResponseEntity();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<BaseResponse> getUserById(@PathVariable Long id) {
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isEmpty()) {
                BaseResponse response = BaseResponse.builder()
                        .success(false)
                        .message("User not found")
                        .data(null)
                        .HttpStatus(HttpStatus.NOT_FOUND)
                        .build();
                return response.buildResponseEntity();
            }

            User user = userOpt.get();
            UserProfileDto profileDto = mapToProfileDto(user);

            BaseResponse response = BaseResponse.builder()
                    .success(true)
                    .message("User retrieved successfully")
                    .data(profileDto)
                    .HttpStatus(HttpStatus.OK)
                    .build();

            return response.buildResponseEntity();

        } catch (Exception e) {
            log.error("Error retrieving user with id: {}", id, e);

            BaseResponse response = BaseResponse.builder()
                    .success(false)
                    .message("An error occurred while retrieving user")
                    .data(null)
                    .HttpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();

            return response.buildResponseEntity();
        }
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse> activateUser(@PathVariable Long id) {
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isEmpty()) {
                BaseResponse response = BaseResponse.builder()
                        .success(false)
                        .message("User not found")
                        .data(null)
                        .HttpStatus(HttpStatus.NOT_FOUND)
                        .build();
                return response.buildResponseEntity();
            }

            User user = userOpt.get();
            user.setActive(true);
            userRepository.save(user);

            BaseResponse response = BaseResponse.builder()
                    .success(true)
                    .message("User activated successfully")
                    .data(mapToProfileDto(user))
                    .HttpStatus(HttpStatus.OK)
                    .build();

            return response.buildResponseEntity();

        } catch (Exception e) {
            log.error("Error activating user with id: {}", id, e);

            BaseResponse response = BaseResponse.builder()
                    .success(false)
                    .message("An error occurred while activating user")
                    .data(null)
                    .HttpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();

            return response.buildResponseEntity();
        }
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse> deactivateUser(@PathVariable Long id) {
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isEmpty()) {
                BaseResponse response = BaseResponse.builder()
                        .success(false)
                        .message("User not found")
                        .data(null)
                        .HttpStatus(HttpStatus.NOT_FOUND)
                        .build();
                return response.buildResponseEntity();
            }

            User user = userOpt.get();
            user.setActive(false);
            userRepository.save(user);

            BaseResponse response = BaseResponse.builder()
                    .success(true)
                    .message("User deactivated successfully")
                    .data(mapToProfileDto(user))
                    .HttpStatus(HttpStatus.OK)
                    .build();

            return response.buildResponseEntity();

        } catch (Exception e) {
            log.error("Error deactivating user with id: {}", id, e);

            BaseResponse response = BaseResponse.builder()
                    .success(false)
                    .message("An error occurred while deactivating user")
                    .data(null)
                    .HttpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();

            return response.buildResponseEntity();
        }
    }

    private UserProfileDto mapToProfileDto(User user) {
        return UserProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .lastname(user.getLastname())
                .secondLastname(user.getSecondLastname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .active(user.getActive())
                .verified(user.getVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    // DTO interno para el perfil de usuario
    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class UserProfileDto {
        private Long id;
        private String username;
        private String name;
        private String lastname;
        private String secondLastname;
        private String email;
        private String phoneNumber;
        private org.devquality.safetyauthservice.persistence.enums.UserRole role;
        private Boolean active;
        private Boolean verified;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime updatedAt;
    }
}