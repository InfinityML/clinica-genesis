package com.clinica.modelo;

public class Usuario {
    
    private int idUsuario;
    private String username;
    private String password;
    private String email;
    private int idRol;
    private int estado;

    // Constructor vacío obligatorio para que sea un JavaBean válido
    public Usuario() {
    }

    public Usuario(int idUsuario, String username, String password, String email, int idRol, int estado) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.password = password;
        this.email = email;
        this.idRol = idRol;
        this.estado = estado;
    }

    // GETTERS Y SETTERS

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}