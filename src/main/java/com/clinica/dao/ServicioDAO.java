package com.clinica.dao;

import com.clinica.modelo.Servicio;
import java.util.List;

public interface ServicioDAO {
    List<Servicio> listar();
    Servicio buscar(int idServicio);
}