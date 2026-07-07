package com.clinica.dao.impl;

import com.clinica.dao.HistorialDAO;
import com.clinica.util.ConexionDB;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistorialDAOImpl implements HistorialDAO {

    @Override
    public List<Map<String, Object>> obtenerMiHistorial(int idUsuario) {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_ver_mi_historial(?)}")) {
            
            cs.setInt(1, idUsuario);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("fecha", rs.getDate("fecha"));
                    fila.put("hora", rs.getTime("hora"));
                    fila.put("motivo", rs.getString("motivo_consulta"));
                    fila.put("diagnostico", rs.getString("diagnostico"));
                    fila.put("medico", rs.getString("medico"));
                    fila.put("especialidad", rs.getString("especialidad"));
                    fila.put("medicamento", rs.getString("medicamento"));
                    fila.put("dosis", rs.getString("dosis"));
                    fila.put("frecuencia", rs.getString("frecuencia"));
                    fila.put("duracion", rs.getString("duracion"));
                    lista.add(fila);
                }
            }
        } catch (SQLException e) { 
            System.err.println("Error HistorialDAO: " + e.getMessage()); 
        }
        return lista;
    }
}