package co.edu.uniquindio.proyectoavanzada.services;

import co.edu.uniquindio.proyectoavanzada.dto.CrearSolicitudDTO;
import co.edu.uniquindio.proyectoavanzada.entities.*;
import co.edu.uniquindio.proyectoavanzada.entities.enums.EstadoSolicitud;

import java.util.List;

public interface SolicitudService {

    Solicitud registrarSolicitud(CrearSolicitudDTO solicitud);

    List<Solicitud> listarTodas();

    Solicitud realizarTriage(Long id, Prioridad prioridad);

    Solicitud asignarResponsable(Long idSolicitud, Long idResponsable);

    void cerrarSolicitud(Long id);

    Solicitud obtenerPorId(Long id);

    List<Solicitud> listarPorEstado(EstadoSolicitud estado);

    List<Historial> obtenerHistorial(Long idSolicitud);

    List<Solicitud> listarPorEstudiante(Long estudianteId);
}