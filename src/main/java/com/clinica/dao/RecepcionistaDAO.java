package com.clinica.dao;

import com.clinica.modelo.Recepcionista;
import java.util.List;

public interface RecepcionistaDAO {
    boolean registrar(Recepcionista recepcionista);
    List<Recepcionista> listar();
    boolean actualizar(Recepcionista recepcionista);
    boolean eliminar(int idRecepcionista);
}