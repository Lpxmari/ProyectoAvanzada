package co.edu.uniquindio.proyectoavanzada.services;

import co.edu.uniquindio.proyectoavanzada.dto.CrearSolicitudDTO;
import co.edu.uniquindio.proyectoavanzada.dto.HistorialDTO;
import co.edu.uniquindio.proyectoavanzada.dto.PrioridadDTO;
import co.edu.uniquindio.proyectoavanzada.dto.SolicitudDTO;
import co.edu.uniquindio.proyectoavanzada.entities.*;
import co.edu.uniquindio.proyectoavanzada.entities.enums.EstadoSolicitud;
import co.edu.uniquindio.proyectoavanzada.dto.CierreDTO;

import java.util.List;

public interface SolicitudService {

    Solicitud registrarSolicitud(CrearSolicitudDTO solicitud);

    List<SolicitudDTO> listarTodas();

    Solicitud realizarTriage(Long id, PrioridadDTO prioridad);

    Solicitud asignarResponsable(Long idSolicitud, Long idResponsable);

    void cerrarSolicitud(Long id, CierreDTO cierreDTO);

    SolicitudDTO obtenerPorId(Long id);

    List<SolicitudDTO> listarPorEstado(EstadoSolicitud estado);

    List<HistorialDTO> obtenerHistorial(Long idSolicitud);

    List<SolicitudDTO> listarPorEstudiante(Long estudianteId);

    Solicitud marcarComoAtendida(Long id, String observaciones);

    List<SolicitudDTO> listarPorResponsable(Long responsableId);
}