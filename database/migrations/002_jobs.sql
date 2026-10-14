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
