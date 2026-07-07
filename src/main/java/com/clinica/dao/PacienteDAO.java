package com.clinica.dao;

import com.clinica.modelo.Paciente;
import java.util.List;

public interface PacienteDAO {
    boolean registrar(Paciente paciente);
    List<Paciente> listar();
    boolean actualizar(Paciente paciente);
    boolean eliminar(int idPaciente);
}