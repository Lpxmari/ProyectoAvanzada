package co.edu.uniquindio.proyectoavanzada.dto;

import co.edu.uniquindio.proyectoavanzada.entities.enums.TipoSolicitud;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CrearSolicitudDTO {
    private String descripcion;
    private TipoSolicitud tipoSolicitud;
    private String canalOrigen;
    private Long estudianteId;
}
