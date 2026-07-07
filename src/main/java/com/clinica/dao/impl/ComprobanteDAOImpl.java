package com.clinica.dao.impl;

import com.clinica.dao.ComprobanteDAO;
import com.clinica.modelo.Comprobante;
import com.clinica.util.ConexionDB;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ComprobanteDAOImpl implements ComprobanteDAO {

    @Override
    public int siguienteCorrelativo(String serie) {
        int sig = 1;
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_siguiente_correlativo(?)}")) {
            cs.setString(1, serie);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) sig = rs.getInt("siguiente");
            }
        } catch (SQLException e) { System.err.println("Error ComprobanteDAO correlativo: " + e.getMessage()); }
        return sig;
    }

    @Override
    public int registrarComprobante(int idPago, String tipo, String serie, int correlativo,
                                    String cliTipoDoc, String cliNumDoc, String cliNombre,
                                    BigDecimal subtotal, BigDecimal igv, BigDecimal total) {
        int id = 0;
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_registrar_comprobante(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {
            cs.setInt(1, idPago);
            cs.setString(2, tipo);
            cs.setString(3, serie);
            cs.setInt(4, correlativo);
            cs.setString(5, cliTipoDoc);
            cs.setString(6, cliNumDoc);
            cs.setString(7, cliNombre);
            cs.setBigDecimal(8, subtotal);
            cs.setBigDecimal(9, igv);
            cs.setBigDecimal(10, total);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) id = rs.getInt("id_comprobante");
            }
        } catch (SQLException e) { System.err.println("Error ComprobanteDAO registrar: " + e.getMessage()); }
        return id;
    }

    @Override
    public boolean actualizarSunat(int idComprobante, String estado, String hash, String pdf, String xml) {
        boolean ok = false;
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_actualizar_comprobante_sunat(?, ?, ?, ?, ?)}")) {
            cs.setInt(1, idComprobante);
            cs.setString(2, estado);
            cs.setString(3, hash);
            cs.setString(4, pdf);
            cs.setString(5, xml);
            ok = cs.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Error ComprobanteDAO actualizar: " + e.getMessage()); }
        return ok;
    }

    @Override
    public Comprobante buscarPorPago(int idPago) {
        try (Connection conn = ConexionDB.getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_buscar_comprobante_por_pago(?)}")) {
            cs.setInt(1, idPago);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    Comprobante c = new Comprobante();
                    c.setIdComprobante(rs.getInt("id_comprobante"));
                    c.setIdPago(rs.getInt("id_pago"));
                    c.setTipoComprobante(rs.getString("tipo_comprobante"));
                    c.setSerie(rs.getString("serie"));
                    c.setCorrelativo(rs.getInt("correlativo"));
                    c.setClienteTipoDoc(rs.getString("cliente_tipo_doc"));
                    c.setClienteNumDoc(rs.getString("cliente_num_doc"));
                    c.setClienteNombre(rs.getString("cliente_nombre"));
                    c.setSubtotal(rs.getBigDecimal("subtotal"));
                    c.setIgv(rs.getBigDecimal("igv"));
                    c.setTotal(rs.getBigDecimal("total"));
                    c.setMoneda(rs.getString("moneda"));
                    c.setEstadoSunat(rs.getString("estado_sunat"));
                    c.setSunatHash(rs.getString("sunat_hash"));
                    c.setEnlacePdf(rs.getString("enlace_pdf"));
                    c.setEnlaceXml(rs.getString("enlace_xml"));
                    c.setFechaEmision(rs.getTimestamp("fecha_emision"));
                    return c;
                }
            }
        } catch (SQLException e) { System.err.println("Error ComprobanteDAO buscar: " + e.getMessage()); }
        return null;
    }
}