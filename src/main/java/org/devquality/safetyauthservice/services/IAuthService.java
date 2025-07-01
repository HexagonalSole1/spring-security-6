// IAuthService.java
package org.devquality.safetyauthservice.services;

import org.devquality.safetyauthservice.web.dtos.requests.AuthenticateRequest;
import org.devquality.safetyauthservice.web.dtos.requests.RefreshTokenRequest;
import org.devquality.safetyauthservice.web.dtos.responses.BaseResponse;

public interface IAuthService {
    BaseResponse authenticate(AuthenticateRequest request);
    BaseResponse refreshToken(RefreshTokenRequest request);
}