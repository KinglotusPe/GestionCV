package com.cvmanager.utils;

import org.apache.commons.validator.routines.EmailValidator;

/**
 * Utilidad de validación y saneamiento de entradas de usuario.
 */
public final class ValidacionUtil {

    private ValidacionUtil() {
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
        return password != null && password.length() >= 8
                && password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length <= 72;
    }

    /**
     * Elimina espacios en blanco residuales y previene valores nulos devolviendo cadena vacía.
     */
    public static String clean(String input) {
        return input == null ? "" : input.trim();
    }

    public static String text(String value, String label, int max, boolean required) {
        String v = clean(value);
        if ((required && v.isEmpty()) || v.length() > max) {
            throw new IllegalArgumentException(label + ": ingrese " + (required ? "entre 1 y " : "hasta ") + max + " caracteres.");
        }
        return v;
    }

    public static String nullable(String value) { return clean(value).isEmpty() ? null : value.trim(); }
    public static boolean dni(String value) { return value != null && value.matches("[0-9]{8}"); }
    public static boolean phone(String value) { return value != null && value.replaceAll("[ +()-]", "").matches("(?:51)?[0-9]{7,9}"); }
    public static boolean ruc(String value) {
        if (value == null || !value.matches("(?:10|15|16|17|20)[0-9]{9}")) return false;
        int[] weights = {5,4,3,2,7,6,5,4,3,2};
        int sum = 0;
        for (int i = 0; i < 10; i++) sum += (value.charAt(i)-'0') * weights[i];
        return (11 - sum % 11) % 10 == value.charAt(10)-'0';
    }
    public static String url(String value) {
        String v = text(value, "URL", 255, false);
        if (v.isEmpty()) return null;
        try {
            java.net.URI uri = java.net.URI.create(v);
            if (!("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme())) || uri.getHost() == null)
                throw new IllegalArgumentException();
        } catch (IllegalArgumentException e) { throw new IllegalArgumentException("Ingrese una URL HTTP o HTTPS válida."); }
        return v;
    }
    public static int integer(String value, String label, int min, int max) {
        try {
            int n = Integer.parseInt(clean(value));
            if (n >= min && n <= max) return n;
        } catch (NumberFormatException ignored) { }
        throw new IllegalArgumentException(label + ": valor fuera del rango " + min + "–" + max + ".");
    }
    public static void dates(String start, String end) {
        java.time.LocalDate a = clean(start).isEmpty() ? null : java.time.LocalDate.parse(start);
        java.time.LocalDate b = clean(end).isEmpty() ? null : java.time.LocalDate.parse(end);
        if (a != null && b != null && b.isBefore(a)) throw new IllegalArgumentException("La fecha final debe ser posterior al inicio.");
    }
}
