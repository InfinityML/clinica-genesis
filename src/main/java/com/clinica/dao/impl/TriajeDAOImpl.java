package com.clinica.dao.impl;

import com.clinica.dao.TriajeDAO;
import com.clinica.util.ConexionDB;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TriajeDAOImpl implements TriajeDAO {

    @Override
    public int obtenerIdPracticantePorUsuario(int idUsuario) {
        int id = 0;
        String sql = "SELECT id_practicante FROM practicante WHERE id_usuario = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) id = rs.getInt("id_practicante");
            }
        } catch (SQLException e) { System.err.println("Error TriajeDAO ID Practicante: " + e.getMessage()); }
        return id;
    }

    @Override
    public int obtenerIdEnfermeroPorUsuario(int idUsuario) {
        int id = 0;
        String sql = "SELECT id_enfermero FROM enfermero WHERE id_usuario = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) id = rs.getInt("id_enfermero");
            }
        } catch (SQLException e) { System.err.println("Error TriajeDAO ID Enfermero: " + e.getMessage()); }
        return id;
    }

    @Override
    public List<Map<String, Object>> listarCitasParaTriaje() {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_citas_pendientes_triaje()}");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("id_cita", rs.getInt("id_cita"));
                fila.put("fecha", rs.getDate("fecha_cita"));
                fila.put("hora", rs.getTime("hora_cita"));
                fila.put("paciente", rs.getString("paciente"));
                fila.put("dni", rs.getString("dni"));
                fila.put("medico", rs.getString("medico"));
                lista.add(fila);
            }
        } catch (SQLException e) { System.err.println("Error TriajeDAO Listar: " + e.getMessage()); }
        return lista;
    }

    @Override
    public boolean registrarTriaje(int idCita, double peso, int talla, double temp,
                                   String presion, int fc, String obs,
                                   Integer idPracticante, Integer idEnfermero) {
        boolean exito = false;
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_registrar_triaje(?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {
            cs.setInt(1, idCita);
            cs.setDouble(2, peso);
            cs.setInt(3, talla);
            cs.setDouble(4, temp);
            cs.setString(5, presion);
            cs.setInt(6, fc);
            cs.setString(7, obs);
            if (idPracticante != null) cs.setInt(8, idPracticante); else cs.setNull(8, Types.INTEGER);
            if (idEnfermero != null)   cs.setInt(9, idEnfermero);   else cs.setNull(9, Types.INTEGER);
            exito = cs.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Error TriajeDAO Registrar: " + e.getMessage()); }
        return exito;
    }
}