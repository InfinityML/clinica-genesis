package com.clinica.dao.impl;

import com.clinica.dao.ConsultasDAO;
import com.clinica.util.ConexionDB;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsultasDAOImpl implements ConsultasDAO {

    @Override
    public List<Map<String, Object>> buscarHistorialPorDNI(String dni) {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_buscar_historial_dni(?)}")) {
            cs.setString(1, dni);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("fecha", rs.getDate("fecha"));
                    fila.put("motivo", rs.getString("motivo_consulta"));
                    fila.put("diagnostico", rs.getString("diagnostico"));
                    fila.put("medico", rs.getString("medico"));
                    fila.put("especialidad", rs.getString("especialidad"));
                    fila.put("medicamento", rs.getString("medicamento"));
                    lista.add(fila);
                }
            }
        } catch (SQLException e) { System.err.println("Error Búsqueda Historial: " + e.getMessage()); }
        return lista;
    }

    @Override
    public List<Map<String, Object>> obtenerReporteEspecialidades() {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_reporte_especialidades()}");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("especialidad", rs.getString("nombre_especialidad"));
                fila.put("total", rs.getInt("total_citas"));
                fila.put("atendidas", rs.getInt("atendidas"));
                fila.put("pendientes", rs.getInt("pendientes"));
                lista.add(fila);
            }
        } catch (SQLException e) { System.err.println("Error Reportes: " + e.getMessage()); }
        return lista;
    }
}