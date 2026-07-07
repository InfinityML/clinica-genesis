package com.clinica.modelo;

import java.math.BigDecimal;

public class Servicio {
    private int idServicio;
    private String nombreServicio;
    private String descripcion;
    private BigDecimal precio;
    private boolean afectoIgv;
    private int estado;

    public Servicio() {}

    public int getIdServicio() { return idServicio; }
    public void setIdServicio(int idServicio) { this.idServicio = idServicio; }
    public String getNombreServicio() { return nombreServicio; }
    public void setNombreServicio(String nombreServicio) { this.nombreServicio = nombreServicio; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public boolean isAfectoIgv() { return afectoIgv; }
    public void setAfectoIgv(boolean afectoIgv) { this.afectoIgv = afectoIgv; }
    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
}