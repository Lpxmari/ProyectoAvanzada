package co.edu.uniquindio.proyectoavanzada.dto;

import co.edu.uniquindio.proyectoavanzada.entities.enums.EstadoSolicitud;
import co.edu.uniquindio.proyectoavanzada.entities.enums.TipoSolicitud;
import java.time.LocalDateTime;

public record SolicitudDTO(
    Long id,
    String descripcion,
    LocalDateTime fechaHoraRegistro,
    LocalDateTime fechaCierre,
    EstadoSolicitud estado,
    TipoSolicitud tipoSolicitud,
    EstudianteDTO estudiante,
    ResponsableDTO responsableAsignado,
    PrioridadDTO prioridad
) {}
