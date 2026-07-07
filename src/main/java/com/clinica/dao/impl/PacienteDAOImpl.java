package com.clinica.dao.impl;

import com.clinica.dao.PacienteDAO;
import com.clinica.modelo.Paciente;
import com.clinica.util.ConexionDB;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAOImpl implements PacienteDAO {

    @Override
    public boolean registrar(Paciente paciente) {
        boolean exito = false;
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_registrar_paciente(?, ?, ?, ?, ?, ?)}")) {
            cs.setString(1, paciente.getNombre());
            cs.setString(2, paciente.getApellido());
            cs.setString(3, paciente.getDni());
            cs.setDate(4, paciente.getFechaNacimiento());
            cs.setString(5, paciente.getTelefono());
            
            if (paciente.getIdUsuario() != null) {
                cs.setInt(6, paciente.getIdUsuario());
            } else {
                cs.setNull(6, java.sql.Types.INTEGER);
            }
            
            exito = cs.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Error en DAO registrar: " + e.getMessage()); }
        return exito;
    }

    @Override
    public List<Paciente> listar() {
        List<Paciente> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_listar_pacientes()}");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                Paciente p = new Paciente();
                p.setIdPaciente(rs.getInt("id_paciente"));
                p.setNombre(rs.getString("nombre"));
                p.setApellido(rs.getString("apellido"));
                p.setDni(rs.getString("dni"));
                p.setFechaNacimiento(rs.getDate("fecha_nacimiento"));
                p.setTelefono(rs.getString("telefono"));
                
                int idUsr = rs.getInt("id_usuario");
                p.setIdUsuario(rs.wasNull() ? null : idUsr);
                p.setUsername(rs.getString("username"));
                p.setEstado(rs.getInt("estado"));
                lista.add(p);
            }
        } catch (SQLException e) { System.err.println("Error en DAO listar: " + e.getMessage()); }
        return lista;
    }

    @Override
    public boolean actualizar(Paciente paciente) {
        boolean exito = false;
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_actualizar_paciente(?, ?, ?, ?, ?, ?)}")) {
            cs.setInt(1, paciente.getIdPaciente());
            cs.setString(2, paciente.getNombre());
            cs.setString(3, paciente.getApellido());
            cs.setString(4, paciente.getDni());
            cs.setDate(5, paciente.getFechaNacimiento());
            cs.setString(6, paciente.getTelefono());
            exito = cs.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Error en DAO actualizar: " + e.getMessage()); }
        return exito;
    }

    @Override
    public boolean eliminar(int idPaciente) {
        boolean exito = false;
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_eliminar_paciente(?)}")) {
            cs.setInt(1, idPaciente);
            exito = cs.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Error en DAO eliminar: " + e.getMessage()); }
        return exito;
    }
}