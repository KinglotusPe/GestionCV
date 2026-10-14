CREATE TABLE IF NOT EXISTS password_resets (
 token_hash CHAR(64) PRIMARY KEY,
 user_id INT NOT NULL,
 expires_at TIMESTAMP NOT NULL,
 UNIQUE KEY uk_reset_user(user_id),
 CONSTRAINT fk_reset_user FOREIGN KEY(user_id) REFERENCES usuarios(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;
UPDATE egresados SET document_number=NULL WHERE document_number='';
