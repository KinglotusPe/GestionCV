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
