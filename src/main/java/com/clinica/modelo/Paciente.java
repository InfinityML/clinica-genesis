package com.clinica.modelo;

import java.sql.Date;

public class Paciente {
    private int idPaciente;
    private String nombre;
    private String apellido;
    private String dni;
    private Date fechaNacimiento;
    private String telefono;
    private Integer idUsuario; // Integer permite valores nulos si se registra sin cuenta web
    private int estado;
    
    // Atributo extra para mostrar en la tabla visual
    private String username;

    public Paciente() {}

    public int getIdPaciente() { return idPaciente; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}