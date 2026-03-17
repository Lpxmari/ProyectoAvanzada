package co.edu.uniquindio.proyectoavanzada.services.impl;

import co.edu.uniquindio.proyectoavanzada.dto.LoginRequest;
import co.edu.uniquindio.proyectoavanzada.services.AuthService;
import org.springframework.stereotype.Service;


@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public String login(LoginRequest request) {

        // Aquí irá la lógica de Spring Security más adelante
        if ("admin".equals(request.username()) && "1234".equals(request.password())) {
            return "TOKEN_JWT_SIMULADO";
        }
        throw new RuntimeException("Credenciales inválidas");
    }
}
