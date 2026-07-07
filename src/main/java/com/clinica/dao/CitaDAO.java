package com.clinica.dao;

import java.util.List;
import java.util.Map;

public interface CitaDAO {
    // Método clave: Busca qué paciente es el dueño del usuario logueado
    int obtenerIdPacientePorUsuario(int idUsuario);

    // Registra la cita
    boolean registrarCita(int idPaciente, int idMedico, String fecha, String hora, String modalidad);

    // Lista el historial para la tabla del paciente
    List<Map<String, Object>> listarMisCitas(int idUsuarioPaciente);

    // Lista TODAS las citas activas (para el portal de recepción)
    List<Map<String, Object>> listarTodas();
    
    // Datos + dueños (usuarios) de una cita, para validar el acceso a la videollamada
    Map<String, Object> buscarCitaParaVideollamada(int idCita);
    
    // Confirmación de teleconsulta por el médico
    boolean confirmarCita(int idCita);
}