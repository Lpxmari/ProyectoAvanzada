package co.edu.uniquindio.proyectoavanzada.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrioridadDTO {
    private String nivel; // ALTA, MEDIA, BAJA
    private String impactoAcademico;
    private String justificacion;
    private LocalDate vigencia;
}