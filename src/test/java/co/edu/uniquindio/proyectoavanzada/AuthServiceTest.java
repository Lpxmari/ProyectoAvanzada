package co.edu.uniquindio.proyectoavanzada;

import co.edu.uniquindio.proyectoavanzada.dto.LoginRequest;
import co.edu.uniquindio.proyectoavanzada.entities.Administrador;
import co.edu.uniquindio.proyectoavanzada.entities.Usuario;
import co.edu.uniquindio.proyectoavanzada.excepciones.RecursoNoEncontradoException;
import co.edu.uniquindio.proyectoavanzada.repositories.UsuarioRepository;
import co.edu.uniquindio.proyectoavanzada.security.JwtUtil;
import co.edu.uniquindio.proyectoavanzada.services.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private JwtUtil jwtUtil;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    // PRUEBA 1: Login exitoso
    @Test
    void login_exitoso() {
        Usuario usuario = Administrador.builder()
                .username("admin")
                .password("$2a$10$hash")
                .activo(true)
                .nombreCompleto("Administrador")
                .build();

        LoginRequest request = new LoginRequest("admin", "admin123");

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("admin123", "$2a$10$hash")).thenReturn(true);
        when(jwtUtil.generarToken(any(), any())).thenReturn("token.jwt.generado");

        String token = authService.login(request);

        assertNotNull(token);
        assertEquals("token.jwt.generado", token);
    }

    // PRUEBA 2: Usuario no encontrado
    @Test
    void login_usuarioNoExiste_lanzaExcepcion() {
        LoginRequest request = new LoginRequest("noexiste", "123456");

        when(usuarioRepository.findByUsername("noexiste")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () ->
                authService.login(request)
        );
    }

    // PRUEBA 3: Contraseña incorrecta
    @Test
    void login_contrasenaIncorrecta_lanzaExcepcion() {
        Usuario usuario = Administrador.builder()
                .username("admin")
                .password("$2a$10$hash")
                .activo(true)
                .nombreCompleto("Administrador")
                .build();

        LoginRequest request = new LoginRequest("admin", "incorrecta");

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("incorrecta", "$2a$10$hash")).thenReturn(false);

        assertThrows(RuntimeException.class, () ->
                authService.login(request)
        );
    }

    // PRUEBA 4: Usuario inactivo
    @Test
    void login_usuarioInactivo_lanzaExcepcion() {
        Usuario usuario = Administrador.builder()
                .username("admin")
                .password("$2a$10$hash")
                .activo(false)
                .nombreCompleto("Administrador")
                .build();

        LoginRequest request = new LoginRequest("admin", "admin123");

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));

        assertThrows(RuntimeException.class, () ->
                authService.login(request)
        );
    }
}