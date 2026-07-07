package com.clinica.dao.impl;

import com.clinica.dao.DashboardDAO;
import com.clinica.util.ConexionDB;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DashboardDAOImpl implements DashboardDAO {

    @Override
    public Map<String, Integer> obtenerEstadisticas() {
        Map<String, Integer> stats = new HashMap<>();
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_estadisticas_dashboard()}");
             ResultSet rs = cs.executeQuery()) {
            
            if (rs.next()) {
                stats.put("usuarios", rs.getInt("total_usuarios"));
                stats.put("citasHoy", rs.getInt("citas_hoy"));
                stats.put("triajes", rs.getInt("triajes_pendientes"));
            }
        } catch (SQLException e) { 
            System.err.println("Error DashboardDAO: " + e.getMessage()); 
        }
        return stats;
    }
}