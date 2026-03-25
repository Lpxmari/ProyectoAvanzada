package co.edu.uniquindio.proyectoavanzada.services;

import co.edu.uniquindio.proyectoavanzada.dto.ResponsableDTO;
import co.edu.uniquindio.proyectoavanzada.entities.Responsable;
import java.util.List;

public interface ResponsableService {

    List<ResponsableDTO> listarActivos();

    ResponsableDTO crearResponsable(Responsable responsable);

    ResponsableDTO obtenerPorId(Long id);

    ResponsableDTO actualizarResponsable(Long id, Responsable responsable);

    void eliminarResponsable(Long id);
}