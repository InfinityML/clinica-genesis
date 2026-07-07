package com.clinica.dao;

import java.util.List;
import java.util.Map;

public interface AtencionDAO {
    boolean registrarAtencion(int idCita, int idHistoria, String motivo, String diagnostico, 
                              int idMedico, Integer idMedicamento, String dosis, 
                              String frecuencia, String duracion);
    List<Map<String, Object>> listarAgendaMedico(int idUsuarioMedico);
    List<Map<String, Object>> listarMedicamentos();
    int crearHistoriaClinica(int idPaciente);
}