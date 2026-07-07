package com.clinica.modelo;

public class Medico {
    private int idMedico;
    private String nombre;
    private String apellido;
    private String dni;
    private int idEspecialidad;
    private int idUsuario;
    private int estado;
    
    // Atributos extra para mostrar en la tabla web (No existen nativamente en la tabla medico)
    private String nombreEspecialidad;
    private String username;

    public Medico() {}

    // Getters y Setters
    public int getIdMedico() { return idMedico; }
    public void setIdMedico(int idMedico) { this.idMedico = idMedico; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public int getIdEspecialidad() { return idEspecialidad; }
    public void setIdEspecialidad(int idEspecialidad) { this.idEspecialidad = idEspecialidad; }
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
    public String getNombreEspecialidad() { return nombreEspecialidad; }
    public void setNombreEspecialidad(String nombreEspecialidad) { this.nombreEspecialidad = nombreEspecialidad; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}