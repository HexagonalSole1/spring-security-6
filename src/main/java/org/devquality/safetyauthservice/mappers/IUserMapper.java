package org.devquality.safetyauthservice.mappers;

import org.devquality.safetyauthservice.persistence.entities.User;
import org.devquality.safetyauthservice.web.dtos.requests.CreateUserRequest;
import org.devquality.safetyauthservice.web.dtos.responses.CreateUserResponse;
import org.devquality.safetyauthservice.web.dtos.responses.UserInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IUserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "verified", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(CreateUserRequest request);

    CreateUserResponse toCreateUserResponse(User user);

    UserInfoResponse toUserInfoResponse(User user);
}

