package org.devquality.safetyauthservice.services;

import org.devquality.safetyauthservice.persistence.entities.User;
import org.devquality.safetyauthservice.web.dtos.requests.CreateUserRequest;
import org.devquality.safetyauthservice.web.dtos.responses.BaseResponse;

import java.util.Optional;

public interface IUserService {
    BaseResponse create(CreateUserRequest request);
    BaseResponse getUserInfo(String email);
    User getByEmail(String email);
    Optional<User> getOptionalUserByEmail(String email);
    User getByUsername(String username);
    Optional<User> getOptionalUserByUsername(String username);
}

