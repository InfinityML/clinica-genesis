package com.clinica.dao.impl;

import com.clinica.dao.AtencionDAO;
import com.clinica.util.ConexionDB;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AtencionDAOImpl implements AtencionDAO {

    @Override
    public boolean registrarAtencion(int idCita, int idHistoria, String motivo, String diagnostico, 
                                     int idMedico, Integer idMedicamento, String dosis, 
                                     String frecuencia, String duracion) {
        boolean exito = false;
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_registrar_atencion_medica(?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {
            cs.setInt(1, idCita);
            cs.setInt(2, idHistoria);
            cs.setString(3, motivo);
            cs.setString(4, diagnostico);
            cs.setInt(5, idMedico);
            
            if (idMedicamento != null && idMedicamento > 0) {
                cs.setInt(6, idMedicamento);
                cs.setString(7, dosis);
                cs.setString(8, frecuencia);
                cs.setString(9, duracion);
            } else {
                cs.setNull(6, java.sql.Types.INTEGER);
                cs.setNull(7, java.sql.Types.VARCHAR);
                cs.setNull(8, java.sql.Types.VARCHAR);
                cs.setNull(9, java.sql.Types.VARCHAR);
            }
            exito = cs.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Error Transacción AtencionDAO: " + e.getMessage()); }
        return exito;
    }

    @Override
    public List<Map<String, Object>> listarAgendaMedico(int idUsuarioMedico) {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_agenda_medico(?)}")) {
            cs.setInt(1, idUsuarioMedico);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("id_cita", rs.getInt("id_cita"));
                    fila.put("fecha", rs.getDate("fecha_cita"));
                    fila.put("hora", rs.getTime("hora_cita"));
                    
                    fila.put("modalidad", rs.getString("tipo_modalidad"));
                    fila.put("confirmada", rs.getInt("confirmada"));
                    fila.put("link", rs.getString("link_teleconsulta"));
                    
                    fila.put("id_paciente", rs.getInt("id_paciente"));
                    fila.put("paciente", rs.getString("paciente"));
                    fila.put("id_historia", rs.getInt("id_historia"));
                    fila.put("id_medico", rs.getInt("id_medico"));
                    
                    // Datos de Triaje 
                    fila.put("peso", rs.getString("peso_kg") != null ? rs.getString("peso_kg") : "N/A");
                    fila.put("talla", rs.getString("talla_cm") != null ? rs.getString("talla_cm") : "N/A");
                    fila.put("temp", rs.getString("temperatura_c") != null ? rs.getString("temperatura_c") : "N/A");
                    fila.put("presion", rs.getString("presion_arterial") != null ? rs.getString("presion_arterial") : "N/A");
                    fila.put("fc", rs.getString("frecuencia_cardiaca_lpm") != null ? rs.getString("frecuencia_cardiaca_lpm") : "N/A");
                    fila.put("obs_triaje", rs.getString("obs_triaje") != null ? rs.getString("obs_triaje") : "Sin observaciones.");
                    
                    lista.add(fila);
                }
            }
        } catch (SQLException e) { System.err.println("Error AgendaDAO: " + e.getMessage()); }
        return lista;
    }

    @Override
    public List<Map<String, Object>> listarMedicamentos() {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_listar_medicamentos()}");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("id_medicamento", rs.getInt("id_medicamento"));
                fila.put("medicamento", rs.getString("medicamento"));
                lista.add(fila);
            }
        } catch (SQLException e) { System.err.println("Error MedicamentosDAO: " + e.getMessage()); }
        return lista;
    }

    @Override
    public int crearHistoriaClinica(int idPaciente) {
        int idGenerado = 0;
        String sql = "INSERT INTO historia_clinica (id_paciente) VALUES (?)";
        try (Connection conn = ConexionDB.getConexion();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idPaciente);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) idGenerado = rs.getInt(1);
            }
        } catch (SQLException e) { System.err.println("Error Crear Historia: " + e.getMessage()); }
        return idGenerado;
    }
}