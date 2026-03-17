package co.edu.uniquindio.proyectoavanzada.dto;


import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record LoginRequest(
        @NotBlank String username,
        @NotBlank @Length(min = 5) String password
) {
}