package co.edu.uniquindio.proyectoavanzada.dto;

import co.edu.uniquindio.proyectoavanzada.entities.enums.ProgramaAcademico;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CrearEstudianteDTO(
        @NotBlank String nombreCompleto,
        @Email(message = "El correo debe tener una forma válida")
        @NotBlank(message = "El correo es obligatorio") String correo,
        @NotBlank String username,
        @NotBlank String password,
        @NotNull ProgramaAcademico programa
) {}
