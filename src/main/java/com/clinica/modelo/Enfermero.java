package com.clinica.modelo;

public class Enfermero {
    private int idEnfermero;
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private Integer idUsuario;
    private int estado;

    // Extras para la vista
    private String username;
    private String email;

    public Enfermero() {}

    public int getIdEnfermero() { return idEnfermero; }
    public void setIdEnfermero(int idEnfermero) { this.idEnfermero = idEnfermero; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}