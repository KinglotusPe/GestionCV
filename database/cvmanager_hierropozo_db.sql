-- ============================================================================
-- SCRIPT DE BASE DE DATOS: cvmanager_hierropozo_db
-- INSTITUTO DE EDUCACIÓN SUPERIOR PÚBLICO "MANUEL ANTONIO HIERRO POZO" (AYACUCHO)
-- SISTEMA WEB DE GESTIÓN DE CURRÍCULUMS VITAE
-- MOTOR: MySQL 8.0+ | CHARSET: utf8mb4 | COLLATION: utf8mb4_unicode_ci
-- ============================================================================

DROP DATABASE IF EXISTS cvmanager_hierropozo_db;
CREATE DATABASE cvmanager_hierropozo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cvmanager_hierropozo_db;

-- ----------------------------------------------------------------------------
-- 1. TABLA: carreras (Programas de Estudio Oficiales)
-- ----------------------------------------------------------------------------
CREATE TABLE carreras (
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
CREATE TABLE usuarios (
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
CREATE TABLE egresados (
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
CREATE TABLE empresas (
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
CREATE TABLE cv (
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
CREATE TABLE educacion (
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
CREATE TABLE experiencia (
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
CREATE TABLE habilidades (
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
CREATE TABLE idiomas (
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
CREATE TABLE certificaciones (
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
CREATE TABLE referencias (
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
CREATE TABLE favoritos (
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
CREATE TABLE solicitudes_contacto (
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
CREATE TABLE busquedas (
    search_id INT AUTO_INCREMENT PRIMARY KEY,
    company_id INT NOT NULL,
    criteria VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_search_company FOREIGN KEY (company_id) REFERENCES empresas(company_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE historial_visualizaciones (
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
CREATE TABLE logs_auditoria (
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
('Mecatrónica Automotriz', 'MA', 'Diagnóstico electrónico, sistemas automotrices y mantenimiento electromecánico', 3, TRUE);
