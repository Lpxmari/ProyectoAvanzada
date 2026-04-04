package co.edu.uniquindio.proyectoavanzada.entities;

import co.edu.uniquindio.proyectoavanzada.entities.enums.ProgramaAcademico;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "estudiantes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Estudiante extends Usuario {

    private String nombreCompleto;
    private String correo;
    private ProgramaAcademico programa;
}