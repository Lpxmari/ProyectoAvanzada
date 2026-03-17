package co.edu.uniquindio.proyectoavanzada.dto;

import co.edu.uniquindio.proyectoavanzada.entities.enums.TipoSolicitud;

public record CrearSolicitudDTO(
    String descripcion,
    TipoSolicitud tipoSolicitud,
    String canalOrigen,
    Long estudianteId
) {}
