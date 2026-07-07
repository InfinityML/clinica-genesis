package com.clinica.dao;

import java.util.List;
import java.util.Map;

public interface TriajeDAO {
    int obtenerIdPracticantePorUsuario(int idUsuario);
    int obtenerIdEnfermeroPorUsuario(int idUsuario);
    List<Map<String, Object>> listarCitasParaTriaje();
    boolean registrarTriaje(int idCita, double peso, int talla, double temp,
                            String presion, int fc, String obs,
                            Integer idPracticante, Integer idEnfermero);
}