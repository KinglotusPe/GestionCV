package com.cvmanager.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias con JUnit 5 para ValidacionUtil.
 */
class ValidationUtilTest {

    @Test
    @DisplayName("Debe validar correctamente formatos de correo electrónico válidos e inválidos")
    void testEmailValidation() {
        assertTrue(ValidacionUtil.email("usuario@ejemplo.com"));
        assertTrue(ValidacionUtil.email("estudiante.hp@tecnologico.edu.pe"));
        assertFalse(ValidacionUtil.email("correo_invalido"));
        assertFalse(ValidacionUtil.email(""));
        assertFalse(ValidacionUtil.email(null));
    }

    @Test
    @DisplayName("Debe validar que las contraseñas cumplan con el mínimo de 8 caracteres")
    void testPasswordValidation() {
        assertTrue(ValidacionUtil.password("12345678"));
        assertTrue(ValidacionUtil.password("ClaveSegura2026!"));
        assertFalse(ValidacionUtil.password("123"));
        assertFalse(ValidacionUtil.password(""));
        assertFalse(ValidacionUtil.password(null));
    }
}
