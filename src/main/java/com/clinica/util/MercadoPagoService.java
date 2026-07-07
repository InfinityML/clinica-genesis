package com.clinica.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class MercadoPagoService {

    private static final Gson GSON = new Gson();
    private static final String PREF_URL = "https://api.mercadopago.com/checkout/preferences";
    private static final String PAY_URL  = "https://api.mercadopago.com/v1/payments/";

    private MercadoPagoService() {}

    public static String crearPreferencia(int idPago, String descripcion, BigDecimal monto, String baseUrl) {
        if (!MercadoPagoConfig.estaConfigurado()) return null;
        try {
            JsonObject item = new JsonObject();
            item.addProperty("title", descripcion);
            item.addProperty("quantity", 1);
            item.addProperty("unit_price", monto);
            item.addProperty("currency_id", "PEN");

            JsonArray items = new JsonArray();
            items.add(item);

            String retorno = baseUrl + "/PagoMercadoPagoServlet?accion=retorno";
            JsonObject backUrls = new JsonObject();
            backUrls.addProperty("success", retorno);
            backUrls.addProperty("failure", retorno);
            backUrls.addProperty("pending", retorno);

            JsonObject body = new JsonObject();
            body.add("items", items);
            body.addProperty("external_reference", String.valueOf(idPago));
            body.add("back_urls", backUrls);
            body.addProperty("auto_return", "approved");

            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(PREF_URL))
                    .timeout(Duration.ofSeconds(25))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + MercadoPagoConfig.accessToken())
                    .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(body)))
                    .build();

            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject json = GSON.fromJson(resp.body(), JsonObject.class);
            if (json != null && json.has("init_point")) {
                return json.get("init_point").getAsString();
            }
            System.err.println("[MercadoPago] Respuesta sin init_point: " + resp.body());
            return null;
        } catch (Exception e) {
            System.err.println("[MercadoPago] Error creando preferencia: " + e.getMessage());
            return null;
        }
    }

    public static String verificarPago(String paymentId) {
        if (paymentId == null || paymentId.isEmpty() || !MercadoPagoConfig.estaConfigurado()) return null;
        try {
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(PAY_URL + paymentId))
                    .timeout(Duration.ofSeconds(20))
                    .header("Authorization", "Bearer " + MercadoPagoConfig.accessToken())
                    .GET()
                    .build();
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject json = GSON.fromJson(resp.body(), JsonObject.class);
            return (json != null && json.has("status")) ? json.get("status").getAsString() : null;
        } catch (Exception e) {
            System.err.println("[MercadoPago] Error verificando pago: " + e.getMessage());
            return null;
        }
    }
}