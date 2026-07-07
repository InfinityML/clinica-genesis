package com.clinica.dao;

import com.clinica.modelo.Practicante;
import java.util.List;

public interface PracticanteDAO {
    boolean registrar(Practicante practicante);
    List<Practicante> listar();
    boolean actualizar(Practicante practicante);
    boolean eliminar(int idPracticante);
}