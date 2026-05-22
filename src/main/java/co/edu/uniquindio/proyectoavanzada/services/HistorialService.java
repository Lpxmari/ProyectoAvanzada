package co.edu.uniquindio.proyectoavanzada.services;

import co.edu.uniquindio.proyectoavanzada.dto.HistorialDTO;

import java.util.List;

public interface HistorialService {

    List<HistorialDTO> obtenerHistorialEstudiante(
            Integer estudianteId,
            Integer solicitudId
    );

}