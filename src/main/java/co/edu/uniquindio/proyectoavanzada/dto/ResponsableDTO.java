package co.edu.uniquindio.proyectoavanzada.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponsableDTO {
    private Long id;
    private String nombreCompleto;
    private String cargo;
    private boolean activo;
}