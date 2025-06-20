package org.devquality.safetyauthservice.persistence.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notifications {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private String id;
    private String title;
    private String body;

}
