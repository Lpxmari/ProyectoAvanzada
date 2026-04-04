package co.edu.uniquindio.proyectoavanzada.services.impl;

import co.edu.uniquindio.proyectoavanzada.dto.LoginRequest;
import co.edu.uniquindio.proyectoavanzada.entities.Usuario;
import co.edu.uniquindio.proyectoavanzada.excepciones.RecursoNoEncontradoException;
import co.edu.uniquindio.proyectoavanzada.repositories.UsuarioRepository;
import co.edu.uniquindio.proyectoavanzada.security.JwtUtil;
import co.edu.uniquindio.proyectoavanzada.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    // Repositorio para buscar el usuario en la base de datos
    private final UsuarioRepository usuarioRepository;

    // Utilidad para generar el token JWT
    private final JwtUtil jwtUtil;

    // Para comparar la contraseña ingresada con la cifrada en BD
    private final PasswordEncoder passwordEncoder;

    @Override
    public String login(LoginRequest request) {

        // Buscamos el usuario por username en la base de datos
        Usuario usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado: " + request.username()));

        // Verificamos que el usuario esté activo
        if (!usuario.isActivo()) {
            throw new RuntimeException("Usuario inactivo");
        }

        // Comparamos la contraseña ingresada con la cifrada en BD
        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // Si todo está bien, generamos y retornamos el token JWT
        return jwtUtil.generarToken(
                usuario.getUsername(),
                usuario.getRol().name()
        );
    }
}
