package org.devquality.safetyauthservice.web.controllers.advice;

import lombok.extern.slf4j.Slf4j;
import org.devquality.safetyauthservice.web.dtos.responses.BaseResponse;
import org.devquality.safetyauthservice.web.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ApplicationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("🔥 Validation error: {}", errors);

        BaseResponse response = BaseResponse.builder()
                .success(false)
                .message("Validation failed")
                .data(errors)
                .HttpStatus(HttpStatus.BAD_REQUEST)
                .build();

        return response.buildResponseEntity();
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<BaseResponse> handleValidationException(ValidationException ex) {
        log.warn("🔥 Custom validation error: {}", ex.getErrors());

        BaseResponse response = BaseResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .data(ex.getErrors())
                .HttpStatus(ex.getHttpStatus())
                .build();

        return response.buildResponseEntity();
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<BaseResponse> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        log.warn("🔐 Invalid credentials: {}", ex.getMessage());

        BaseResponse response = BaseResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .HttpStatus(ex.getHttpStatus())
                .build();

        return response.buildResponseEntity();
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<BaseResponse> handleConflictException(ConflictException ex) {
        log.warn("⚠️ Conflict: {}", ex.getMessage());

        BaseResponse response = BaseResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .HttpStatus(ex.getHttpStatus())
                .build();

        return response.buildResponseEntity();
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BaseResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("🔍 Resource not found: {}", ex.getMessage());

        BaseResponse response = BaseResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .HttpStatus(ex.getHttpStatus())
                .build();

        return response.buildResponseEntity();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("🚫 Access denied: {}", ex.getMessage());

        BaseResponse response = BaseResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .HttpStatus(ex.getHttpStatus())
                .build();

        return response.buildResponseEntity();
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<BaseResponse> handleTokenExpiredException(TokenExpiredException ex) {
        log.warn("⏰ Token expired: {}", ex.getMessage());

        BaseResponse response = BaseResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .HttpStatus(ex.getHttpStatus())
                .build();

        return response.buildResponseEntity();
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<BaseResponse> handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("🔐 Bad credentials: {}", ex.getMessage());

        BaseResponse response = BaseResponse.builder()
                .success(false)
                .message("Invalid credentials")
                .data(null)
                .HttpStatus(HttpStatus.UNAUTHORIZED)
                .build();

        return response.buildResponseEntity();
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<BaseResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        log.warn("👤 User not found: {}", ex.getMessage());

        BaseResponse response = BaseResponse.builder()
                .success(false)
                .message("User not found")
                .data(null)
                .HttpStatus(HttpStatus.NOT_FOUND)
                .build();

        return response.buildResponseEntity();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("📝 Illegal argument: {}", ex.getMessage());

        BaseResponse response = BaseResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .HttpStatus(HttpStatus.BAD_REQUEST)
                .build();

        return response.buildResponseEntity();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse> handleRuntimeException(RuntimeException ex) {
        log.error("💥 Runtime exception: ", ex);

        BaseResponse response = BaseResponse.builder()
                .success(false)
                .message("An unexpected error occurred")
                .data(null)
                .HttpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();

        return response.buildResponseEntity();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleGenericException(Exception ex) {
        log.error("💥 Generic exception: ", ex);

        BaseResponse response = BaseResponse.builder()
                .success(false)
                .message("An internal server error occurred")
                .data(null)
                .HttpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();

        return response.buildResponseEntity();
    }
}