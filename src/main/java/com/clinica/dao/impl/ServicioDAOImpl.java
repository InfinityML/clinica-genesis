package com.clinica.dao.impl;

import com.clinica.dao.ServicioDAO;
import com.clinica.modelo.Servicio;
import com.clinica.util.ConexionDB;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServicioDAOImpl implements ServicioDAO {

    private Servicio mapear(ResultSet rs) throws SQLException {
        Servicio s = new Servicio();
        s.setIdServicio(rs.getInt("id_servicio"));
        s.setNombreServicio(rs.getString("nombre_servicio"));
        s.setDescripcion(rs.getString("descripcion"));
        s.setPrecio(rs.getBigDecimal("precio"));
        s.setAfectoIgv(rs.getInt("afecto_igv") == 1);
        s.setEstado(rs.getInt("estado"));
        return s;
    }

    @Override
    public List<Servicio> listar() {
        List<Servicio> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_listar_servicios()}");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { System.err.println("Error ServicioDAO listar: " + e.getMessage()); }
        return lista;
    }

    @Override
    public Servicio buscar(int idServicio) {
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_listar_servicios()}");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                if (rs.getInt("id_servicio") == idServicio) return mapear(rs);
            }
        } catch (SQLException e) { System.err.println("Error ServicioDAO buscar: " + e.getMessage()); }
        return null;
    }
}