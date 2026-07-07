package com.clinica.dao;

import java.util.List;
import java.util.Map;

public interface HistorialDAO {
    List<Map<String, Object>> obtenerMiHistorial(int idUsuario);
}