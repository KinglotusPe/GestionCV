-- ============================================================================
-- MIGRACIÓN 004: MEJORAS INTEGRALES DE GESTIONCV
-- Notificaciones, estados de convocatorias y postulaciones, datos académicos y empresariales.
-- ============================================================================

-- 1. Tabla de Notificaciones Internas
CREATE TABLE IF NOT EXISTS notificaciones (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL DEFAULT 'SISTEMA',
    reference_url VARCHAR(255) NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notif_user FOREIGN KEY (user_id) REFERENCES usuarios(user_id) ON DELETE CASCADE,
    INDEX idx_notif_user_read (user_id, is_read, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Campos ampliados para egresados/estudiantes
ALTER TABLE egresados 
    ADD COLUMN IF NOT EXISTS institutional_code VARCHAR(30) NULL AFTER document_number,
    ADD COLUMN IF NOT EXISTS entry_year SMALLINT NULL AFTER graduation_year,
    ADD COLUMN IF NOT EXISTS academic_status ENUM('ESTUDIANTE', 'EGRESADO') NOT NULL DEFAULT 'EGRESADO' AFTER entry_year;

-- 3. Campos ampliados para empresas
ALTER TABLE empresas 
    ADD COLUMN IF NOT EXISTS trade_name VARCHAR(150) NULL AFTER company_name,
    ADD COLUMN IF NOT EXISTS address VARCHAR(200) NULL AFTER phone,
    ADD COLUMN IF NOT EXISTS representative_name VARCHAR(120) NULL AFTER description,
    ADD COLUMN IF NOT EXISTS representative_dni VARCHAR(20) NULL AFTER representative_name,
    ADD COLUMN IF NOT EXISTS representative_position VARCHAR(80) NULL AFTER representative_dni;

-- 4. Ampliación de estados de vacantes
ALTER TABLE vacantes MODIFY COLUMN status ENUM('BORRADOR', 'PUBLICADA', 'PAUSADA', 'CERRADA', 'VENCIDA', 'open', 'closed') NOT NULL DEFAULT 'BORRADOR';
UPDATE vacantes SET status = 'PUBLICADA' WHERE status = 'open';
UPDATE vacantes SET status = 'CERRADA' WHERE status = 'closed';

-- 5. Ampliación de estados de postulaciones
ALTER TABLE postulaciones MODIFY COLUMN status ENUM(
    'POSTULADO', 'EN_REVISION', 'PRESELECCIONADO', 'ENTREVISTA', 'SELECCIONADO', 'NO_SELECCIONADO', 'RETIRADO',
    'pending', 'reviewing', 'accepted', 'rejected', 'withdrawn'
) NOT NULL DEFAULT 'POSTULADO';

UPDATE postulaciones SET status = 'POSTULADO' WHERE status = 'pending';
UPDATE postulaciones SET status = 'EN_REVISION' WHERE status = 'reviewing';
UPDATE postulaciones SET status = 'SELECCIONADO' WHERE status = 'accepted';
UPDATE postulaciones SET status = 'NO_SELECCIONADO' WHERE status = 'rejected';
UPDATE postulaciones SET status = 'RETIRADO' WHERE status = 'withdrawn';

-- 6. Enlace de solicitudes de contacto con postulación (opcional)
ALTER TABLE solicitudes_contacto
    ADD COLUMN IF NOT EXISTS application_id INT NULL AFTER graduate_id,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at;
