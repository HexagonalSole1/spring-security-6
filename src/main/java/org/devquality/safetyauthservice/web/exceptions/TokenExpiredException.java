package org.devquality.safetyauthservice.web.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TokenExpiredException extends RuntimeException {
    private final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

    public TokenExpiredException() {
        super("Token expired");
    }

    public TokenExpiredException(String message) {
        super(message);
    }
}
