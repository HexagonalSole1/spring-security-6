package org.devquality.safetyauthservice.web.dtos.responses;


import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
public class BaseResponse {

    private Object data;

    private String message;

    private HttpStatus HttpStatus;

    private Boolean success;

    public ResponseEntity<BaseResponse> buildResponseEntity() {
        return new ResponseEntity<>(this, this.getHttpStatus());
    }

}
