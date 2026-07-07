package com.clinica.dao.impl;

import com.clinica.dao.EvaluacionDAO;
import com.clinica.util.ConexionDB;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvaluacionDAOImpl implements EvaluacionDAO {

    @Override
    public List<Map<String, Object>> listarMisPracticantes(int idUsuarioMedico) {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_listar_mis_practicantes(?)}")) {
            cs.setInt(1, idUsuarioMedico);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("id_practicante", rs.getInt("id_practicante"));
                    fila.put("nombre", rs.getString("nombre"));
                    fila.put("apellido", rs.getString("apellido"));
                    fila.put("codigo", rs.getString("codigo_universitario"));
                    fila.put("evaluaciones", rs.getInt("total_evaluaciones"));
                    lista.add(fila);
                }
            }
        } catch (SQLException e) { System.err.println("Error listar practicantes: " + e.getMessage()); }
        return lista;
    }

    @Override
    public boolean registrarEvaluacion(int idPracticante, int idUsuarioMedico, int puntualidad, int conocimiento, int desempeno, String comentario) {
        boolean exito = false;
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_registrar_evaluacion(?, ?, ?, ?, ?, ?)}")) {
            cs.setInt(1, idPracticante);
            cs.setInt(2, idUsuarioMedico);
            cs.setInt(3, puntualidad);
            cs.setInt(4, conocimiento);
            cs.setInt(5, desempeno);
            cs.setString(6, comentario);
            exito = cs.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Error registrar evaluacion: " + e.getMessage()); }
        return exito;
    }
}