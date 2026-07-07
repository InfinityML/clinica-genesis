package com.clinica.dao.impl;

import com.clinica.dao.PracticanteDAO;
import com.clinica.modelo.Practicante;
import com.clinica.util.ConexionDB;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PracticanteDAOImpl implements PracticanteDAO {

    @Override
    public boolean registrar(Practicante p) {
        boolean exito = false;
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_registrar_practicante(?, ?, ?, ?, ?, ?)}")) {
            cs.setString(1, p.getNombre());
            cs.setString(2, p.getApellido());
            cs.setString(3, p.getDni());
            cs.setString(4, p.getCodigoUniversitario());
            
            if (p.getIdMedicoTutor() != null) cs.setInt(5, p.getIdMedicoTutor());
            else cs.setNull(5, java.sql.Types.INTEGER);
            
            if (p.getIdUsuario() != null) cs.setInt(6, p.getIdUsuario());
            else cs.setNull(6, java.sql.Types.INTEGER);
            
            exito = cs.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Error PracticanteDAO registrar: " + e.getMessage()); }
        return exito;
    }

    @Override
    public List<Practicante> listar() {
        List<Practicante> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_listar_practicantes()}");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                Practicante p = new Practicante();
                p.setIdPracticante(rs.getInt("id_practicante"));
                p.setNombre(rs.getString("nombre"));
                p.setApellido(rs.getString("apellido"));
                p.setDni(rs.getString("dni"));
                p.setCodigoUniversitario(rs.getString("codigo_universitario"));
                
                int idTutor = rs.getInt("id_medico_tutor");
                p.setIdMedicoTutor(rs.wasNull() ? null : idTutor);
                p.setNombreTutor(rs.getString("nombre_tutor"));
                p.setApellidoTutor(rs.getString("apellido_tutor"));
                
                int idUsr = rs.getInt("id_usuario");
                p.setIdUsuario(rs.wasNull() ? null : idUsr);
                p.setUsername(rs.getString("username"));
                
                p.setEstado(rs.getInt("estado"));
                lista.add(p);
            }
        } catch (SQLException e) { System.err.println("Error PracticanteDAO listar: " + e.getMessage()); }
        return lista;
    }

    @Override
    public boolean actualizar(Practicante p) {
        boolean exito = false;
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_actualizar_practicante(?, ?, ?, ?, ?, ?)}")) {
            cs.setInt(1, p.getIdPracticante());
            cs.setString(2, p.getNombre());
            cs.setString(3, p.getApellido());
            cs.setString(4, p.getDni());
            cs.setString(5, p.getCodigoUniversitario());
            
            if (p.getIdMedicoTutor() != null) cs.setInt(6, p.getIdMedicoTutor());
            else cs.setNull(6, java.sql.Types.INTEGER);
            
            exito = cs.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Error PracticanteDAO actualizar: " + e.getMessage()); }
        return exito;
    }

    @Override
    public boolean eliminar(int idPracticante) {
        boolean exito = false;
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_eliminar_practicante(?)}")) {
            cs.setInt(1, idPracticante);
            exito = cs.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Error PracticanteDAO eliminar: " + e.getMessage()); }
        return exito;
    }
}