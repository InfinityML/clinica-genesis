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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Emite comprobantes electrónicos (Boleta/Factura) contra la API de Nubefact.
 * Si no hay credenciales configuradas, simula una respuesta aceptada por SUNAT.
 */
public final class NubefactService {

    private static final Gson GSON = new Gson();

    private NubefactService() {}

    public static class Resultado {
        public boolean exito;
        public String estado;   // Aceptado / Rechazado
        public String hash;
        public String pdf;
        public String xml;
        public String mensaje;
        public Resultado(boolean exito, String estado, String hash, String pdf, String xml, String mensaje) {
            this.exito = exito; this.estado = estado; this.hash = hash;
            this.pdf = pdf; this.xml = xml; this.mensaje = mensaje;
        }
    }

    /**
     * @param tipoComprobante "Factura" o "Boleta"
     * @param afectoIgv true = gravado (IGV 18%), false = exonerado
     */
    public static Resultado emitir(String tipoComprobante, String serie, int correlativo,
                                   String cliTipoDoc, String cliNumDoc, String cliNombre,
                                   BigDecimal subtotal, BigDecimal igv, BigDecimal total,
                                   boolean afectoIgv, String descripcion) {

        // MODO SIMULACIÓN
        /*if (!NubefactConfig.estaConfigurado()) {
            String num = serie + "-" + correlativo;
            return new Resultado(true, "Aceptado",
                    "SIM" + System.currentTimeMillis(),
                    "https://simulado.local/comprobante/" + num + ".pdf",
                    "https://simulado.local/comprobante/" + num + ".xml",
                    "Comprobante emitido en modo simulación (aceptado por SUNAT).");
        }*/
        
        // Requiere credenciales reales configuradas (sin modo simulación).
        if (!NubefactConfig.estaConfigurado()) {
            return new Resultado(false, "Rechazado", null, null, null,
                    "Nubefact no está configurado. Pega tu RUTA y TOKEN reales en NubefactConfig.java.");
        }

        try {
            int tipoNum = "Factura".equalsIgnoreCase(tipoComprobante) ? 1 : 2;

            JsonObject item = new JsonObject();
            item.addProperty("unidad_de_medida", "ZZ");
            item.addProperty("codigo", "SERV01");
            item.addProperty("descripcion", descripcion);
            item.addProperty("cantidad", 1);
            item.addProperty("valor_unitario", afectoIgv ? subtotal : total);
            item.addProperty("precio_unitario", total);
            item.addProperty("descuento", "");
            item.addProperty("subtotal", afectoIgv ? subtotal : total);
            item.addProperty("tipo_de_igv", afectoIgv ? 1 : 8);
            item.addProperty("igv", igv);
            item.addProperty("total", total);
            item.addProperty("anticipo_regularizacion", false);

            JsonArray items = new JsonArray();
            items.add(item);

            JsonObject body = new JsonObject();
            body.addProperty("operacion", "generar_comprobante");
            body.addProperty("tipo_de_comprobante", tipoNum);
            body.addProperty("serie", serie);
            body.addProperty("numero", correlativo);
            body.addProperty("sunat_transaction", 1);
            body.addProperty("cliente_tipo_de_documento", cliTipoDoc);
            body.addProperty("cliente_numero_de_documento", cliNumDoc);
            body.addProperty("cliente_denominacion", cliNombre);
            body.addProperty("cliente_direccion", "-");
            body.addProperty("fecha_de_emision", LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            body.addProperty("moneda", 1);
            body.addProperty("porcentaje_de_igv", 18.00);
            if (afectoIgv) body.addProperty("total_gravada", subtotal);
            else body.addProperty("total_exonerada", total);
            body.addProperty("total_igv", igv);
            body.addProperty("total", total);
            body.addProperty("enviar_automaticamente_a_la_sunat", true);
            body.add("items", items);

            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(NubefactConfig.ruta()))
                    .timeout(Duration.ofSeconds(25))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Token token=" + NubefactConfig.token())
                    .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(body)))
                    .build();

            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject json = GSON.fromJson(resp.body(), JsonObject.class);

            if (json != null && json.has("errors")) {
                return new Resultado(false, "Rechazado", null, null, null,
                        json.get("errors").getAsString());
            }

            boolean aceptada = json != null && json.has("aceptada_por_sunat")
                    && json.get("aceptada_por_sunat").getAsBoolean();
            String hash = (json != null && json.has("codigo_hash")) ? json.get("codigo_hash").getAsString() : "";
            String pdf = (json != null && json.has("enlace_del_pdf")) ? json.get("enlace_del_pdf").getAsString() : "";
            String xml = (json != null && json.has("enlace_del_xml")) ? json.get("enlace_del_xml").getAsString() : "";
            String desc = (json != null && json.has("sunat_description")) ? json.get("sunat_description").getAsString() : "";

            return new Resultado(aceptada, aceptada ? "Aceptado" : "Rechazado",
                    hash, pdf, xml, aceptada ? "Comprobante aceptado por SUNAT." : ("SUNAT: " + desc));

        } catch (Exception e) {
            return new Resultado(false, "Rechazado", null, null, null,
                    "Error de conexión con Nubefact: " + e.getMessage());
        }
    }
}