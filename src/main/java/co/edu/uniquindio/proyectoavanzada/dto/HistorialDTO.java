package co.edu.uniquindio.proyectoavanzada.dto;

import co.edu.uniquindio.proyectoavanzada.entities.enums.EstadoSolicitud;
import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HistorialDTO {
    private Long idHistorial;
    private LocalDateTime fechaHora;
    private EstadoSolicitud estadoAnterior;
    private EstadoSolicitud estadoNuevo;
    private String observaciones;
    private ResponsableDTO responsableAccion;
}