package org.devquality.safetyauthservice.services.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devquality.safetyauthservice.mappers.IUserMapper;
import org.devquality.safetyauthservice.persistence.entities.User;
import org.devquality.safetyauthservice.persistence.repositories.IUserRepository;
import org.devquality.safetyauthservice.services.IUserService;
import org.devquality.safetyauthservice.web.dtos.requests.CreateUserRequest;
import org.devquality.safetyauthservice.web.dtos.responses.BaseResponse;
import org.devquality.safetyauthservice.web.dtos.responses.UserInfoResponse;
import org.devquality.safetyauthservice.web.exceptions.ConflictException;
import org.devquality.safetyauthservice.web.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IUserMapper userMapper;

    @Override
    @Transactional
    public BaseResponse create(CreateUserRequest request) {
        log.info("👤 Creating user: {}", request.getUsername());

        try {
            // Validar que las contraseñas coincidan
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                throw new IllegalArgumentException("Passwords do not match");
            }

            // Verificar si el usuario ya existe
            if (userRepository.existsByUsername(request.getUsername())) {
                log.warn("❌ Username already exists: {}", request.getUsername());
                throw new ConflictException("Username already exists");
            }

            if (userRepository.existsByEmail(request.getEmail())) {
                log.warn("❌ Email already exists: {}", request.getEmail());
                throw new ConflictException("Email already exists");
            }

            if (request.getPhoneNumber() != null && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                log.warn("❌ Phone number already exists: {}", request.getPhoneNumber());
                throw new ConflictException("Phone number already exists");
            }

            // Crear usuario
            User user = userMapper.toEntity(request);
            user.setPassword(passwordEncoder.encode(request.getPassword()));

            User savedUser = userRepository.save(user);

            log.info("✅ User created successfully: {}", savedUser.getUsername());

            return BaseResponse.builder()
                    .data(userMapper.toCreateUserResponse(savedUser))
                    .message("User created successfully")
                    .success(true)
                    .HttpStatus(HttpStatus.CREATED)
                    .build();

        } catch (ConflictException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("❌ Unexpected error creating user: {}", request.getUsername(), e);
            throw new RuntimeException("Failed to create user", e);
        }
    }

    @Override
    public BaseResponse getUserInfo(String email) {
        log.debug("📊 Getting user info for: {}", email);

        try {
            User user = getByEmail(email);
            UserInfoResponse userInfo = userMapper.toUserInfoResponse(user);

            return BaseResponse.builder()
                    .data(userInfo)
                    .message("User info retrieved successfully")
                    .success(true)
                    .HttpStatus(HttpStatus.OK)
                    .build();

        } catch (Exception e) {
            log.error("❌ Error getting user info for: {}", email, e);
            throw e;
        }
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(User.class));
    }

    @Override
    public Optional<User> getOptionalUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(User.class));
    }

    @Override
    public Optional<User> getOptionalUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}