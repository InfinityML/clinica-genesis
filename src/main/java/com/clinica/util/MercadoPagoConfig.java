package com.clinica.util;

public final class MercadoPagoConfig {

    private MercadoPagoConfig() {}

    public static String accessToken() { return AppConfig.get("mercadopago.access_token"); }

    public static boolean estaConfigurado() {
        return AppConfig.estaDefinido("mercadopago.access_token");
    }
}