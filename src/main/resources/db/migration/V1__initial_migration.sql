-- Crear tabla de usuarios
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       name VARCHAR(100) NOT NULL,
                       lastname VARCHAR(100) NOT NULL,
                       second_lastname VARCHAR(100),
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       fcm_token VARCHAR(255),
                       phone_number VARCHAR(20) UNIQUE,
                       active BOOLEAN NOT NULL DEFAULT true,
                       verified BOOLEAN NOT NULL DEFAULT false,
                       role VARCHAR(20) NOT NULL DEFAULT 'CITIZEN',
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para mejorar performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_phone_number ON users(phone_number);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_active ON users(active);

-- Comentarios para documentación
COMMENT ON TABLE users IS 'Tabla de usuarios del sistema de seguridad ciudadana';
COMMENT ON COLUMN users.role IS 'Roles: CITIZEN, MODERATOR, ADMIN';
COMMENT ON COLUMN users.active IS 'Estado activo/inactivo del usuario';
COMMENT ON COLUMN users.verified IS 'Usuario verificado por email o autoridades';
