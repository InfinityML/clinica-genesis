package com.clinica.dao.impl;

import com.clinica.dao.MedicoDAO;
import com.clinica.modelo.Medico;
import com.clinica.util.ConexionDB;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedicoDAOImpl implements MedicoDAO {

    @Override
    public boolean registrar(Medico medico) {
        boolean exito = false;
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_registrar_medico(?, ?, ?, ?, ?)}")) {
            cs.setString(1, medico.getNombre());
            cs.setString(2, medico.getApellido());
            cs.setString(3, medico.getDni());
            cs.setInt(4, medico.getIdEspecialidad());
            cs.setInt(5, medico.getIdUsuario());
            exito = cs.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Error MedicoDAO registrar: " + e.getMessage()); }
        return exito;
    }

    @Override
    public List<Medico> listar() {
        List<Medico> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_listar_medicos()}");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                Medico m = new Medico();
                m.setIdMedico(rs.getInt("id_medico"));
                m.setNombre(rs.getString("nombre"));
                m.setApellido(rs.getString("apellido"));
                m.setDni(rs.getString("dni"));
                m.setIdEspecialidad(rs.getInt("id_especialidad"));
                m.setNombreEspecialidad(rs.getString("nombre_especialidad"));
                m.setIdUsuario(rs.getInt("id_usuario"));
                m.setUsername(rs.getString("username"));
                m.setEstado(rs.getInt("estado"));
                lista.add(m);
            }
        } catch (SQLException e) { System.err.println("Error MedicoDAO listar: " + e.getMessage()); }
        return lista;
    }

    @Override
    public boolean actualizar(Medico medico) {
        boolean exito = false;
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_actualizar_medico(?, ?, ?, ?, ?)}")) {
            cs.setInt(1, medico.getIdMedico());
            cs.setString(2, medico.getNombre());
            cs.setString(3, medico.getApellido());
            cs.setString(4, medico.getDni());
            cs.setInt(5, medico.getIdEspecialidad());
            exito = cs.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Error MedicoDAO actualizar: " + e.getMessage()); }
        return exito;
    }

    @Override
    public boolean eliminar(int idMedico) {
        boolean exito = false;
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_eliminar_medico(?)}")) {
            cs.setInt(1, idMedico);
            exito = cs.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Error MedicoDAO eliminar: " + e.getMessage()); }
        return exito;
    }
}