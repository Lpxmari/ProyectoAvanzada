package co.edu.uniquindio.proyectoavanzada.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstudianteDTO {
    private Long id;
    private String nombreCompleto;
    private String correo;
    private String programa;
}