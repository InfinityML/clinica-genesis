package com.clinica.dao;

import com.clinica.modelo.Medico;
import java.util.List;

public interface MedicoDAO {
    boolean registrar(Medico medico);
    List<Medico> listar();
    boolean actualizar(Medico medico);
    boolean eliminar(int idMedico);
}