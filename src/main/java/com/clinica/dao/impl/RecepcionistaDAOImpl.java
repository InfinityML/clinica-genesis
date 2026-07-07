package com.clinica.dao.impl;

import com.clinica.dao.RecepcionistaDAO;
import com.clinica.modelo.Recepcionista;
import com.clinica.util.ConexionDB;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecepcionistaDAOImpl implements RecepcionistaDAO {

    @Override
    public boolean registrar(Recepcionista e) {
        boolean exito = false;
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_registrar_recepcionista(?, ?, ?, ?, ?)}")) {
            cs.setString(1, e.getNombre());
            cs.setString(2, e.getApellido());
            cs.setString(3, e.getDni());
            cs.setString(4, e.getTelefono());
            if (e.getIdUsuario() != null) cs.setInt(5, e.getIdUsuario());
            else cs.setNull(5, java.sql.Types.INTEGER);
            exito = cs.executeUpdate() > 0;
        } catch (SQLException ex) { System.err.println("Error RecepcionistaDAO registrar: " + ex.getMessage()); }
        return exito;
    }

    @Override
    public List<Recepcionista> listar() {
        List<Recepcionista> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_listar_recepcionistas()}");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                Recepcionista e = new Recepcionista();
                e.setIdRecepcionista(rs.getInt("id_recepcionista"));
                e.setNombre(rs.getString("nombre"));
                e.setApellido(rs.getString("apellido"));
                e.setDni(rs.getString("dni"));
                e.setTelefono(rs.getString("telefono"));
                int idUsr = rs.getInt("id_usuario");
                e.setIdUsuario(rs.wasNull() ? null : idUsr);
                e.setEstado(rs.getInt("estado"));
                e.setUsername(rs.getString("username"));
                e.setEmail(rs.getString("email"));
                lista.add(e);
            }
        } catch (SQLException ex) { System.err.println("Error RecepcionistaDAO listar: " + ex.getMessage()); }
        return lista;
    }

    @Override
    public boolean actualizar(Recepcionista e) {
        boolean exito = false;
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_actualizar_recepcionista(?, ?, ?, ?, ?)}")) {
            cs.setInt(1, e.getIdRecepcionista());
            cs.setString(2, e.getNombre());
            cs.setString(3, e.getApellido());
            cs.setString(4, e.getDni());
            cs.setString(5, e.getTelefono());
            exito = cs.executeUpdate() > 0;
        } catch (SQLException ex) { System.err.println("Error RecepcionistaDAO actualizar: " + ex.getMessage()); }
        return exito;
    }

    @Override
    public boolean eliminar(int idRecepcionista) {
        boolean exito = false;
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_eliminar_recepcionista(?)}")) {
            cs.setInt(1, idRecepcionista);
            exito = cs.executeUpdate() > 0;
        } catch (SQLException ex) { System.err.println("Error RecepcionistaDAO eliminar: " + ex.getMessage()); }
        return exito;
    }
}