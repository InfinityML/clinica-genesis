package com.clinica.modelo;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Pago {
    private int idPago;
    private int idCita;
    private int idServicio;
    private BigDecimal monto;
    private String metodoPago;      // Culqui | Efectivo
    private String estadoPago;      // Pendiente | Pagado | Anulado
    private String culquiToken;
    private String culquiChargeId;
    private Timestamp fechaPago;
    private Integer idRecepcionista;

    // Extras para la vista
    private String nombreServicio;
    private boolean afectoIgv;
    private String pacienteNombre;
    private String pacienteApellido;

    public Pago() {}

    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }
    public int getIdCita() { return idCita; }
    public void setIdCita(int idCita) { this.idCita = idCita; }
    public int getIdServicio() { return idServicio; }
    public void setIdServicio(int idServicio) { this.idServicio = idServicio; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }
    public String getCulquiToken() { return culquiToken; }
    public void setCulquiToken(String culquiToken) { this.culquiToken = culquiToken; }
    public String getCulquiChargeId() { return culquiChargeId; }
    public void setCulquiChargeId(String culquiChargeId) { this.culquiChargeId = culquiChargeId; }
    public Timestamp getFechaPago() { return fechaPago; }
    public void setFechaPago(Timestamp fechaPago) { this.fechaPago = fechaPago; }
    public Integer getIdRecepcionista() { return idRecepcionista; }
    public void setIdRecepcionista(Integer idRecepcionista) { this.idRecepcionista = idRecepcionista; }
    public String getNombreServicio() { return nombreServicio; }
    public void setNombreServicio(String nombreServicio) { this.nombreServicio = nombreServicio; }
    public boolean isAfectoIgv() { return afectoIgv; }
    public void setAfectoIgv(boolean afectoIgv) { this.afectoIgv = afectoIgv; }
    public String getPacienteNombre() { return pacienteNombre; }
    public void setPacienteNombre(String pacienteNombre) { this.pacienteNombre = pacienteNombre; }
    public String getPacienteApellido() { return pacienteApellido; }
    public void setPacienteApellido(String pacienteApellido) { this.pacienteApellido = pacienteApellido; }
}