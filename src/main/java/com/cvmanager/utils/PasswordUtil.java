package com.cvmanager.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilidad de seguridad para generación y verificación de hashes BCrypt.
 */
public final class PasswordUtil {

    private static final int BCRYPT_LOG_ROUNDS = 12;

    private PasswordUtil() {
        // Constructor privado para clase utilitaria
    }

    /**
     * Genera un hash seguro para la contraseña en texto plano utilizando salt aleatorio.
     * @param plainPassword Contraseña en texto plano
     * @return Hash en formato String BCrypt
     */
    public static String hash(String plainPassword) {
        if (plainPassword == null) {
            throw new IllegalArgumentException("La contraseña no puede ser nula.");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_LOG_ROUNDS));
    }

    /**
     * Verifica si una contraseña en texto plano coincide con el hash almacenado.
     * @param plainPassword Contraseña ingresada
     * @param hashedPassword Hash guardado en la base de datos
     * @return true si coincide; false en caso contrario
     */
    public static boolean verify(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // En caso de formato de hash inválido
            return false;
        }
    }
}
