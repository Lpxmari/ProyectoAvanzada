package co.edu.uniquindio.proyectoavanzada.services;

import co.edu.uniquindio.proyectoavanzada.dto.LoginRequest;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public String login(LoginRequest request) {
        // Aquí irá la lógica de Spring Security más adelante
        if ("admin".equals(request.getUsername()) && "1234".equals(request.getPassword())) {
            return "TOKEN_JWT_SIMULADO";
        }
        throw new RuntimeException("Credenciales inválidas");
    }
}