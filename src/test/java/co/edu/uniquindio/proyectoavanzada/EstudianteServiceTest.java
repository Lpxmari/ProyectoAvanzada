package co.edu.uniquindio.proyectoavanzada;

import co.edu.uniquindio.proyectoavanzada.dto.CrearEstudianteDTO;
import co.edu.uniquindio.proyectoavanzada.dto.EstudianteDTO;
import co.edu.uniquindio.proyectoavanzada.entities.Estudiante;
import co.edu.uniquindio.proyectoavanzada.entities.enums.ProgramaAcademico;
import co.edu.uniquindio.proyectoavanzada.excepciones.RecursoNoEncontradoException;
import co.edu.uniquindio.proyectoavanzada.repositories.EstudianteRepository;
import co.edu.uniquindio.proyectoavanzada.services.impl.EstudianteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


//  Cubre: crear, actualizar, eliminar, obtener y listar estudiantes


@ExtendWith(MockitoExtension.class)
@DisplayName("EstudianteService — Tests Unitarios Completos")
class EstudianteServiceTest {

    @Mock private EstudianteRepository estudianteRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private EstudianteServiceImpl estudianteService;

    private Estudiante estudianteExistente;
    private CrearEstudianteDTO crearDTO;
    private EstudianteDTO actualizarDTO;

    @BeforeEach
    void setUp() {
        estudianteExistente = new Estudiante();
        estudianteExistente.setId(3456L);
        estudianteExistente.setNombreCompleto("Mariana Ramírez");
        estudianteExistente.setCorreo("mariana@uniquindio.edu.co");
        estudianteExistente.setPrograma(ProgramaAcademico.ING_SISTEMAS);

        crearDTO = new CrearEstudianteDTO(
                "Mariana Ramírez",
                "mariana@uniquindio.edu.co",
                "mariana.ramirez",        // ← username
                "pass1234",
                ProgramaAcademico.ING_SISTEMAS
        );

        actualizarDTO = new EstudianteDTO(3456L,
                "Valentina N. Actualizada",
                "valentina.nueva@uniquindio.edu.co",
                ProgramaAcademico.MEDICINA);
    }



    //  crearEstudiante

    @Nested
    @DisplayName("crearEstudiante")
    class CrearEstudianteTests {

