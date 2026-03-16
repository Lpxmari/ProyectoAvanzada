package co.edu.uniquindio.proyectoavanzada.entities;

import co.edu.uniquindio.proyectoavanzada.entities.enums.ProgramaAcademico;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estudiantes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Estudiante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreCompleto;
    private String correo;
    private ProgramaAcademico programa;
}