-- ============================================================================
-- SCRIPT DE BASE DE DATOS: cvmanager_hierropozo_db
-- INSTITUTO DE EDUCACIÓN SUPERIOR PÚBLICO "MANUEL ANTONIO HIERRO POZO" (AYACUCHO)
-- SISTEMA WEB DE GESTIÓN DE CURRÍCULUMS VITAE
-- MOTOR: MySQL 8.0+ | CHARSET: utf8mb4 | COLLATION: utf8mb4_unicode_ci
-- ============================================================================

CREATE DATABASE IF NOT EXISTS cvmanager_hierropozo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cvmanager_hierropozo_db;

-- ----------------------------------------------------------------------------
-- 1. TABLA: carreras (Programas de Estudio Oficiales)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS carreras (
    career_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL UNIQUE,
    code VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(255),
    duration_years TINYINT NOT NULL DEFAULT 3,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 2. TABLA: usuarios (Credenciales, Roles y Estados)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuarios (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    role ENUM('graduate', 'company', 'admin') NOT NULL,
    status ENUM('active', 'inactive', 'pending', 'suspended') NOT NULL DEFAULT 'pending',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    INDEX idx_user_role_status(role, status)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 3. TABLA: egresados (Datos Personales y Académicos)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS egresados (
    graduate_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    document_type VARCHAR(20) DEFAULT 'DNI',
    document_number VARCHAR(20) UNIQUE,
    phone VARCHAR(30),
    address VARCHAR(180),
    city VARCHAR(80) DEFAULT 'Ayacucho',
    country VARCHAR(80) DEFAULT 'Perú',
    birth_date DATE NULL,
    photo_url VARCHAR(255),
    linkedin_url VARCHAR(255),
    portfolio_url VARCHAR(255),
    career_id INT NULL,
    graduation_year SMALLINT NULL,
    is_public BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_grad_user FOREIGN KEY (user_id) REFERENCES usuarios(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_grad_career FOREIGN KEY (career_id) REFERENCES carreras(career_id) ON DELETE SET NULL,
    INDEX idx_grad_career(career_id),
    INDEX idx_grad_city(city)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 4. TABLA: empresas (Datos Corporativos y Empleadores)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS empresas (
    company_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    company_name VARCHAR(150) NOT NULL,
    ruc VARCHAR(11) UNIQUE,
    industry VARCHAR(100),
    company_size VARCHAR(50),
    phone VARCHAR(30),
    website VARCHAR(255),
    description TEXT,
    logo_url VARCHAR(255),
    CONSTRAINT fk_company_user FOREIGN KEY (user_id) REFERENCES usuarios(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 5. TABLA: cv (Cabecera del Currículum Vitae)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cv (
    cv_id INT AUTO_INCREMENT PRIMARY KEY,
    graduate_id INT NOT NULL UNIQUE,
    title VARCHAR(150) NOT NULL DEFAULT 'Mi Currículum Vitae',
    professional_summary TEXT,
    cv_pdf_url VARCHAR(255),
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    views_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_cv_grad FOREIGN KEY (graduate_id) REFERENCES egresados(graduate_id) ON DELETE CASCADE,
    INDEX idx_cv_published(is_published)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 6. TABLA: educacion (Historial de Estudios del Egresado)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS educacion (
    education_id INT AUTO_INCREMENT PRIMARY KEY,
    cv_id INT NOT NULL,
    institution VARCHAR(150) NOT NULL,
    degree VARCHAR(120) NOT NULL,
    field_of_study VARCHAR(120),
    start_date DATE,
    end_date DATE,
    is_current BOOLEAN NOT NULL DEFAULT FALSE,
    description TEXT,
    gpa VARCHAR(20),
    CONSTRAINT fk_edu_cv FOREIGN KEY (cv_id) REFERENCES cv(cv_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 7. TABLA: experiencia (Trayectoria Laboral del Egresado)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS experiencia (
    experience_id INT AUTO_INCREMENT PRIMARY KEY,
    cv_id INT NOT NULL,
    company_name VARCHAR(150) NOT NULL,
    position VARCHAR(120) NOT NULL,
    start_date DATE,
    end_date DATE,
    is_current BOOLEAN NOT NULL DEFAULT FALSE,
    responsibilities TEXT,
    achievements TEXT,
    employment_type VARCHAR(50),
    CONSTRAINT fk_exp_cv FOREIGN KEY (cv_id) REFERENCES cv(cv_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 8. TABLA: habilidades (Competencias Técnicas y Blandas)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS habilidades (
    skill_id INT AUTO_INCREMENT PRIMARY KEY,
    cv_id INT NOT NULL,
    skill_name VARCHAR(100) NOT NULL,
    skill_category ENUM('technical', 'soft', 'other') DEFAULT 'technical',
    proficiency_level TINYINT NOT NULL DEFAULT 3,
    CONSTRAINT fk_skill_cv FOREIGN KEY (cv_id) REFERENCES cv(cv_id) ON DELETE CASCADE,
    INDEX idx_skill_name(skill_name)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 9. TABLA: idiomas (Dominio de Lenguas)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS idiomas (
    language_id INT AUTO_INCREMENT PRIMARY KEY,
    cv_id INT NOT NULL,
    language_name VARCHAR(80) NOT NULL,
    proficiency_level ENUM('basic', 'intermediate', 'advanced', 'native') NOT NULL DEFAULT 'basic',
    certificaciones VARCHAR(255),
    CONSTRAINT fk_lang_cv FOREIGN KEY (cv_id) REFERENCES cv(cv_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 10. TABLA: certificaciones (Cursos y Diplomas Adicionales)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS certificaciones (
    certification_id INT AUTO_INCREMENT PRIMARY KEY,
    cv_id INT NOT NULL,
    name VARCHAR(150) NOT NULL,
    issuing_organization VARCHAR(150),
    issue_date DATE,
    expiration_date DATE,
    credential_id VARCHAR(100),
    credential_url VARCHAR(255),
    CONSTRAINT fk_cert_cv FOREIGN KEY (cv_id) REFERENCES cv(cv_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 11. TABLA: referencias (Contactos Profesionales de Recomendación)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS referencias (
    reference_id INT AUTO_INCREMENT PRIMARY KEY,
    cv_id INT NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    position VARCHAR(120),
    company_name VARCHAR(150),
    phone VARCHAR(30),
    email VARCHAR(150),
    CONSTRAINT fk_ref_cv FOREIGN KEY (cv_id) REFERENCES cv(cv_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 12. TABLA: favoritos (Candidatos Guardados por Empresas)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS favoritos (
    favorite_id INT AUTO_INCREMENT PRIMARY KEY,
    company_id INT NOT NULL,
    cv_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_favorite(company_id, cv_id),
    CONSTRAINT fk_fav_company FOREIGN KEY (company_id) REFERENCES empresas(company_id) ON DELETE CASCADE,
    CONSTRAINT fk_fav_cv FOREIGN KEY (cv_id) REFERENCES cv(cv_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 13. TABLA: solicitudes_contacto (Comunicación Empresa -> Egresado)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS solicitudes_contacto (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    company_id INT NOT NULL,
    graduate_id INT NOT NULL,
    message TEXT NOT NULL,
    status ENUM('pending', 'accepted', 'rejected') NOT NULL DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_req_company FOREIGN KEY (company_id) REFERENCES empresas(company_id) ON DELETE CASCADE,
    CONSTRAINT fk_req_grad FOREIGN KEY (graduate_id) REFERENCES egresados(graduate_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 14. TABLA: busquedas e historial de visualizaciones
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS busquedas (
    search_id INT AUTO_INCREMENT PRIMARY KEY,
    company_id INT NOT NULL,
    criteria VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_search_company FOREIGN KEY (company_id) REFERENCES empresas(company_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS historial_visualizaciones (
    view_id INT AUTO_INCREMENT PRIMARY KEY,
    company_id INT NOT NULL,
    cv_id INT NOT NULL,
    viewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_view_company FOREIGN KEY (company_id) REFERENCES empresas(company_id) ON DELETE CASCADE,
    CONSTRAINT fk_view_cv FOREIGN KEY (cv_id) REFERENCES cv(cv_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 15. TABLA: logs_auditoria (Bitácora de Trazabilidad)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS logs_auditoria (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(80),
    entity_id INT NULL,
    details VARCHAR(500),
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_log_user FOREIGN KEY (user_id) REFERENCES usuarios(user_id) ON DELETE SET NULL,
    INDEX idx_log_date(created_at),
    INDEX idx_log_user(user_id)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- DATOS INICIALES: PROGRAMAS DE ESTUDIO OFICIALES DEL INSTITUTO HIERRO POZO
-- ----------------------------------------------------------------------------
INSERT INTO carreras (name, code, description, duration_years, is_active) VALUES
('Administración de Redes y Comunicaciones', 'ARC', 'Gestión, administración y seguridad de infraestructuras y telecomunicaciones', 3, TRUE),
('Electricidad Industrial', 'EI', 'Automatización, control eléctrico industrial y sistemas de potencia', 3, TRUE),
('Gestión Administrativa', 'GA', 'Administración empresarial, contabilidad, logística y talento humano', 3, TRUE),
('Mecatrónica Automotriz', 'MA', 'Diagnóstico electrónico, sistemas automotrices y mantenimiento electromecánico', 3, TRUE) ON DUPLICATE KEY UPDATE code=VALUES(code);

CREATE TABLE IF NOT EXISTS password_resets (
 token_hash CHAR(64) PRIMARY KEY,
 user_id INT NOT NULL,
 expires_at TIMESTAMP NOT NULL,
 UNIQUE KEY uk_reset_user(user_id),
 CONSTRAINT fk_reset_user FOREIGN KEY(user_id) REFERENCES usuarios(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS vacantes (
 job_id INT AUTO_INCREMENT PRIMARY KEY,
 company_id INT NOT NULL,
 career_id INT NULL,
 title VARCHAR(150) NOT NULL,
 description TEXT NOT NULL,
 city VARCHAR(80) NOT NULL,
 modality ENUM('onsite','remote','hybrid') NOT NULL,
 salary VARCHAR(100),
 status ENUM('open','closed') NOT NULL DEFAULT 'open',
 closing_date DATE NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 CONSTRAINT fk_job_company FOREIGN KEY(company_id) REFERENCES empresas(company_id) ON DELETE CASCADE,
 CONSTRAINT fk_job_career FOREIGN KEY(career_id) REFERENCES carreras(career_id) ON DELETE SET NULL,
 INDEX idx_job_status(status,closing_date)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS postulaciones (
 application_id INT AUTO_INCREMENT PRIMARY KEY,
 job_id INT NOT NULL,
 graduate_id INT NOT NULL,
 message TEXT NOT NULL,
 status ENUM('pending','reviewing','accepted','rejected','withdrawn') NOT NULL DEFAULT 'pending',
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 UNIQUE KEY uk_job_graduate(job_id,graduate_id),
 CONSTRAINT fk_application_job FOREIGN KEY(job_id) REFERENCES vacantes(job_id) ON DELETE CASCADE,
 CONSTRAINT fk_application_graduate FOREIGN KEY(graduate_id) REFERENCES egresados(graduate_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Bolsa laboral institucional
-- Extensión aditiva y repetible. Ejecutar después de 002_jobs.sql.
CREATE TABLE IF NOT EXISTS vacante_detalles (
 job_id INT PRIMARY KEY,
 opening_date DATE NULL,
 shift_name VARCHAR(40) NOT NULL DEFAULT 'Completo',
 positions INT NOT NULL DEFAULT 1,
 requirements TEXT NULL,
 benefits TEXT NULL,
 conditions_text TEXT NULL,
 external_url VARCHAR(500) NULL,
 managed_by INT NULL,
 archived BOOLEAN NOT NULL DEFAULT FALSE,
 FOREIGN KEY(job_id) REFERENCES vacantes(job_id) ON DELETE CASCADE,
 FOREIGN KEY(managed_by) REFERENCES usuarios(user_id) ON DELETE SET NULL
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS vacante_carreras (
 job_id INT NOT NULL,
 career_id INT NOT NULL,
 PRIMARY KEY(job_id,career_id),
 FOREIGN KEY(job_id) REFERENCES vacantes(job_id) ON DELETE CASCADE,
 FOREIGN KEY(career_id) REFERENCES carreras(career_id) ON DELETE CASCADE
) ENGINE=InnoDB;
INSERT IGNORE INTO vacante_carreras(job_id,career_id)
 SELECT job_id,career_id FROM vacantes WHERE career_id IS NOT NULL;
CREATE TABLE IF NOT EXISTS vacante_documentos (
 document_id INT AUTO_INCREMENT PRIMARY KEY,
 job_id INT NOT NULL,
 title VARCHAR(150) NOT NULL,
 storage_name VARCHAR(80) NOT NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY(job_id) REFERENCES vacantes(job_id) ON DELETE CASCADE
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS empresa_revisiones (
 review_id INT AUTO_INCREMENT PRIMARY KEY,
 company_id INT NOT NULL,
 actor_id INT NULL,
 decision ENUM('active','inactive','suspended') NOT NULL,
 reason VARCHAR(1000) NOT NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY(company_id) REFERENCES empresas(company_id) ON DELETE CASCADE,
 FOREIGN KEY(actor_id) REFERENCES usuarios(user_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- DATOS INICIALES: CUENTAS DE DEMOSTRACIÓN Y PRUEBAS (ROLES ACTIVOS)
-- ----------------------------------------------------------------------------

-- 1. Administrador Institucional
-- Correo: admin@hierropozo.edu.pe | Contraseña: Admin123!
INSERT INTO usuarios (email, password_hash, role, status)
VALUES ('admin@hierropozo.edu.pe', '$2a$12$ya.X2UXOe.0rxLNyBGjiVOH771VynXu3WKsckD1CHgIEAZumbsah6', 'admin', 'active')
ON DUPLICATE KEY UPDATE password_hash=VALUES(password_hash), status='active';

-- 2. Egresado
-- Correo: egresado@hierropozo.edu.pe | Contraseña: Egresado123!
INSERT INTO usuarios (email, password_hash, role, status)
VALUES ('egresado@hierropozo.edu.pe', '$2a$12$35/.ANNbvmi4Wca5Mfbo4uOpORAvocfKrx.eH4xgie3TBctV7HXCK', 'graduate', 'active')
ON DUPLICATE KEY UPDATE password_hash=VALUES(password_hash), status='active';

SET @grad_user_id = (SELECT user_id FROM usuarios WHERE email = 'egresado@hierropozo.edu.pe');

INSERT INTO egresados (user_id, first_name, last_name, document_type, document_number, phone, city, career_id, graduation_year, is_public)
VALUES (@grad_user_id, 'Juan', 'Pérez Quispe', 'DNI', '71234567', '966123456', 'Ayacucho', 1, 2024, TRUE)
ON DUPLICATE KEY UPDATE phone=VALUES(phone), is_public=TRUE;

SET @graduate_id = (SELECT graduate_id FROM egresados WHERE user_id = @grad_user_id);

INSERT INTO cv (graduate_id, title, professional_summary, is_published)
VALUES (@graduate_id, 'Técnico en Redes y Telecomunicaciones', 'Egresado del IESP Manuel Antonio Hierro Pozo especializado en conectividad, redes de datos y soporte computacional.', TRUE)
ON DUPLICATE KEY UPDATE title=VALUES(title), is_published=TRUE;

-- 3. Empresa
-- Correo: empresa@hierropozo.edu.pe | Contraseña: Empresa123!
INSERT INTO usuarios (email, password_hash, role, status)
VALUES ('empresa@hierropozo.edu.pe', '$2a$12$CxmwgpxLIJUkTFSBt3pyg.jEsfApp0frN0PXiGb9VxyswncB6GYx.', 'company', 'active')
ON DUPLICATE KEY UPDATE password_hash=VALUES(password_hash), status='active';

SET @comp_user_id = (SELECT user_id FROM usuarios WHERE email = 'empresa@hierropozo.edu.pe');

INSERT INTO empresas (user_id, company_name, ruc, industry, company_size, phone, website)
VALUES (@comp_user_id, 'Tech Solutions Ayacucho S.A.C.', '20601234567', 'Tecnología e Informática', 'Mediana', '966987654', 'https://techsolutions.pe')
ON DUPLICATE KEY UPDATE company_name=VALUES(company_name);

SET @company_id = (SELECT company_id FROM empresas WHERE user_id = @comp_user_id);

INSERT INTO vacantes (company_id, career_id, title, description, city, modality, salary, status)
VALUES (@company_id, 1, 'Técnico de Soporte TI y Redes', 'Empresa líder en soluciones informáticas requiere técnico egresado para mantenimiento de infraestructura de redes.', 'Ayacucho', 'onsite', 'S/ 1,500 - 1,800', 'open')
ON DUPLICATE KEY UPDATE title=VALUES(title);

