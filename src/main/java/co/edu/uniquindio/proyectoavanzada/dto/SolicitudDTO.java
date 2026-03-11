package co.edu.uniquindio.proyectoavanzada.dto;


import co.edu.uniquindio.proyectoavanzada.entities.enums.EstadoSolicitud;
import co.edu.uniquindio.proyectoavanzada.entities.enums.TipoSolicitud;
import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SolicitudDTO {
    private Long id;
    private String descripcion;
    private LocalDateTime fechaHoraRegistro;
    private LocalDateTime fechaCierre;
    private EstadoSolicitud estado;
    private TipoSolicitud tipoSolicitud;
    private EstudianteDTO estudiante;
    private ResponsableDTO responsableAsignado;
    private PrioridadDTO prioridad;
}