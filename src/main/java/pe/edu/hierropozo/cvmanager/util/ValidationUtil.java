package pe.edu.hierropozo.cvmanager.util;

import org.apache.commons.validator.routines.EmailValidator;

/**
 * Utilidad de validación y saneamiento de entradas de usuario.
 */
public final class ValidationUtil {

    private ValidationUtil() {
        // Constructor privado
    }

    /**
     * Valida sintaxis de correo electrónico usando Commons Validator.
     */
    public static boolean email(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EmailValidator.getInstance().isValid(email.trim());
    }

    /**
     * Valida que la contraseña cumpla con una longitud mínima de 8 caracteres.
     */
    public static boolean password(String password) {
        return password != null && password.length() >= 8;
    }

    /**
     * Elimina espacios en blanco residuales y previene valores nulos devolviendo cadena vacía.
     */
    public static String clean(String input) {
        return input == null ? "" : input.trim();
    }
}
