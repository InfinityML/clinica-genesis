package com.clinica.modelo;

public class Practicante {
    private int idPracticante;
    private String nombre;
    private String apellido;
    private String dni;
    private String codigoUniversitario;
    private Integer idMedicoTutor; // Integer por si se guarda temporalmente sin tutor
    private Integer idUsuario;
    private int estado;
    
    // Atributos extra para la vista (DataTables)
    private String nombreTutor;
    private String apellidoTutor;
    private String username;

    public Practicante() {}

    // Getters y Setters
    public int getIdPracticante() { return idPracticante; }
    public void setIdPracticante(int idPracticante) { this.idPracticante = idPracticante; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getCodigoUniversitario() { return codigoUniversitario; }
    public void setCodigoUniversitario(String codigoUniversitario) { this.codigoUniversitario = codigoUniversitario; }
    public Integer getIdMedicoTutor() { return idMedicoTutor; }
    public void setIdMedicoTutor(Integer idMedicoTutor) { this.idMedicoTutor = idMedicoTutor; }
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
    public String getNombreTutor() { return nombreTutor; }
    public void setNombreTutor(String nombreTutor) { this.nombreTutor = nombreTutor; }
    public String getApellidoTutor() { return apellidoTutor; }
    public void setApellidoTutor(String apellidoTutor) { this.apellidoTutor = apellidoTutor; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}