        @Test
        @DisplayName("should_retornarId_when_datosValidos")
        void should_retornarId_when_datosValidos() {
            Estudiante guardado = new Estudiante();
            guardado.setId(5555L);
            when(passwordEncoder.encode("pass1234")).thenReturn("hashedPass");
            when(estudianteRepository.save(any())).thenReturn(guardado);

            assertEquals(5555L, estudianteService.crearEstudiante(crearDTO));
            verify(estudianteRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("should_cifrarPassword_when_crearEstudiante")
        void should_cifrarPassword_when_crearEstudiante() {
            ArgumentCaptor<Estudiante> captor = ArgumentCaptor.forClass(Estudiante.class);
            Estudiante guardado = new Estudiante();
            guardado.setId(3456L);

            when(passwordEncoder.encode("pass1234")).thenReturn("$2a$10$hashedValue");
            when(estudianteRepository.save(captor.capture())).thenReturn(guardado);

            estudianteService.crearEstudiante(crearDTO);

            assertNotEquals("pass1234", captor.getValue().getPassword(),
                    "La contraseña NO debe guardarse en texto plano");
            verify(passwordEncoder, times(1)).encode("pass1234");
        }

        @Test
        @DisplayName("should_mapearCamposCorrectamente_when_crearEstudiante")
        void should_mapearCamposCorrectamente_when_crearEstudiante() {
            ArgumentCaptor<Estudiante> captor = ArgumentCaptor.forClass(Estudiante.class);
            Estudiante guardado = new Estudiante();
            guardado.setId(1234L);
            when(passwordEncoder.encode(anyString())).thenReturn("hashed");
            when(estudianteRepository.save(captor.capture())).thenReturn(guardado);

            estudianteService.crearEstudiante(crearDTO);


            Estudiante capturado = captor.getValue();

            assertAll(
                    () -> assertEquals(crearDTO.nombreCompleto(), capturado.getNombreCompleto()),
                    () -> assertEquals(crearDTO.correo(),         capturado.getCorreo()),
                    () -> assertEquals(crearDTO.programa(),       capturado.getPrograma())
            );
        }
    }



    // actualizarEstudiante

    @Nested
    @DisplayName("actualizarEstudiante")
    class ActualizarEstudianteTests {

        @Test
        @DisplayName("should_actualizarNombreCorreo_when_datosNuevosValidos")
        void should_actualizarNombreCorreo_when_datosNuevosValidos() {
            EstudianteDTO nuevos = new EstudianteDTO(3456L,
                    "Mariana Ramírez Colorado",
                    "mariana.colorado@uniquindio.edu.co",
                    ProgramaAcademico.ING_SISTEMAS);

            when(estudianteRepository.findById(3456L))
                    .thenReturn(Optional.of(estudianteExistente));
            when(estudianteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            estudianteService.actualizarEstudiante(3456L, nuevos);

            assertAll(
                    () -> assertEquals("Mariana Ramírez Colorado",
                            estudianteExistente.getNombreCompleto()),
                    () -> assertEquals("mariana.colorado@uniquindio.edu.co",
                            estudianteExistente.getCorreo())
            );
        }

        @Test
        @DisplayName("should_cambiarPrograma_when_estudianteCambiaCarrera")
        void should_cambiarPrograma_when_estudianteCambiaCarrera() {
            EstudianteDTO nuevos = new EstudianteDTO(3456L,
                    "Mariana Ramírez",
                    "mariana@uniquindio.edu.co",
                    ProgramaAcademico.MEDICINA);

            when(estudianteRepository.findById(3456L))
                    .thenReturn(Optional.of(estudianteExistente));
            when(estudianteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            estudianteService.actualizarEstudiante(3456L, nuevos);

            assertEquals(ProgramaAcademico.MEDICINA, estudianteExistente.getPrograma());
        }

        @Test
        @DisplayName("should_persistirCambios_when_actualizarEstudiante")
        void should_persistirCambios_when_actualizarEstudiante() {
            when(estudianteRepository.findById(1L))
                    .thenReturn(Optional.of(estudianteExistente));
            when(estudianteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            estudianteService.actualizarEstudiante(1L, actualizarDTO);

            verify(estudianteRepository, times(1)).save(estudianteExistente);
        }

        @Test
        @DisplayName("should_lanzarRecursoNoEncontrado_when_idInexistente")
        void should_lanzarRecursoNoEncontrado_when_idInexistente() {
            when(estudianteRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(RecursoNoEncontradoException.class,
                    () -> estudianteService.actualizarEstudiante(999L, actualizarDTO));
            verify(estudianteRepository, never()).save(any());
        }

        @Test
        @DisplayName("should_noActualizarPassword_when_actualizarEstudiante")
        void should_noActualizarPassword_when_actualizarEstudiante() {
            when(estudianteRepository.findById(1234L))
                    .thenReturn(Optional.of(estudianteExistente));
            when(estudianteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            estudianteService.actualizarEstudiante(1234L, actualizarDTO);

            verifyNoInteractions(passwordEncoder);
        }
    }



    // eliminarEstudiante

    @Nested
    @DisplayName("eliminarEstudiante")
    class EliminarEstudianteTests {

        @Test
        @DisplayName("should_eliminarEstudiante_when_idExistente")
        void should_eliminarEstudiante_when_idExistente() {
            when(estudianteRepository.existsById(1234L)).thenReturn(true);

            estudianteService.eliminarEstudiante(1234L);

            verify(estudianteRepository, times(1)).deleteById(1234L);
        }

        @Test
        @DisplayName("should_lanzarRecursoNoEncontrado_when_idInexistenteEnEliminar")
        void should_lanzarRecursoNoEncontrado_when_idInexistenteEnEliminar() {
            when(estudianteRepository.existsById(999L)).thenReturn(false);

            assertThrows(RecursoNoEncontradoException.class,
                    () -> estudianteService.eliminarEstudiante(999L));
            verify(estudianteRepository, never()).deleteById(any());
        }
    }



    // obtenerEstudiante

    @Nested
    @DisplayName("obtenerEstudiante")
    class ObtenerEstudianteTests {

        @Test
        @DisplayName("should_retornarDTO_when_idExistente")
        void should_retornarDTO_when_idExistente() {
            when(estudianteRepository.findById(1L))
                    .thenReturn(Optional.of(estudianteExistente));

            EstudianteDTO resultado = estudianteService.obtenerEstudiante(1L);

            assertAll(
                    () -> assertEquals(estudianteExistente.getId(),
                            resultado.id()),
                    () -> assertEquals(estudianteExistente.getNombreCompleto(),
                            resultado.nombreCompleto()),
                    () -> assertEquals(estudianteExistente.getCorreo(),
                            resultado.correo()),
                    () -> assertEquals(estudianteExistente.getPrograma(),
                            resultado.programa())
            );
        }

        @Test
        @DisplayName("should_lanzarRecursoNoEncontrado_when_idInexistenteEnObtener")
        void should_lanzarRecursoNoEncontrado_when_idInexistenteEnObtener() {
            when(estudianteRepository.findById(4049L)).thenReturn(Optional.empty());

            RecursoNoEncontradoException ex = assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> estudianteService.obtenerEstudiante(4049L));
            assertThat(ex.getMessage()).containsIgnoringCase("estudiante");
        }
    }



    // listarEstudiantes

    @Nested
    @DisplayName("listarEstudiantes")
    class ListarEstudiantesTests {

        @Test
        @DisplayName("should_retornarListaVacia_when_noHayEstudiantes")
        void should_retornarListaVacia_when_noHayEstudiantes() {
            when(estudianteRepository.findAll()).thenReturn(List.of());

            assertTrue(estudianteService.listarEstudiantes().isEmpty());
        }

        @Test
        @DisplayName("should_retornarListaConTresEstudiantes_when_hayTresRegistros")
        void should_retornarListaConTresEstudiantes_when_hayTresRegistros() {
            Estudiante e1 = crearEstudiante(3456L, "Mariana Ramírez",
                    "mariana@uniquindio.edu.co",   ProgramaAcademico.ING_SISTEMAS);
            Estudiante e2 = crearEstudiante(1234L, "Yamileth Londoño",
                    "jhaineth@uniquindio.edu.co",  ProgramaAcademico.ING_SISTEMAS);
            Estudiante e3 = crearEstudiante(7891L, "Andrés Ospina",
                    "andres@uniquindio.edu.co",    ProgramaAcademico.MEDICINA);

            when(estudianteRepository.findAll()).thenReturn(List.of(e1, e2, e3));

            List<EstudianteDTO> resultado = estudianteService.listarEstudiantes();

            assertAll(
                    () -> assertEquals(3, resultado.size()),
                    () -> assertEquals("Mariana Ramírez",  resultado.get(0).nombreCompleto()),
                    () -> assertEquals("Yamileth Londoño", resultado.get(1).nombreCompleto()),
                    () -> assertEquals("Andrés Ospina",
                            resultado.get(2).nombreCompleto())
            );
        }

        @Test
        @DisplayName("should_mapearProgramaAcademico_when_listarEstudiantes")
        void should_mapearProgramaAcademico_when_listarEstudiantes() {
            Estudiante e = crearEstudiante(1234L, "Yamileth Londoño",
                    "yamileth@uniquindio.edu.co", ProgramaAcademico.MEDICINA);
            when(estudianteRepository.findAll()).thenReturn(List.of(e));

            assertEquals(ProgramaAcademico.MEDICINA,
                    estudianteService.listarEstudiantes().get(0).programa());
        }

        @Test
        @DisplayName("should_mapearCorreoCorrectamente_when_listarEstudiantes")
        void should_mapearCorreoCorrectamente_when_listarEstudiantes() {
            Estudiante e = crearEstudiante(1L, "Mariana Ramírez",
                    "mariana@uniquindio.edu.co", ProgramaAcademico.ING_SISTEMAS);
            when(estudianteRepository.findAll()).thenReturn(List.of(e));

            assertEquals("mariana@uniquindio.edu.co",
                    estudianteService.listarEstudiantes().get(0).correo());
        }
    }



    // helper (Este es un método privado que fabrica objetos Estudiante
    //  listos para usar en las pruebas sin necesidad de repetir su
    //  construcción escribiendo las mismas líneas cada vez que necesite uno.
    //  Solo es un atajo que optimiza el código)

    private Estudiante crearEstudiante(Long id, String nombre,
                                       String correo, ProgramaAcademico programa) {
        Estudiante e = new Estudiante();
        e.setId(id);
        e.setNombreCompleto(nombre);
        e.setCorreo(correo);
        e.setPrograma(programa);
        return e;
    }
}
