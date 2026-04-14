package co.edu.uniquindio.proyectoavanzada;

import co.edu.uniquindio.proyectoavanzada.dto.LoginRequest;
import co.edu.uniquindio.proyectoavanzada.entities.Estudiante;
import co.edu.uniquindio.proyectoavanzada.entities.Responsable;
import co.edu.uniquindio.proyectoavanzada.entities.Usuario;
import co.edu.uniquindio.proyectoavanzada.excepciones.RecursoNoEncontradoException;
import co.edu.uniquindio.proyectoavanzada.repositories.UsuarioRepository;
import co.edu.uniquindio.proyectoavanzada.security.JwtUtil;
import co.edu.uniquindio.proyectoavanzada.services.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


//  Cubre: login exitoso, usuario inexistente, inactivo, contraseña incorrecta

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService — Tests Unitarios Completos")
class AuthServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private JwtUtil           jwtUtil;
    @Mock private PasswordEncoder   passwordEncoder;
    @InjectMocks private AuthServiceImpl authService;

    private Usuario usuarioActivo;

    @BeforeEach
    void setUp() {
        usuarioActivo = new Estudiante(); // ← Estudiante para que getRol() devuelva "ROLE_ESTUDIANTE"
        usuarioActivo.setId(1L);
        usuarioActivo.setUsername("mariana.ramirez");
        usuarioActivo.setPassword("$2a$10$hashedPassword");
        usuarioActivo.setActivo(true);
        // ← setRol() eliminado — el rol se infiere automáticamente por instanceof
    }

    @Nested
    @DisplayName("login — casos exitosos")
    class LoginExitosoTests {

        @Test
        @DisplayName("should_retornarToken_when_credencialesValidas")
        void should_retornarToken_when_credencialesValidas() {
            LoginRequest request = new LoginRequest("mariana.ramirez", "pass1234");

            when(usuarioRepository.findByUsername("mariana.ramirez"))
                    .thenReturn(Optional.of(usuarioActivo));
            when(passwordEncoder.matches("pass1234", "$2a$10$hashedPassword"))
                    .thenReturn(true);
            when(jwtUtil.generarToken("mariana.ramirez", "ROLE_ESTUDIANTE"))
                    .thenReturn("eyJhbGciOiJIUzI1NiJ9.token");

            String token = authService.login(request);

            assertNotNull(token);
            assertEquals("eyJhbGciOiJIUzI1NiJ9.token", token);
        }

        @Test
        @DisplayName("should_generarTokenConRolCorrecto_when_usuarioEsResponsable")
        void should_generarTokenConRolCorrecto_when_usuarioEsResponsable() {
            // Arrange — usar Responsable para que getRol() devuelva "ROLE_RESPONSABLE"
            Responsable responsable = new Responsable();
            responsable.setUsername("prof.martinez");
            responsable.setPassword("$2a$10$hash2");
            responsable.setActivo(true);

            when(usuarioRepository.findByUsername("prof.martinez"))
                    .thenReturn(Optional.of(responsable));
            when(passwordEncoder.matches("clave", "$2a$10$hash2")).thenReturn(true);
            when(jwtUtil.generarToken("prof.martinez", "ROLE_RESPONSABLE"))
                    .thenReturn("tokenResponsable");

            // Act & Assert
            assertEquals("tokenResponsable",
                    authService.login(new LoginRequest("prof.martinez", "clave")));
            verify(jwtUtil).generarToken("prof.martinez", "ROLE_RESPONSABLE");
        }

        @Test
        @DisplayName("should_usarPasswordEncoderSiempre_when_loginNuncaCompararEnTextPlano")
        void should_usarPasswordEncoderSiempre_when_loginNuncaCompararEnTextPlano() {
            when(usuarioRepository.findByUsername("mariana.ramirez"))
                    .thenReturn(Optional.of(usuarioActivo));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(jwtUtil.generarToken(any(), any())).thenReturn("token");

            authService.login(new LoginRequest("mariana.ramirez", "pass1234"));

            verify(passwordEncoder).matches(anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("login — usuario no encontrado")
    class LoginUsuarioInexistenteTests {

        @Test
        @DisplayName("should_lanzarRecursoNoEncontrado_when_usernameInexistente")
        void should_lanzarRecursoNoEncontrado_when_usernameInexistente() {
            when(usuarioRepository.findByUsername("noexiste")).thenReturn(Optional.empty());

            assertThrows(RecursoNoEncontradoException.class,
                    () -> authService.login(new LoginRequest("noexiste", "pass")));
        }

        @Test
        @DisplayName("should_mensajeContieneUsername_when_usuarioNoEncontrado")
        void should_mensajeContieneUsername_when_usuarioNoEncontrado() {
            when(usuarioRepository.findByUsername("usuario.inexistente"))
                    .thenReturn(Optional.empty());

            RecursoNoEncontradoException ex = assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> authService.login(
                            new LoginRequest("usuario.inexistente", "pass")));

            assertThat(ex.getMessage()).contains("usuario.inexistente");
        }

        @Test
        @DisplayName("should_noLlamarPasswordEncoder_when_usuarioNoEncontrado")
        void should_noLlamarPasswordEncoder_when_usuarioNoEncontrado() {
            when(usuarioRepository.findByUsername("fantasma")).thenReturn(Optional.empty());

            assertThrows(RecursoNoEncontradoException.class,
                    () -> authService.login(new LoginRequest("fantasma", "pass")));

            verifyNoInteractions(passwordEncoder);
        }
    }

    @Nested
    @DisplayName("login — usuario inactivo")
    class LoginUsuarioInactivoTests {

        @Test
        @DisplayName("should_lanzarRuntimeException_when_usuarioInactivo")
        void should_lanzarRuntimeException_when_usuarioInactivo() {
            usuarioActivo.setActivo(false);
            when(usuarioRepository.findByUsername("mariana.ramirez"))
                    .thenReturn(Optional.of(usuarioActivo));

            assertThrows(RuntimeException.class,
                    () -> authService.login(
                            new LoginRequest("mariana.ramirez", "pass1234")));
        }

        @Test
        @DisplayName("should_noGenerarToken_when_usuarioInactivo")
        void should_noGenerarToken_when_usuarioInactivo() {
            usuarioActivo.setActivo(false);
            when(usuarioRepository.findByUsername("mariana.ramirez"))
                    .thenReturn(Optional.of(usuarioActivo));

            assertThrows(RuntimeException.class,
                    () -> authService.login(
                            new LoginRequest("mariana.ramirez", "pass1234")));

            verifyNoInteractions(jwtUtil);
        }
    }

    @Nested
    @DisplayName("login — contraseña incorrecta")
    class LoginPasswordIncorrectaTests {

        @Test
        @DisplayName("should_lanzarRuntimeException_when_passwordIncorrecta")
        void should_lanzarRuntimeException_when_passwordIncorrecta() {
            when(usuarioRepository.findByUsername("mariana.ramirez"))
                    .thenReturn(Optional.of(usuarioActivo));
            when(passwordEncoder.matches("passwordMal", "$2a$10$hashedPassword"))
                    .thenReturn(false);

            assertThrows(RuntimeException.class,
                    () -> authService.login(
                            new LoginRequest("mariana.ramirez", "passwordMal")));
        }

        @Test
        @DisplayName("should_noGenerarToken_when_passwordIncorrecta")
        void should_noGenerarToken_when_passwordIncorrecta() {
            when(usuarioRepository.findByUsername("mariana.ramirez"))
                    .thenReturn(Optional.of(usuarioActivo));
            when(passwordEncoder.matches("wrongPass", "$2a$10$hashedPassword"))
                    .thenReturn(false);

            assertThrows(RuntimeException.class,
                    () -> authService.login(
                            new LoginRequest("mariana.ramirez", "wrongPass")));

            verifyNoInteractions(jwtUtil);
        }

        @Test
        @DisplayName("should_lanzarExcepcion_when_passwordEsNull")
        void should_lanzarExcepcion_when_passwordEsNull() {
            when(usuarioRepository.findByUsername("mariana.ramirez"))
                    .thenReturn(Optional.of(usuarioActivo));
            when(passwordEncoder.matches(null, "$2a$10$hashedPassword"))
                    .thenReturn(false);

            assertThrows(RuntimeException.class,
                    () -> authService.login(new LoginRequest("mariana.ramirez", null)));
        }
    }
}
