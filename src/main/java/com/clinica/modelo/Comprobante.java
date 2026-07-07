package com.clinica.modelo;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Comprobante {
    private int idComprobante;
    private int idPago;
    private String tipoComprobante;  // Boleta | Factura
    private String serie;
    private int correlativo;
    private String clienteTipoDoc;   // DNI | RUC
    private String clienteNumDoc;
    private String clienteNombre;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private String moneda;
    private String estadoSunat;      // Pendiente | Aceptado | Rechazado
    private String sunatHash;
    private String enlacePdf;
    private String enlaceXml;
    private Timestamp fechaEmision;

    public Comprobante() {}

    public int getIdComprobante() { return idComprobante; }
    public void setIdComprobante(int idComprobante) { this.idComprobante = idComprobante; }
    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }
    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante = tipoComprobante; }
    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }
    public int getCorrelativo() { return correlativo; }
    public void setCorrelativo(int correlativo) { this.correlativo = correlativo; }
    public String getClienteTipoDoc() { return clienteTipoDoc; }
    public void setClienteTipoDoc(String clienteTipoDoc) { this.clienteTipoDoc = clienteTipoDoc; }
    public String getClienteNumDoc() { return clienteNumDoc; }
    public void setClienteNumDoc(String clienteNumDoc) { this.clienteNumDoc = clienteNumDoc; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getIgv() { return igv; }
    public void setIgv(BigDecimal igv) { this.igv = igv; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public String getEstadoSunat() { return estadoSunat; }
    public void setEstadoSunat(String estadoSunat) { this.estadoSunat = estadoSunat; }
    public String getSunatHash() { return sunatHash; }
    public void setSunatHash(String sunatHash) { this.sunatHash = sunatHash; }
    public String getEnlacePdf() { return enlacePdf; }
    public void setEnlacePdf(String enlacePdf) { this.enlacePdf = enlacePdf; }
    public String getEnlaceXml() { return enlaceXml; }
    public void setEnlaceXml(String enlaceXml) { this.enlaceXml = enlaceXml; }
    public Timestamp getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(Timestamp fechaEmision) { this.fechaEmision = fechaEmision; }
}