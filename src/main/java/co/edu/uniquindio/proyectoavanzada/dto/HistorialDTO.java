package co.edu.uniquindio.proyectoavanzada.dto;

import co.edu.uniquindio.proyectoavanzada.entities.enums.EstadoSolicitud;
import java.time.LocalDateTime;

public record HistorialDTO(
    Long idHistorial,
    LocalDateTime fechaHora,
    EstadoSolicitud estadoAnterior,
    EstadoSolicitud estadoNuevo,
    String observaciones,
    ResponsableDTO responsableAccion
) {}
