package co.edu.uniquindio.proyectoavanzada.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "responsables")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Responsable extends Usuario {

    private String nombreCompleto;
    private String cargo;

    private boolean isDeleted;
    private LocalDateTime deletedAt;
}