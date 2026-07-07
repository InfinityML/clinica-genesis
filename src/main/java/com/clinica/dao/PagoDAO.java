package com.clinica.dao;

import com.clinica.modelo.Pago;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PagoDAO {
    int registrarPago(int idCita, int idServicio, BigDecimal monto, String metodo, Integer idRecepcionista);
    boolean confirmarPago(int idPago, String token, String chargeId);
    boolean anularPago(int idPago);
    Pago buscarPago(int idPago);
    List<Map<String, Object>> listarPagos();
}