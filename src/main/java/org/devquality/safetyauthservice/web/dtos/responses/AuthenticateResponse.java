package org.devquality.safetyauthservice.web.dtos.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticateResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserInfoResponse user;
}
