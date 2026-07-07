package com.clinica.dao.impl;

import com.clinica.dao.PagoDAO;
import com.clinica.modelo.Pago;
import com.clinica.util.ConexionDB;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PagoDAOImpl implements PagoDAO {

    @Override
    public int registrarPago(int idCita, int idServicio, BigDecimal monto, String metodo, Integer idRecepcionista) {
        int idPago = 0;
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_registrar_pago(?, ?, ?, ?, ?)}")) {
            cs.setInt(1, idCita);
            cs.setInt(2, idServicio);
            cs.setBigDecimal(3, monto);
            cs.setString(4, metodo);
            if (idRecepcionista != null) cs.setInt(5, idRecepcionista); else cs.setNull(5, Types.INTEGER);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) idPago = rs.getInt("id_pago");
            }
        } catch (SQLException e) { System.err.println("Error PagoDAO registrar: " + e.getMessage()); }
        return idPago;
    }

    @Override
    public boolean confirmarPago(int idPago, String token, String chargeId) {
        boolean ok = false;
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_confirmar_pago(?, ?, ?)}")) {
            cs.setInt(1, idPago);
            cs.setString(2, token);
            cs.setString(3, chargeId);
            ok = cs.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Error PagoDAO confirmar: " + e.getMessage()); }
        return ok;
    }

    @Override
    public boolean anularPago(int idPago) {
        boolean ok = false;
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_anular_pago(?)}")) {
            cs.setInt(1, idPago);
            ok = cs.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Error PagoDAO anular: " + e.getMessage()); }
        return ok;
    }

    @Override
    public Pago buscarPago(int idPago) {
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_buscar_pago(?)}")) {
            cs.setInt(1, idPago);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    Pago p = new Pago();
                    p.setIdPago(rs.getInt("id_pago"));
                    p.setIdCita(rs.getInt("id_cita"));
                    p.setIdServicio(rs.getInt("id_servicio"));
                    p.setMonto(rs.getBigDecimal("monto"));
                    p.setMetodoPago(rs.getString("metodo_pago"));
                    p.setEstadoPago(rs.getString("estado_pago"));
                    p.setNombreServicio(rs.getString("nombre_servicio"));
                    p.setAfectoIgv(rs.getInt("afecto_igv") == 1);
                    return p;
                }
            }
        } catch (SQLException e) { System.err.println("Error PagoDAO buscar: " + e.getMessage()); }
        return null;
    }

    @Override
    public List<Map<String, Object>> listarPagos() {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_listar_pagos()}");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> f = new HashMap<>();
                f.put("id_pago", rs.getInt("id_pago"));
                f.put("id_cita", rs.getInt("id_cita"));
                f.put("monto", rs.getBigDecimal("monto"));
                f.put("metodo", rs.getString("metodo_pago"));
                f.put("estado", rs.getString("estado_pago"));
                f.put("fecha", rs.getTimestamp("fecha_pago"));
                f.put("servicio", rs.getString("nombre_servicio"));
                f.put("paciente_nombre", rs.getString("paciente_nombre"));
                f.put("paciente_apellido", rs.getString("paciente_apellido"));
                lista.add(f);
            }
        } catch (SQLException e) { System.err.println("Error PagoDAO listar: " + e.getMessage()); }
        return lista;
    }
}