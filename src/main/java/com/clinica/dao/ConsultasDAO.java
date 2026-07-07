package com.clinica.dao;

import java.util.List;
import java.util.Map;

public interface ConsultasDAO {
    List<Map<String, Object>> buscarHistorialPorDNI(String dni);
    List<Map<String, Object>> obtenerReporteEspecialidades();
}