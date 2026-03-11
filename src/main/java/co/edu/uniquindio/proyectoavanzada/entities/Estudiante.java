package co.edu.uniquindio.proyectoavanzada.entities;

import co.edu.uniquindio.proyectoavanzada.entities.enums.ProgramaAcademico;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estudiantes")
@Data // Genera Getters, Setters, toString, equals y hashCode
@NoArgsConstructor // Constructor vacío (obligatorio para JPA)
@AllArgsConstructor // Constructor con todos los campos
@Builder // Permite crear objetos de forma fluida: Estudiante.builder().nombre("...").build()
public class Estudiante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreCompleto;
    private String correo;
    private ProgramaAcademico programa;
}