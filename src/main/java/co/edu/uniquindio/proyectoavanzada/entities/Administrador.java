package co.edu.uniquindio.proyectoavanzada.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

// Entidad que representa al administrador del sistema
// Hereda username, password, rol y activo de Usuario
// El administrador puede: gestionar responsables, asignar solicitudes,
// priorizar, cerrar y ver todo el sistema
@Entity
@Table(name = "administradores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Administrador extends Usuario {

    // Nombre completo del administrador para identificarlo
    private String nombreCompleto;
}