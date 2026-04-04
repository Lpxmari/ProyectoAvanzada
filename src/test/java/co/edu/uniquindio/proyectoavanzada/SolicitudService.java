package co.edu.uniquindio.proyectoavanzada;

import co.edu.uniquindio.proyectoavanzada.dto.CrearSolicitudDTO;
import co.edu.uniquindio.proyectoavanzada.entities.*;
import co.edu.uniquindio.proyectoavanzada.entities.enums.EstadoSolicitud;
import co.edu.uniquindio.proyectoavanzada.entities.enums.TipoSolicitud;
import co.edu.uniquindio.proyectoavanzada.excepciones.RecursoNoEncontradoException;
import co.edu.uniquindio.proyectoavanzada.repositories.*;
import co.edu.uniquindio.proyectoavanzada.services.impl.SolicitudServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitudServiceTest {

    @Mock private SolicitudRepository solicitudRepository;
    @Mock private ResponsableRepository responsableRepository;
    @Mock private HistorialRepository historialRepository;
    @Mock private EstudianteRepository estudianteRepository;

    @InjectMocks
    private SolicitudServiceImpl solicitudService;

    // PRUEBA 1: Registrar solicitud exitosamente
    @Test
    void registrarSolicitud_exitoso() {
        // Preparamos el estudiante simulado
        Estudiante estudiante = new Estudiante();
        estudiante.setNombreCompleto("Andrés Torres");

        // Preparamos el DTO de entrada
        CrearSolicitudDTO dto = new CrearSolicitudDTO(
                "Necesito homologar una materia",
                TipoSolicitud.HOMOLOGACION,
                "correo",
                1L
        );

        // Preparamos la solicitud que retornará el repositorio
        Solicitud solicitudGuardada = Solicitud.builder()
                .descripcion(dto.descripcion())
                .tipo(dto.tipoSolicitud())
                .estado(EstadoSolicitud.REGISTRADA)
                .estudiante(estudiante)
                .build();

        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudiante));
        when(solicitudRepository.save(any(Solicitud.class))).thenReturn(solicitudGuardada);

        // Ejecutamos
        Solicitud resultado = solicitudService.registrarSolicitud(dto);

        // Verificamos
        assertNotNull(resultado);
        assertEquals(EstadoSolicitud.REGISTRADA, resultado.getEstado());
        assertEquals(TipoSolicitud.HOMOLOGACION, resultado.getTipo());
    }

    // PRUEBA 2: Registrar solicitud con estudiante inexistente
    @Test
    void registrarSolicitud_estudianteNoExiste_lanzaExcepcion() {
        CrearSolicitudDTO dto = new CrearSolicitudDTO(
                "Solicitud de cupos",
                TipoSolicitud.CUPOS,
                "presencial",
                99L
        );

        when(estudianteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> {
            solicitudService.registrarSolicitud(dto);
        });
    }

    // PRUEBA 3: Cerrar solicitud exitosamente
    @Test
    void cerrarSolicitud_exitoso() {
        Solicitud solicitud = Solicitud.builder()
                .estado(EstadoSolicitud.ATENDIDA)
                .build();

        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(solicitudRepository.save(any())).thenReturn(solicitud);
        when(historialRepository.save(any())).thenReturn(null);

        solicitudService.cerrarSolicitud(1L);

        assertEquals(EstadoSolicitud.CERRADA, solicitud.getEstado());
        assertNotNull(solicitud.getFechaCierre());
    }

    // PRUEBA 4: Cerrar solicitud que no está ATENDIDA — debe fallar
    @Test
    void cerrarSolicitud_estadoInvalido_lanzaExcepcion() {
        Solicitud solicitud = Solicitud.builder()
                .estado(EstadoSolicitud.REGISTRADA)
                .build();

        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));

        assertThrows(IllegalStateException.class, () -> {
            solicitudService.cerrarSolicitud(1L);
        });
    }

    // PRUEBA 5: Asignar responsable inactivo — debe fallar
    @Test
    void asignarResponsable_inactivo_lanzaExcepcion() {
        Solicitud solicitud = Solicitud.builder()
                .estado(EstadoSolicitud.CLASIFICADA)
                .build();

        Responsable responsableInactivo = new Responsable();
        responsableInactivo.setActivo(false);

        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(responsableRepository.findById(1L)).thenReturn(Optional.of(responsableInactivo));

        assertThrows(IllegalStateException.class, () -> {
            solicitudService.asignarResponsable(1L, 1L);
        });
    }
}