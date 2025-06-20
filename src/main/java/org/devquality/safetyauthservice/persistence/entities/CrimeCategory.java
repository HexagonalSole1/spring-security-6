package org.devquality.safetyauthservice.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "crime_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrimeCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    // Icono para mostrar en el mapa
    @Column(nullable = false, length = 50)
    private String icon;

    // Color en formato HEX para el mapa
    @Column(nullable = false, length = 7)
    private String color;

    // Orden de prioridad/severidad
    @Column(nullable = false)
    private Integer priority = 0;

    @Column(nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "category")
    private List<CrimeReport> reports = new ArrayList<>();
}