package edu.eci.dosw.DOSW_Library.core.util;

import java.time.LocalDate;

public final class DateUtil {
    private DateUtil() {
    }

    public static LocalDate today() {
        return LocalDate.now();
    }

    public static LocalDate addDays(LocalDate baseDate, int days) {
        return baseDate.plusDays(days);
    }
}
