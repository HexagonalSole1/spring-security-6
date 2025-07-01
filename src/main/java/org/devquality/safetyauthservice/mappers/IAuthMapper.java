package org.devquality.safetyauthservice.mappers;

import org.devquality.safetyauthservice.persistence.entities.User;
import org.devquality.safetyauthservice.web.dtos.responses.AuthenticateResponse;
import org.devquality.safetyauthservice.web.dtos.responses.RefreshTokenResponse;
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

    // Método por defecto para crear RefreshTokenResponse
    default RefreshTokenResponse toRefreshTokenResponse() {
        RefreshTokenResponse response = new RefreshTokenResponse();
        response.setTokenType("Bearer");
        return response;
    }
}