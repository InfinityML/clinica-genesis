package com.clinica.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Verifica el token de Google reCAPTCHA v2 contra el servidor de Google.
 * Si el captcha no está configurado (llaves con XXXX), la verificación se
 * omite para no bloquear el desarrollo. En cuanto pegues llaves reales,
 * queda activo automáticamente.
 */
public final class RecaptchaService {

    private static final Gson GSON = new Gson();
    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    private RecaptchaService() {}

    public static String siteKey()   { return AppConfig.get("recaptcha.site_key"); }
    private static String secretKey() { return AppConfig.get("recaptcha.secret_key"); }

    public static boolean estaConfigurado() {
        return AppConfig.estaDefinido("recaptcha.site_key")
            && AppConfig.estaDefinido("recaptcha.secret_key");
    }

    public static boolean verificar(String token, String ip) {
        // Captcha desactivado hasta configurar llaves reales
        if (!estaConfigurado()) return true;
        if (token == null || token.trim().isEmpty()) return false;

        try {
            StringBuilder body = new StringBuilder();
            body.append("secret=").append(enc(secretKey()));
            body.append("&response=").append(enc(token));
            if (ip != null && !ip.isEmpty()) body.append("&remoteip=").append(enc(ip));

            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VERIFY_URL))
                    .timeout(Duration.ofSeconds(15))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();

            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject json = GSON.fromJson(resp.body(), JsonObject.class);
            return json != null && json.has("success") && json.get("success").getAsBoolean();
        } catch (Exception e) {
            System.err.println("Error verificando reCAPTCHA: " + e.getMessage());
            return false;
        }
    }

    private static String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}