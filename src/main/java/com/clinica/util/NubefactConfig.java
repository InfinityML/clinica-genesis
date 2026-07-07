package com.clinica.util;

/**
 * Configuración de Nubefact (facturación SUNAT). Valores desde la
 * configuración externa (config.properties / variables de entorno).
 */
public final class NubefactConfig {

    private NubefactConfig() {}

    public static String ruta()   { return AppConfig.get("nubefact.ruta"); }
    public static String token()  { return AppConfig.get("nubefact.token"); }
    public static String rucEmisor()    { return AppConfig.get("nubefact.ruc_emisor", "20123456789"); }
    public static String nombreEmisor() { return AppConfig.get("nubefact.nombre_emisor", "CLINICA UNIVERSITARIA UPN S.A.C."); }

    public static boolean estaConfigurado() {
        return AppConfig.estaDefinido("nubefact.token") && AppConfig.estaDefinido("nubefact.ruta");
    }
}