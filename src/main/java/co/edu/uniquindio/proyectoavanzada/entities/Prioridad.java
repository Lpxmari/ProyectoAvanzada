package co.edu.uniquindio.proyectoavanzada.entities;

import co.edu.uniquindio.proyectoavanzada.entities.enums.NivelSolicitud;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "prioridades")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prioridad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private NivelSolicitud nivel; // ALTA, MEDIA, BAJA
    private String impactoAcademico;
    private String justificacion;
    private LocalDate vigencia;
}