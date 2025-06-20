package org.devquality.safetyauthservice.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "zones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    // Límites de la zona (polígono)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String boundaries; // JSON con coordenadas del polígono

    // Centro de la zona
    @Column(nullable = false)
    private Double centerLatitude;

    @Column(nullable = false)
    private Double centerLongitude;

    // Contadores para el mapa de calor
    @Column(nullable = false)
    private Integer totalReports = 0;

    @Column(nullable = false)
    private Integer activeReports = 0;

    @Column(nullable = false)
    private Integer resolvedReports = 0;

    // Nivel de peligro calculado (0-100)
    @Column(nullable = false)
    private Integer dangerLevel = 0;

    // Color actual para el mapa de calor
    @Column(nullable = false, length = 7)
    private String heatmapColor = "#00FF00"; // Verde por defecto

    @UpdateTimestamp
    private LocalDateTime lastCalculated;

    @OneToMany(mappedBy = "zone")
    private List<CrimeReport> reports = new ArrayList<>();

    // Método para actualizar estadísticas
    @PreUpdate
    @PrePersist
    public void updateStatistics() {
        // Calcular nivel de peligro basado en reportes
        if (totalReports == 0) {
            dangerLevel = 0;
            heatmapColor = "#00FF00"; // Verde - Seguro
        } else {
            double activeRatio = (double) activeReports / totalReports;
            dangerLevel = (int) (activeRatio * 100);

            // Asignar color según nivel de peligro
            if (dangerLevel < 20) {
                heatmapColor = "#00FF00"; // Verde - Seguro
            } else if (dangerLevel < 40) {
                heatmapColor = "#FFFF00"; // Amarillo - Precaución
            } else if (dangerLevel < 60) {
                heatmapColor = "#FFA500"; // Naranja - Moderado
            } else if (dangerLevel < 80) {
                heatmapColor = "#FF4500"; // Naranja Oscuro - Alto
            } else {
                heatmapColor = "#FF0000"; // Rojo - Muy Peligroso
            }
        }
    }
}