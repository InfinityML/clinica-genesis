package com.clinica.dao;

import com.clinica.modelo.Enfermero;
import java.util.List;

public interface EnfermeroDAO {
    boolean registrar(Enfermero enfermero);
    List<Enfermero> listar();
    boolean actualizar(Enfermero enfermero);
    boolean eliminar(int idEnfermero);
}