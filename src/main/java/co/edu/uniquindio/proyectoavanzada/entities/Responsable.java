package co.edu.uniquindio.proyectoavanzada.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "responsables")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Responsable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreCompleto;
    private String cargo;
    private boolean activo;
}