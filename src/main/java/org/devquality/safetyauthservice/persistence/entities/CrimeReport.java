package org.devquality.safetyauthservice.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import org.devquality.safetyauthservice.persistence.enums.ReportStatus;
import org.devquality.safetyauthservice.persistence.enums.SeverityLevel;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "crime_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrimeReport {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    // Ubicación del reporte
    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false, length = 500)
    private String address;

    // Fecha y hora cuando ocurrió el incidente
    @Column(nullable = false)
    private LocalDateTime incidentDateTime;

    // Estado del reporte
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.PENDING;

    // Nivel de severidad
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeverityLevel severity = SeverityLevel.MEDIUM;

    // Es anónimo?
    @Column(nullable = false)
    private Boolean anonymous = false;

    // Número de vistas
    @Column(nullable = false)
    private Integer viewCount = 0;

    // Verificado por autoridades
    @Column(nullable = false)
    private Boolean verified = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CrimeCategory category;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportEvidence> evidences = new ArrayList<>();

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // Para el mapa de calor - zona a la que pertenece
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    private Zone zone;
}