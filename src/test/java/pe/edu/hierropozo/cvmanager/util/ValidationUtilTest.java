package pe.edu.hierropozo.cvmanager.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias con JUnit 5 para ValidationUtil.
 */
class ValidationUtilTest {

    @Test
    @DisplayName("Debe validar correctamente formatos de correo electrónico válidos e inválidos")
    void testEmailValidation() {
        assertTrue(ValidationUtil.email("usuario@ejemplo.com"));
        assertTrue(ValidationUtil.email("estudiante.hp@tecnologico.edu.pe"));
        assertFalse(ValidationUtil.email("correo_invalido"));
        assertFalse(ValidationUtil.email(""));
        assertFalse(ValidationUtil.email(null));
    }

    @Test
    @DisplayName("Debe validar que las contraseñas cumplan con el mínimo de 8 caracteres")
    void testPasswordValidation() {
        assertTrue(ValidationUtil.password("12345678"));
        assertTrue(ValidationUtil.password("ClaveSegura2026!"));
        assertFalse(ValidationUtil.password("123"));
        assertFalse(ValidationUtil.password(""));
        assertFalse(ValidationUtil.password(null));
    }
}
