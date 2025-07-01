package org.devquality.safetyauthservice.mappers;

import org.devquality.safetyauthservice.persistence.entities.User;
import org.devquality.safetyauthservice.web.dtos.responses.AuthenticateResponse;
import org.devquality.safetyauthservice.web.dtos.responses.RefreshTokenResponse;
import org.devquality.safetyauthservice.web.dtos.responses.UserInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {IUserMapper.class})
public interface IAuthMapper {

    @Mapping(target = "accessToken", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "tokenType", constant = "Bearer")
    @Mapping(target = "expiresIn", ignore = true)
    @Mapping(target = "user", source = "user")
    AuthenticateResponse toAuthenticateResponse(User user);

    @Mapping(target = "accessToken", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "tokenType", constant = "Bearer")
    @Mapping(target = "expiresIn", ignore = true)
    RefreshTokenResponse toRefreshTokenResponse();
}