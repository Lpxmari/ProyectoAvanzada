package co.edu.uniquindio.proyectoavanzada;

import co.edu.uniquindio.proyectoavanzada.dto.CrearEstudianteDTO;
import co.edu.uniquindio.proyectoavanzada.dto.EstudianteDTO;
import co.edu.uniquindio.proyectoavanzada.entities.Estudiante;
import co.edu.uniquindio.proyectoavanzada.entities.enums.ProgramaAcademico;
import co.edu.uniquindio.proyectoavanzada.excepciones.RecursoNoEncontradoException;
import co.edu.uniquindio.proyectoavanzada.repositories.EstudianteRepository;
import co.edu.uniquindio.proyectoavanzada.services.impl.EstudianteServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstudianteServiceTest {

    @Mock private EstudianteRepository estudianteRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EstudianteServiceImpl estudianteService;

    // PRUEBA 1: Crear estudiante exitosamente
    @Test
    void crearEstudiante_exitoso() {
        CrearEstudianteDTO dto = new CrearEstudianteDTO(
                "Andrés Torres",
                "andres@uniquindio.edu.co",
                "est001",        // username
                "est123",        // password
                ProgramaAcademico.ING_SISTEMAS
        );

        Estudiante guardado = Estudiante.builder()
                .nombreCompleto("Andrés Torres")
                .correo("andres@uniquindio.edu.co")
                .programa(ProgramaAcademico.ING_SISTEMAS)
                .build();

        when(passwordEncoder.encode("est123")).thenReturn("$2a$hash");
        when(estudianteRepository.save(any())).thenReturn(guardado);

        estudianteService.crearEstudiante(dto);

        verify(estudianteRepository, times(1)).save(any());
    }

    // PRUEBA 2: Obtener estudiante exitosamente
    @Test
    void obtenerEstudiante_exitoso() {
        Estudiante estudiante = Estudiante.builder()
                .nombreCompleto("Laura Gómez")
                .correo("laura@uniquindio.edu.co")
                .programa(ProgramaAcademico.ING_SISTEMAS)
                .build();

        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudiante));

        EstudianteDTO resultado = estudianteService.obtenerEstudiante(1L);

        assertNotNull(resultado);
        assertEquals("Laura Gómez", resultado.nombreCompleto());
    }

    // PRUEBA 3: Obtener estudiante que no existe
    @Test
    void obtenerEstudiante_noExiste_lanzaExcepcion() {
        when(estudianteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () ->
                estudianteService.obtenerEstudiante(99L)
        );
    }

    // PRUEBA 4: Listar estudiantes
    @Test
    void listarEstudiantes_exitoso() {
        Estudiante e1 = Estudiante.builder()
                .nombreCompleto("Andrés Torres")
                .correo("andres@uniquindio.edu.co")
                .programa(ProgramaAcademico.ING_SISTEMAS)
                .build();

        Estudiante e2 = Estudiante.builder()
                .nombreCompleto("Laura Gómez")
                .correo("laura@uniquindio.edu.co")
                .programa(ProgramaAcademico.ING_SISTEMAS)
                .build();

        when(estudianteRepository.findAll()).thenReturn(List.of(e1, e2));

        List<EstudianteDTO> resultado = estudianteService.listarEstudiantes();

        assertEquals(2, resultado.size());
    }

    // PRUEBA 5: Eliminar estudiante que no existe
    @Test
    void eliminarEstudiante_noExiste_lanzaExcepcion() {
        when(estudianteRepository.existsById(99L)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class, () ->
                estudianteService.eliminarEstudiante(99L)
        );
    }
}