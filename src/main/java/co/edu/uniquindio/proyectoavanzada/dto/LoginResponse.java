package co.edu.uniquindio.proyectoavanzada.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String nombre;
    private String rol;
}