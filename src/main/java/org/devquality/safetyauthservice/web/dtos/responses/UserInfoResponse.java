package org.devquality.safetyauthservice.web.dtos.responses;

import lombok.*;
import org.devquality.safetyauthservice.persistence.enums.UserRole;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoResponse {
    private Long id;
    private String username;
    private String name;
    private String lastname;
    private String secondLastname;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private Boolean verified;
    private Boolean active;
}