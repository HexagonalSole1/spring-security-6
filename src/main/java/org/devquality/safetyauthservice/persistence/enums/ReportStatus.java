package org.devquality.safetyauthservice.persistence.enums;


public enum ReportStatus {
    PENDING,        // Pendiente de revisión
    VERIFIED,       // Verificado por moderadores
    IN_PROGRESS,    // En proceso de resolución
    RESOLVED,       // Resuelto
    REJECTED,       // Rechazado (falso o spam)
    ARCHIVED        // Archivado
}
