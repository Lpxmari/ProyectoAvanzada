package co.edu.uniquindio.proyectoavanzada.services;

import co.edu.uniquindio.proyectoavanzada.dto.LoginRequest;
import org.springframework.stereotype.Service;

public interface AuthService {

    String login(LoginRequest request);
}