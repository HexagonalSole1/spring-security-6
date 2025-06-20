package org.devquality.safetyauthservice.persistence.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String username;
    private String name;
    private String lastname;
    private String secondLastname;
    private String email;
    private String password;

    private String fcmToken;
    private String phoneNumber;
}
