package co.edu.uniquindio.proyectoavanzada.services;

import co.edu.uniquindio.proyectoavanzada.dto.CrearSolicitudDTO;
import co.edu.uniquindio.proyectoavanzada.entities.*;
import java.util.List;

public interface SolicitudService {

    // 1. Crear nueva solicitud (Estudiante)
    Solicitud registrarSolicitud(CrearSolicitudDTO solicitud);

    // 2. Listar solicitudes (Responsable)
    List<Solicitud> listarTodas();

    // 3. Proceso de Triage (Asignar Prioridad)
    Solicitud realizarTriage(Long id, Prioridad prioridad);

    // 4. Asignar Responsable
    Solicitud asignarResponsable(Long idSolicitud, Long idResponsable);

    void cerrarSolicitud(Long id);
}