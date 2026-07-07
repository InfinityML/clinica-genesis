package com.clinica.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Reglas de la ventana horaria para unirse a una teleconsulta.
 * El paciente puede unirse desde 15 min antes hasta 120 min después de la hora.
 */
public final class TeleconsultaUtil {

    private static final int ANTES_MIN = 15;
    private static final int DESPUES_MIN = 120;

    private TeleconsultaUtil() {}

    public static boolean dentroDeVentana(java.sql.Date fecha, java.sql.Time hora) {
        if (fecha == null || hora == null) return false;
        LocalDate d = fecha.toLocalDate();
        LocalTime h = hora.toLocalTime();
        LocalDateTime cita = LocalDateTime.of(d, h);
        LocalDateTime ahora = LocalDateTime.now();
        return !ahora.isBefore(cita.minusMinutes(ANTES_MIN))
            && !ahora.isAfter(cita.plusMinutes(DESPUES_MIN));
    }

    /**
     * "disponible" | "pendiente_confirmacion" | "fuera_de_hora" | null (no aplica).
     */
    public static String estadoUnion(String modalidad, String estadoCita, int confirmada,
                                     java.sql.Date fecha, java.sql.Time hora) {
        if (!"Teleconsulta".equals(modalidad) || !"Pendiente".equals(estadoCita)) return null;
        if (confirmada != 1) return "pendiente_confirmacion";
        if (!dentroDeVentana(fecha, hora)) return "fuera_de_hora";
        return "disponible";
    }
}