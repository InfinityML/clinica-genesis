package com.clinica.dao.impl;

import com.clinica.dao.CitaDAO;
import com.clinica.util.ConexionDB;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CitaDAOImpl implements CitaDAO {

    @Override
    public int obtenerIdPacientePorUsuario(int idUsuario) {
        int idPaciente = 0;
        String sql = "SELECT id_paciente FROM paciente WHERE id_usuario = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idPaciente = rs.getInt("id_paciente");
                }
            }
        } catch (SQLException e) { 
            System.err.println("Error al buscar ID de Paciente: " + e.getMessage()); 
        }
        return idPaciente;
    }

    @Override
    public boolean registrarCita(int idPaciente, int idMedico, String fecha, String hora, String modalidad) {
        boolean exito = false;
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_registrar_cita(?, ?, ?, ?, ?)}")) {
            cs.setInt(1, idPaciente);
            cs.setInt(2, idMedico);
            cs.setString(3, fecha);
            cs.setString(4, hora);
            cs.setString(5, modalidad);
            
            exito = cs.executeUpdate() > 0;
        } catch (SQLException e) { 
            System.err.println("Error CitaDAO registrar: " + e.getMessage()); 
        }
        return exito;
    }

    @Override
    public List<Map<String, Object>> listarMisCitas(int idUsuarioPaciente) {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_mis_citas(?)}")) {
            cs.setInt(1, idUsuarioPaciente);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("id_cita", rs.getInt("id_cita"));
                    fila.put("fecha", rs.getDate("fecha_cita"));
                    fila.put("hora", rs.getTime("hora_cita"));
                    fila.put("modalidad", rs.getString("tipo_modalidad"));
                    fila.put("estado", rs.getString("estado_cita"));
                    fila.put("confirmada", rs.getInt("confirmada"));
                    fila.put("link", rs.getString("link_teleconsulta"));
                    fila.put("medico", rs.getString("medico"));
                    fila.put("especialidad", rs.getString("nombre_especialidad"));
                    lista.add(fila);
                }
            }
        } catch (SQLException e) { 
            System.err.println("Error CitaDAO listar mis citas: " + e.getMessage()); 
        }
        return lista;
    }
    
    @Override
    public List<Map<String, Object>> listarTodas() {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_listar_citas()}");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("id_cita", rs.getInt("id_cita"));
                fila.put("fecha", rs.getDate("fecha_cita"));
                fila.put("hora", rs.getTime("hora_cita"));
                fila.put("modalidad", rs.getString("tipo_modalidad"));
                fila.put("estado", rs.getString("estado_cita"));
                fila.put("paciente", rs.getString("paciente"));
                fila.put("dni", rs.getString("dni"));
                fila.put("medico", rs.getString("medico"));
                lista.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("Error CitaDAO listar todas: " + e.getMessage());
        }
        return lista;
    }
    
    @Override
    public Map<String, Object> buscarCitaParaVideollamada(int idCita) {
        String sql = "SELECT c.id_cita, c.fecha_cita, c.hora_cita, c.tipo_modalidad, c.confirmada, "
                   + "p.id_paciente, CONCAT(p.nombre,' ',p.apellido) AS paciente, p.id_usuario AS usuario_paciente, "
                   + "m.id_medico, CONCAT(m.nombre,' ',m.apellido) AS medico, m.id_usuario AS usuario_medico, "
                   + "IFNULL(hc.id_historia, 0) AS id_historia, "
                   + "t.peso_kg, t.talla_cm, t.temperatura_c, t.presion_arterial, t.frecuencia_cardiaca_lpm, t.observacion AS obs_triaje "
                   + "FROM cita c "
                   + "INNER JOIN paciente p ON p.id_paciente = c.id_paciente "
                   + "INNER JOIN medico   m ON m.id_medico   = c.id_medico "
                   + "LEFT JOIN historia_clinica hc ON p.id_paciente = hc.id_paciente "
                   + "LEFT JOIN triaje t ON c.id_cita = t.id_cita "
                   + "WHERE c.id_cita = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCita);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> f = new HashMap<>();
                    f.put("id_cita", rs.getInt("id_cita"));
                    f.put("fecha", rs.getDate("fecha_cita"));
                    f.put("hora", rs.getTime("hora_cita"));
                    f.put("modalidad", rs.getString("tipo_modalidad"));
                    f.put("confirmada", rs.getInt("confirmada"));
                    f.put("id_paciente", rs.getInt("id_paciente"));
                    f.put("paciente", rs.getString("paciente"));
                    f.put("usuario_paciente", rs.getInt("usuario_paciente"));
                    f.put("id_medico", rs.getInt("id_medico"));
                    f.put("medico", rs.getString("medico"));
                    f.put("usuario_medico", rs.getInt("usuario_medico"));
                    f.put("id_historia", rs.getInt("id_historia"));
                    f.put("peso", rs.getObject("peso_kg"));
                    f.put("talla", rs.getObject("talla_cm"));
                    f.put("temp", rs.getObject("temperatura_c"));
                    f.put("presion", rs.getString("presion_arterial"));
                    f.put("fc", rs.getObject("frecuencia_cardiaca_lpm"));
                    f.put("obs_triaje", rs.getString("obs_triaje"));
                    return f;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error CitaDAO videollamada: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public boolean confirmarCita(int idCita) {
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_confirmar_cita(?)}")) {
            cs.setInt(1, idCita);
            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error confirmarCita: " + e.getMessage());
            return false;
        }
    }
}