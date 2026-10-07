package pe.edu.hierropozo.cvmanager.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para el utilitario de seguridad PasswordUtil (BCrypt).
 */
class PasswordUtilTest {

    @Test
    @DisplayName("Debe generar un hash BCrypt válido y verificar coincidencias")
    void testHashAndVerify() {
        String plainPassword = "SuperPassword2026!";
        String hash = PasswordUtil.hash(plainPassword);

        assertNotNull(hash, "El hash generado no debe ser nulo");
        assertTrue(hash.startsWith("$2a$"), "El hash debe tener formato BCrypt ($2a$)");

        assertTrue(PasswordUtil.verify(plainPassword, hash), "La contraseña correcta debe coincidir con su hash");
        assertFalse(PasswordUtil.verify("PasswordEquivocada", hash), "Una contraseña errónea no debe coincidir");
        assertFalse(PasswordUtil.verify(null, hash), "Una contraseña nula debe retornar false");
        assertFalse(PasswordUtil.verify(plainPassword, null), "Un hash nulo debe retornar false");
    }
}
