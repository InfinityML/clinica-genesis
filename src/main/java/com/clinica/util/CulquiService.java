package com.clinica.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Realiza el cargo contra la API de Culqui usando la llave secreta.
 * El token de tarjeta se genera en el navegador (Culqui Checkout) y llega aquí.
 */
public final class CulquiService {

    private static final Gson GSON = new Gson();

    private CulquiService() {}

    /** Resultado simple del cobro. */
    public static class Resultado {
        public boolean exito;
        public String chargeId;
        public String mensaje;
        public Resultado(boolean exito, String chargeId, String mensaje) {
            this.exito = exito; this.chargeId = chargeId; this.mensaje = mensaje;
        }
    }

    /**
     * Crea un cargo. montoCentimos = precio en soles * 100 (ej. S/50.00 -> 5000).
     */
    public static Resultado crearCargo(String token, int montoCentimos, String email) {

        // MODO SIMULACIÓN: sin llaves reales, aprobamos el pago localmente.
        /*if (!CulquiConfig.estaConfigurado()) {
            return new Resultado(true, "sim_" + System.currentTimeMillis(),
                    "Pago aprobado en modo simulación (sin llaves de Culqui).");
        }*/
        
        // Requiere llaves reales configuradas (sin modo simulación).
        if (!CulquiConfig.estaConfigurado()) {
            return new Resultado(false, null,
                    "Culqui no está configurado. Pega tus llaves reales en CulquiConfig.java.");
        }

        try {
            Map<String, Object> cuerpo = new LinkedHashMap<>();
            cuerpo.put("source_id", token);
            cuerpo.put("amount", montoCentimos);
            cuerpo.put("currency_code", CulquiConfig.CURRENCY);
            cuerpo.put("email", (email != null && !email.isEmpty()) ? email : "cliente@clinica.edu.pe");

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(15)).build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CulquiConfig.CHARGES_URL))
                    .timeout(Duration.ofSeconds(20))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + CulquiConfig.secretKey())
                    .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(cuerpo)))
                    .build();

            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject json = GSON.fromJson(resp.body(), JsonObject.class);

            if (resp.statusCode() == 200 || resp.statusCode() == 201) {
                String id = (json != null && json.has("id")) ? json.get("id").getAsString() : "";
                return new Resultado(true, id, "Pago aprobado por Culqui.");
            } else {
                String msg = "Pago rechazado por Culqui.";
                if (json != null && json.has("user_message")) msg = json.get("user_message").getAsString();
                else if (json != null && json.has("merchant_message")) msg = json.get("merchant_message").getAsString();
                return new Resultado(false, null, msg);
            }
        } catch (Exception e) {
            return new Resultado(false, null, "Error de conexión con Culqui: " + e.getMessage());
        }
    }
}