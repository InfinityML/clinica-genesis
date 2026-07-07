package com.clinica.dao;

import com.clinica.modelo.Usuario;
import java.util.List;

/**
 * Interfaz que define los métodos estándar (CRUD y operaciones de negocio) 
 * para la entidad Usuario, garantizando el bajo acoplamiento.
 */
public interface UsuarioDAO {
    
    // Operación de Negocio: Autenticación
    Usuario validarLogin(String username, String password);
    
    // Operaciones CRUD estándar
    boolean registrar(Usuario usuario);
    List<Usuario> listar();
    Usuario buscarPorId(int idUsuario);
    boolean actualizar(Usuario usuario);
    boolean eliminar(int idUsuario); // Se usará para el Soft Delete (cambiar estado a 0)
    
}