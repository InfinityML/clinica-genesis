package com.clinica.dao.impl;

import com.clinica.dao.UsuarioDAO;
import com.clinica.modelo.Usuario;
import com.clinica.util.ConexionDB;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public Usuario validarLogin(String username, String password) {
        Usuario usuario = null;
        Connection conn = null;
        CallableStatement cs = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConexion();
            // Llamamos al SP por username
            cs = conn.prepareCall("{CALL sp_buscar_usuario_username(?)}");
            cs.setString(1, username);
            rs = cs.executeQuery();

            if (rs.next()) {
                String hashBD = rs.getString("password");
                // Comparamos la contraseña en texto plano con el Hash de la BD
                if (BCrypt.checkpw(password, hashBD)) {
                    usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setUsername(rs.getString("username"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setIdRol(rs.getInt("id_rol"));
                    usuario.setEstado(rs.getInt("estado"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en validarLogin: " + e.getMessage());
        } finally {
            // Buena práctica: cerrar recursos (Clean Code)
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (cs != null) cs.close(); } catch (SQLException e) {}
            // No cerramos la conexion porque usamos Singleton
        }
        return usuario; // Retorna null si las credenciales son incorrectas
    }

    @Override
    public boolean registrar(Usuario usuario) {
        boolean registrado = false;
        Connection conn = null;
        CallableStatement cs = null;

        try {
            conn = ConexionDB.getConexion();
            cs = conn.prepareCall("{CALL sp_registrar_usuario(?, ?, ?, ?)}");
            cs.setString(1, usuario.getUsername());
            
            // Encriptamos la contraseña antes de guardarla
            String hashPassword = BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt());
            cs.setString(2, hashPassword);
            
            cs.setString(3, usuario.getEmail());
            cs.setInt(4, usuario.getIdRol());

            registrado = cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en registrar usuario: " + e.getMessage());
        } finally {
            try { if (cs != null) cs.close(); } catch (SQLException e) {}
        }
        return registrado;
    }

    @Override
    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        Connection conn = null;
        CallableStatement cs = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConexion();
            cs = conn.prepareCall("{CALL sp_listar_usuarios()}");
            rs = cs.executeQuery();

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                u.setIdRol(rs.getInt("id_rol"));
                u.setEstado(rs.getInt("estado"));
                lista.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (cs != null) cs.close(); } catch (SQLException e) {}
        }
        return lista;
    }

    @Override
    public Usuario buscarPorId(int idUsuario) {
        Usuario usuario = null;
        Connection conn = null;
        CallableStatement cs = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConexion();
            cs = conn.prepareCall("{CALL sp_buscar_usuario_id(?)}");
            cs.setInt(1, idUsuario);
            rs = cs.executeQuery();

            if (rs.next()) {
                usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setUsername(rs.getString("username"));
                usuario.setEmail(rs.getString("email"));
                usuario.setIdRol(rs.getInt("id_rol"));
                usuario.setEstado(rs.getInt("estado"));
            }
        } catch (SQLException e) {
            System.err.println("Error en buscarPorId: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (cs != null) cs.close(); } catch (SQLException e) {}
        }
        return usuario;
    }

    @Override
    public boolean actualizar(Usuario usuario) {
        boolean actualizado = false;
        Connection conn = null;
        CallableStatement cs = null;

        try {
            conn = ConexionDB.getConexion();
            cs = conn.prepareCall("{CALL sp_actualizar_usuario(?, ?, ?, ?)}");
            cs.setInt(1, usuario.getIdUsuario());
            cs.setString(2, usuario.getUsername());
            cs.setString(3, usuario.getEmail());
            cs.setInt(4, usuario.getIdRol());

            actualizado = cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
        } finally {
            try { if (cs != null) cs.close(); } catch (SQLException e) {}
        }
        return actualizado;
    }

    @Override
    public boolean eliminar(int idUsuario) {
        boolean eliminado = false;
        Connection conn = null;
        CallableStatement cs = null;

        try {
            conn = ConexionDB.getConexion();
            cs = conn.prepareCall("{CALL sp_eliminar_usuario(?)}");
            cs.setInt(1, idUsuario);

            eliminado = cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
        } finally {
            try { if (cs != null) cs.close(); } catch (SQLException e) {}
        }
        return eliminado;
    }
}