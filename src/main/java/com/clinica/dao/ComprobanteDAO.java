package com.clinica.dao;

import com.clinica.modelo.Comprobante;
import java.math.BigDecimal;

public interface ComprobanteDAO {
    int siguienteCorrelativo(String serie);
    int registrarComprobante(int idPago, String tipo, String serie, int correlativo,
                             String cliTipoDoc, String cliNumDoc, String cliNombre,
                             BigDecimal subtotal, BigDecimal igv, BigDecimal total);
    boolean actualizarSunat(int idComprobante, String estado, String hash, String pdf, String xml);
    Comprobante buscarPorPago(int idPago);
}