package com.clinica.util;

/**
 * Identificadores de rol centralizados para evitar "números mágicos"
 * repartidos por servlets y JSP.
 */
public final class Roles {

    public static final int ADMINISTRADOR = 1;
    public static final int DOCTOR        = 2;
    public static final int PRACTICANTE   = 3;
    public static final int PACIENTE      = 4;
    public static final int RECEPCIONISTA = 5;
    public static final int ENFERMERO     = 6;

    private Roles() {}

    public static String nombre(int idRol) {
        switch (idRol) {
            case ADMINISTRADOR: return "Administrador";
            case DOCTOR:        return "Doctor";
            case PRACTICANTE:   return "Practicante";
            case PACIENTE:      return "Paciente";
            case RECEPCIONISTA: return "Recepcionista";
            case ENFERMERO:     return "Enfermero";
            default:            return "Usuario";
        }
    }
}