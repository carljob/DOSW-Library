package edu.eci.dosw.DOSW_Library.core.util;

/**
 * Utilidad con validaciones básicas para argumentos de entrada.
 */
public final class ValidationUtil {
    private ValidationUtil() {
    }

    public static void requireNotNull(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireNotBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requirePositive(int value, String message) {
        if (value <= 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireZeroOrPositive(int value, String message) {
        if (value < 0) {
            throw new IllegalArgumentException(message);
        }
    }
}
