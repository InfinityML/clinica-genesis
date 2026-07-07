package com.clinica.util;

/**
 * Configuración de la pasarela Culqui. Los valores se leen de la
 * configuración externa (config.properties / variables de entorno),
 * nunca del código fuente.
 */
public final class CulquiConfig {

    private CulquiConfig() {}

    public static String publicKey() { return AppConfig.get("culqui.public_key"); }
    public static String secretKey() { return AppConfig.get("culqui.secret_key"); }

    public static final String CURRENCY = "PEN";
    public static final String CHARGES_URL = "https://api.culqi.com/v2/charges";

    public static boolean estaConfigurado() {
        return AppConfig.estaDefinido("culqui.secret_key")
            && secretKey().startsWith("sk_");
    }
}