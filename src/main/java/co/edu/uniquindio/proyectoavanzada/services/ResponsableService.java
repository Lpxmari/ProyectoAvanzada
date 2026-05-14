package co.edu.uniquindio.proyectoavanzada.services;

import co.edu.uniquindio.proyectoavanzada.dto.ResponsableDTO;
import java.util.List;

public interface ResponsableService {
    List<ResponsableDTO> listarActivos();
    ResponsableDTO crearResponsable(ResponsableDTO responsableDTO);
    ResponsableDTO obtenerPorId(Long id);
    ResponsableDTO actualizarResponsable(Long id, ResponsableDTO responsableDTO);
    void eliminarResponsable(Long id);
}