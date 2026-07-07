package com.clinica.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static Connection conexion = null;

    // Se leen de la configuración externa (config.properties / env / -D...), no del código.
    private static final String URL = AppConfig.get("db.url",
            "jdbc:mysql://localhost:3306/clinica_universitaria?useSSL=false&serverTimezone=UTC");
    private static final String USER = AppConfig.get("db.user", "root");
    private static final String PASSWORD = AppConfig.get("db.password", "root");

    private ConexionDB() {
    }

    public static Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexion = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[Arquitectura] Conexión a MySQL establecida correctamente.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el Driver de MySQL. Revisa el pom.xml.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error de conexión a la Base de Datos: " + e.getMessage());
            e.printStackTrace();
        }
        return conexion;
    }

    public static void cerrarConexion(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}