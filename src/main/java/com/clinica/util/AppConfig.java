package com.clinica.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public final class AppConfig {

    private static final Properties PROPS = new Properties();
    private static volatile boolean cargado = false;

    private AppConfig() {}

    private static synchronized void cargar() {
        if (cargado) return;

        String ruta = System.getProperty("clinica.config");
        if (ruta == null || ruta.trim().isEmpty()) ruta = System.getenv("CLINICA_CONFIG");

        boolean ok = false;
        if (ruta != null && !ruta.trim().isEmpty()) {
            try (InputStream in = new FileInputStream(ruta.trim())) {
                PROPS.load(in);
                ok = true;
                System.out.println("[AppConfig] Configuración cargada desde archivo externo: " + ruta);
            } catch (Exception e) {
                System.err.println("[AppConfig] No se pudo leer el archivo externo (" + ruta + "): " + e.getMessage());
            }
        }

        if (!ok) {
            String[] candidatos = {"config.properties", "config/config.properties"};
            for (String recurso : candidatos) {
                try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream(recurso)) {
                    if (in != null) {
                        PROPS.load(in);
                        ok = true;
                        System.out.println("[AppConfig] Configuración cargada desde el classpath: " + recurso);
                        break;
                    }
                } catch (Exception e) {
                    System.err.println("[AppConfig] Error leyendo " + recurso + ": " + e.getMessage());
                }
            }
        }

        if (!ok) {
            System.err.println("[AppConfig] ADVERTENCIA: no se encontró configuración. "
                    + "Crea config.properties o define -Dclinica.config.");
        }
        cargado = true;
    }

    public static String get(String clave) {
        return get(clave, "");
    }

    public static String get(String clave, String porDefecto) {
        cargar();
        String env = System.getenv(clave.toUpperCase().replace('.', '_'));
        if (env != null && !env.trim().isEmpty()) return env.trim();
        String sys = System.getProperty(clave);
        if (sys != null && !sys.trim().isEmpty()) return sys.trim();
        return PROPS.getProperty(clave, porDefecto);
    }

    public static boolean estaDefinido(String clave) {
        String v = get(clave, "");
        return v != null && !v.trim().isEmpty() && !v.contains("XXXX");
    }
}