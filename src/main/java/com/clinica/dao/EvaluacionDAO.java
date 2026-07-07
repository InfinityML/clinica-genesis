package com.clinica.dao;

import java.util.List;
import java.util.Map;

public interface EvaluacionDAO {
    List<Map<String, Object>> listarMisPracticantes(int idUsuarioMedico);
    boolean registrarEvaluacion(int idPracticante, int idUsuarioMedico, int puntualidad, int conocimiento, int desempeno, String comentario);
}