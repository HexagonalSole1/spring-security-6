package org.devquality.safetyauthservice.web.dtos.responses;

import lombok.Getter;
import lombok.Setter;
import org.devquality.safetyauthservice.persistence.enums.UserRole;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateUserResponse {
    private Long id;
    private String username;
    private String name;
    private String lastname;
    private String secondLastname;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private Boolean active;
    private Boolean verified;
    private LocalDateTime createdAt;
}
