package co.edu.uniquindio.proyectoavanzada;

import co.edu.uniquindio.proyectoavanzada.dto.*;
import co.edu.uniquindio.proyectoavanzada.entities.*;
import co.edu.uniquindio.proyectoavanzada.entities.enums.*;
import co.edu.uniquindio.proyectoavanzada.excepciones.RecursoNoEncontradoException;
import co.edu.uniquindio.proyectoavanzada.repositories.*;
import co.edu.uniquindio.proyectoavanzada.security.JwtUtil;
import co.edu.uniquindio.proyectoavanzada.services.impl.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("SolicitudService — Tests Unitarios Completos")
class SolicitudServiceTest {

    @Mock private SolicitudRepository    solicitudRepository;
    @Mock private ResponsableRepository  responsableRepository;
    @Mock private HistorialRepository    historialRepository;
    @Mock private EstudianteRepository   estudianteRepository;

    @InjectMocks
    private SolicitudServiceImpl solicitudService;

    private Estudiante estudianteActivo;
    private Responsable responsableActivo;

    @BeforeEach
    void setUp() {
        estudianteActivo = new Estudiante();
        estudianteActivo.setId(1L);
        estudianteActivo.setNombreCompleto("Mariana Ramírez");
        estudianteActivo.setCorreo("mariana@uniquindio.edu.co");

        responsableActivo = new Responsable();
        responsableActivo.setId(10L);
        responsableActivo.setNombreCompleto("Prof. Martínez");
        responsableActivo.setActivo(true);
        responsableActivo.setDeleted(false);
    }

    // 1. Pruebas para registrarSolicitud

    @Nested
    @DisplayName("registrarSolicitud")
    class RegistrarSolicitudTests {

        @Test
        @DisplayName("should_retornarSolicitudRegistrada_when_datosValidos")
        void should_retornarSolicitudRegistrada_when_datosValidos() {
            CrearSolicitudDTO dto = new CrearSolicitudDTO(
                    "Necesito homologar Cálculo II",
                    TipoSolicitud.HOMOLOGACION, "correo", 1L);
            Solicitud guardada = Solicitud.builder()
                    .id(100L).descripcion(dto.descripcion())
                    .tipo(dto.tipoSolicitud()).estado(EstadoSolicitud.REGISTRADA)
                    .canalOrigen(dto.canalOrigen()).estudiante(estudianteActivo)
                    .fechaHoraRegistro(LocalDateTime.now()).build();

            when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudianteActivo));
            when(solicitudRepository.save(any(Solicitud.class))).thenReturn(guardada);

            Solicitud resultado = solicitudService.registrarSolicitud(dto);

            assertNotNull(resultado);
            assertEquals(EstadoSolicitud.REGISTRADA, resultado.getEstado());
            assertEquals(TipoSolicitud.HOMOLOGACION, resultado.getTipo());
            assertEquals(estudianteActivo, resultado.getEstudiante());
            verify(solicitudRepository, times(1)).save(any(Solicitud.class));
        }

        @Test
        @DisplayName("should_asignarFechaRegistro_when_solicitudCreada")
        void should_asignarFechaRegistro_when_solicitudCreada() {
            CrearSolicitudDTO dto = new CrearSolicitudDTO(
                    "Solicitud de cupos", TipoSolicitud.CUPOS, "presencial", 1L);
            ArgumentCaptor<Solicitud> captor = ArgumentCaptor.forClass(Solicitud.class);

            when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudianteActivo));
            when(solicitudRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            solicitudService.registrarSolicitud(dto);

            assertNotNull(captor.getValue().getFechaHoraRegistro());
            assertThat(captor.getValue().getFechaHoraRegistro())
                    .isBeforeOrEqualTo(LocalDateTime.now());
        }

        @Test
        @DisplayName("should_lanzarRecursoNoEncontrado_when_estudianteInexistente")
        void should_lanzarRecursoNoEncontrado_when_estudianteInexistente() {
            CrearSolicitudDTO dto = new CrearSolicitudDTO(
                    "desc", TipoSolicitud.CUPOS, "web", 999L);
            when(estudianteRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(RecursoNoEncontradoException.class,
                    () -> solicitudService.registrarSolicitud(dto));
            verify(solicitudRepository, never()).save(any());
        }

        @Test
        @DisplayName("should_guardarConEstadoRegistrada_when_nuevaSolicitud")
        void should_guardarConEstadoRegistrada_when_nuevaSolicitud() {
            CrearSolicitudDTO dto = new CrearSolicitudDTO(
                    "desc", TipoSolicitud.HOMOLOGACION, "correo", 1L);
            ArgumentCaptor<Solicitud> captor = ArgumentCaptor.forClass(Solicitud.class);

            when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudianteActivo));
            when(solicitudRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            solicitudService.registrarSolicitud(dto);

            assertEquals(EstadoSolicitud.REGISTRADA, captor.getValue().getEstado());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. realizarTriage
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("realizarTriage")
    class RealizarTriageTests {

        @Test
        @DisplayName("should_cambiarEstadoAClasificada_when_solicitudRegistrada")
        void should_cambiarEstadoAClasificada_when_solicitudRegistrada() {
            Solicitud solicitud = Solicitud.builder()
                    .id(1L).estado(EstadoSolicitud.REGISTRADA).build();

            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
            when(solicitudRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Solicitud resultado = solicitudService.realizarTriage(1L,
                    new PrioridadDTO(
                            NivelSolicitud.ALTA,
                            "Urgente por fecha límite",
                            "Solicitud prioritaria",
                            LocalDate.now()
                    )
                    );
            assertEquals(EstadoSolicitud.CLASIFICADA, resultado.getEstado());
            assertNotNull(resultado.getPrioridad());
        }

        @Test
        @DisplayName("should_asignarNivelDePrioridad_when_triageExitoso")
        void should_asignarNivelDePrioridad_when_triageExitoso() {
            // Arrange
            Solicitud solicitud = Solicitud.builder()
                    .id(1L).estado(EstadoSolicitud.REGISTRADA).build();

            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
            when(solicitudRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // Act
            Solicitud resultado = solicitudService.realizarTriage(1L,
                    new PrioridadDTO(NivelSolicitud.BAJA, "Bajo", "Sin urgencia", null)            );

            // Assert — verificamos el nivel, que sí está definido en la entidad
            assertEquals(NivelSolicitud.BAJA, resultado.getPrioridad().getNivel());        }

        @Test
        @DisplayName("should_guardarSolicitudConEstadoClasificada_when_triageExitoso")
        void should_guardarSolicitudConEstadoClasificada_when_triageExitoso() {
            Solicitud solicitud = Solicitud.builder()
                    .id(1L).estado(EstadoSolicitud.REGISTRADA).build();
            ArgumentCaptor<Solicitud> captor = ArgumentCaptor.forClass(Solicitud.class);

            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
            when(solicitudRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            solicitudService.realizarTriage(
                    1L,
                    new PrioridadDTO(
                            NivelSolicitud.MEDIA,
                            "Revisión normal",
                            "Solicitud de prioridad media",
                            LocalDate.now()
                    )
            );
            assertEquals(EstadoSolicitud.CLASIFICADA, captor.getValue().getEstado());
        }

        @Test
        @DisplayName("should_registrarDescripcionEnPrioridad_when_triageConDescripcion")
        void should_registrarDescripcionEnPrioridad_when_triageConDescripcion() {
            // Arrange
            Solicitud solicitud = Solicitud.builder()
                    .id(1L).estado(EstadoSolicitud.REGISTRADA).build();

            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
            when(solicitudRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // Act
            Solicitud resultado = solicitudService.realizarTriage(1L,
                    new PrioridadDTO(NivelSolicitud.BAJA, "Bajo", "Sin urgencia inmediata", null));

            // Assert — verificamos justificacion en la entidad Prioridad
            assertEquals("Sin urgencia inmediata", resultado.getPrioridad().getJustificacion());
        }

        @Test
        @DisplayName("should_lanzarRecursoNoEncontrado_when_solicitudInexistente")
        void should_lanzarRecursoNoEncontrado_when_solicitudInexistente() {
            when(solicitudRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(RecursoNoEncontradoException.class,
                    () -> solicitudService.realizarTriage(999L,
                            new PrioridadDTO(
                                    NivelSolicitud.MEDIA,
                                    "Impacto académico moderado",
                                    "desc",
                                    LocalDate.now()
                            )
                    )
            );
        }

        @Test
        @DisplayName("should_lanzarIllegalState_when_solicitudYaClasificada")
        void should_lanzarIllegalState_when_solicitudYaClasificada() {
            Solicitud solicitud = Solicitud.builder()
                    .id(2L).estado(EstadoSolicitud.CLASIFICADA).build();
            when(solicitudRepository.findById(2L)).thenReturn(Optional.of(solicitud));

            assertThrows(IllegalStateException.class,
                    () -> solicitudService.realizarTriage(2L,
                            new PrioridadDTO(
                                    NivelSolicitud.BAJA,
                                    "Bajo impacto académico",
                                    "desc",
                                    LocalDate.now()
                            )
                    )
            );
        }

        // java
        @Test
        @DisplayName("should_registrarHistorial_when_triageExitoso")
        void should_registrarHistorial_when_triageExitoso() {
            Solicitud solicitud = Solicitud.builder()
                    .id(1L).estado(EstadoSolicitud.REGISTRADA).build();

            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
            when(solicitudRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            PrioridadDTO dto = new PrioridadDTO(
                    NivelSolicitud.BAJA,
                    "Bajo impacto académico",
                    "desc",
                    LocalDate.now()
            );

            // Invocar el método bajo prueba
            solicitudService.realizarTriage(1L, dto);

            verify(historialRepository, atLeastOnce()).save(any(Historial.class));
        }

    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. asignarResponsable
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("asignarResponsable")
    class AsignarResponsableTests {

        @Test
        @DisplayName("should_cambiarEstadoAEnAtencion_when_responsableActivoAsignado")
        void should_cambiarEstadoAEnAtencion_when_responsableActivoAsignado() {
            Solicitud solicitud = Solicitud.builder()
                    .id(1L).estado(EstadoSolicitud.CLASIFICADA).build();

            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
            when(responsableRepository.findById(10L)).thenReturn(Optional.of(responsableActivo));
            when(solicitudRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Solicitud resultado = solicitudService.asignarResponsable(1L, 10L);

            assertEquals(EstadoSolicitud.EN_ATENCION, resultado.getEstado());
        }

        @Test
        @DisplayName("should_vincularResponsableEnSolicitud_when_asignacionExitosa")
        void should_vincularResponsableEnSolicitud_when_asignacionExitosa() {
            Solicitud solicitud = Solicitud.builder()
                    .id(1L).estado(EstadoSolicitud.CLASIFICADA).build();

            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
            when(responsableRepository.findById(10L)).thenReturn(Optional.of(responsableActivo));
            when(solicitudRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Solicitud resultado = solicitudService.asignarResponsable(1L, 10L);

            assertNotNull(resultado.getResponsableAsignado());
            assertEquals(10L, resultado.getResponsableAsignado().getId());
            assertEquals("Prof. Martínez",
                    resultado.getResponsableAsignado().getNombreCompleto());
        }

        @Test
        @DisplayName("should_persistirCambios_when_asignacionExitosa")
        void should_persistirCambios_when_asignacionExitosa() {
            Solicitud solicitud = Solicitud.builder()
                    .id(1L).estado(EstadoSolicitud.CLASIFICADA).build();

            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
            when(responsableRepository.findById(10L)).thenReturn(Optional.of(responsableActivo));
            when(solicitudRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            solicitudService.asignarResponsable(1L, 10L);

            verify(solicitudRepository, times(1)).save(solicitud);
        }

        @Test
        @DisplayName("should_lanzarIllegalState_when_responsableInactivo")
        void should_lanzarIllegalState_when_responsableInactivo() {
            Solicitud solicitud = Solicitud.builder()
                    .id(1L).estado(EstadoSolicitud.CLASIFICADA).build();
            Responsable inactivo = new Responsable();
            inactivo.setActivo(false);

            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
            when(responsableRepository.findById(10L)).thenReturn(Optional.of(inactivo));

            assertThrows(IllegalStateException.class,
                    () -> solicitudService.asignarResponsable(1L, 10L));
            verify(solicitudRepository, never()).save(any());
        }

        @Test
        @DisplayName("should_lanzarIllegalState_when_solicitudNoClasificada")
        void should_lanzarIllegalState_when_solicitudNoClasificada() {
            Solicitud solicitud = Solicitud.builder()
                    .id(1L).estado(EstadoSolicitud.REGISTRADA).build();

            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
            when(responsableRepository.findById(10L)).thenReturn(Optional.of(responsableActivo));

            assertThrows(IllegalStateException.class,
                    () -> solicitudService.asignarResponsable(1L, 10L));
        }

        @Test
        @DisplayName("should_lanzarRecursoNoEncontrado_when_responsableInexistente")
        void should_lanzarRecursoNoEncontrado_when_responsableInexistente() {
            Solicitud solicitud = Solicitud.builder()
                    .id(1L).estado(EstadoSolicitud.CLASIFICADA).build();

            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
            when(responsableRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(RecursoNoEncontradoException.class,
                    () -> solicitudService.asignarResponsable(1L, 99L));
        }

        @Test
        @DisplayName("should_registrarHistorial_when_asignacionExitosa")
        void should_registrarHistorial_when_asignacionExitosa() {
            Solicitud solicitud = Solicitud.builder()
                    .id(1L).estado(EstadoSolicitud.CLASIFICADA).build();

            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
            when(responsableRepository.findById(10L)).thenReturn(Optional.of(responsableActivo));
            when(solicitudRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            solicitudService.asignarResponsable(1L, 10L);

            verify(historialRepository, atLeastOnce()).save(any(Historial.class));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 4. marcarComoAtendida
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("marcarComoAtendida")
    class MarcarComoAtendidaTests {

        @Test
        @DisplayName("should_cambiarEstadoAAtendida_when_solicitudEnAtencion")
        void should_cambiarEstadoAAtendida_when_solicitudEnAtencion() {
            Solicitud solicitud = Solicitud.builder()
                    .id(1L).estado(EstadoSolicitud.EN_ATENCION).build();

            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
            when(solicitudRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Solicitud resultado = solicitudService.marcarComoAtendida(1L, "Resuelto");

            assertEquals(EstadoSolicitud.ATENDIDA, resultado.getEstado());
        }

        @Test
        @DisplayName("should_lanzarIllegalState_when_solicitudNoEnAtencion")
        void should_lanzarIllegalState_when_solicitudNoEnAtencion() {
            Solicitud solicitud = Solicitud.builder()
                    .id(1L).estado(EstadoSolicitud.REGISTRADA).build();
            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));

            assertThrows(IllegalStateException.class,
                    () -> solicitudService.marcarComoAtendida(1L, "obs"));
        }

        @Test
        @DisplayName("should_lanzarIllegalState_when_solicitudYaCerrada")
        void should_lanzarIllegalState_when_solicitudYaCerrada() {
            Solicitud solicitud = Solicitud.builder()
                    .id(1L).estado(EstadoSolicitud.CERRADA).build();
            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));

            assertThrows(IllegalStateException.class,
                    () -> solicitudService.marcarComoAtendida(1L, "obs"));
        }

        @Test
        @DisplayName("should_lanzarIllegalState_when_solicitudClasificada")
        void should_lanzarIllegalState_when_solicitudClasificada() {
            Solicitud solicitud = Solicitud.builder()
                    .id(1L).estado(EstadoSolicitud.CLASIFICADA).build();
            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));

            assertThrows(IllegalStateException.class,
                    () -> solicitudService.marcarComoAtendida(1L, "obs"));
        }

        @Test
        @DisplayName("should_registrarHistorial_when_marcadaAtendida")
        void should_registrarHistorial_when_marcadaAtendida() {
            Solicitud solicitud = Solicitud.builder()
                    .id(1L).estado(EstadoSolicitud.EN_ATENCION).build();

            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
            when(solicitudRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            solicitudService.marcarComoAtendida(1L, "Respuesta enviada");

            verify(historialRepository, atLeastOnce()).save(any(Historial.class));
        }

        @Test
        @DisplayName("should_lanzarRecursoNoEncontrado_when_idInexistente")
        void should_lanzarRecursoNoEncontrado_when_idInexistente() {
            when(solicitudRepository.findById(777L)).thenReturn(Optional.empty());

            assertThrows(RecursoNoEncontradoException.class,
                    () -> solicitudService.marcarComoAtendida(777L, "obs"));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 5. cerrarSolicitud
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("cerrarSolicitud")
    class CerrarSolicitudTests {

        @Test
        @DisplayName("should_cambiarEstadoACerrada_when_solicitudAtendida")
        void should_cambiarEstadoACerrada_when_solicitudAtendida() {
            Solicitud solicitud = Solicitud.builder()
                    .id(1L).estado(EstadoSolicitud.ATENDIDA).build();

            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
            when(solicitudRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            solicitudService.cerrarSolicitud(1L, new CierreDTO(null, "Todo resuelto", null));

            assertEquals(EstadoSolicitud.CERRADA, solicitud.getEstado());
            assertNotNull(solicitud.getFechaCierre());
        }

        @Test
        @DisplayName("should_asignarFechaCierre_when_solicitudCerradaExitosamente")
        void should_asignarFechaCierre_when_solicitudCerradaExitosamente() {
            Solicitud solicitud = Solicitud.builder()
                    .id(1L).estado(EstadoSolicitud.ATENDIDA).build();
            LocalDateTime antes = LocalDateTime.now().minusSeconds(1);

            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
            when(solicitudRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            solicitudService.cerrarSolicitud(1L, new CierreDTO(null, "obs", null));

            assertThat(solicitud.getFechaCierre()).isAfter(antes);
        }

        @Test
        @DisplayName("should_lanzarIllegalState_when_solicitudRegistrada")
        void should_lanzarIllegalState_when_solicitudRegistrada() {
            Solicitud solicitud = Solicitud.builder()
                    .id(1L).estado(EstadoSolicitud.REGISTRADA).build();
            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));

            assertThrows(IllegalStateException.class,
                    () -> solicitudService.cerrarSolicitud(1L,
                            new CierreDTO(null, "obs", null)));
        }

        @Test
        @DisplayName("should_lanzarIllegalState_when_solicitudEnAtencion")
        void should_lanzarIllegalState_when_solicitudEnAtencion() {
            Solicitud solicitud = Solicitud.builder()
                    .id(1L).estado(EstadoSolicitud.EN_ATENCION).build();
            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));

            assertThrows(IllegalStateException.class,
                    () -> solicitudService.cerrarSolicitud(1L,
                            new CierreDTO(null, "obs", null)));
        }

        @Test
        @DisplayName("should_lanzarIllegalState_when_solicitudClasificada")
        void should_lanzarIllegalState_when_solicitudClasificada() {
            Solicitud solicitud = Solicitud.builder()
                    .id(1L).estado(EstadoSolicitud.CLASIFICADA).build();
            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));

            assertThrows(IllegalStateException.class,
                    () -> solicitudService.cerrarSolicitud(1L,
                            new CierreDTO(null, "obs", null)));
        }

        @Test
        @DisplayName("should_lanzarRecursoNoEncontrado_when_idInexistente")
        void should_lanzarRecursoNoEncontrado_when_idInexistente() {
            when(solicitudRepository.findById(404L)).thenReturn(Optional.empty());

            assertThrows(RecursoNoEncontradoException.class,
                    () -> solicitudService.cerrarSolicitud(404L,
                            new CierreDTO(null, "obs", null)));
        }

        @Test
        @DisplayName("should_registrarHistorial_when_cierreExitoso")
        void should_registrarHistorial_when_cierreExitoso() {
            Solicitud solicitud = Solicitud.builder()
                    .id(1L).estado(EstadoSolicitud.ATENDIDA).build();

            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
            when(solicitudRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            solicitudService.cerrarSolicitud(1L, new CierreDTO(null, "obs", null));

            verify(historialRepository, atLeastOnce()).save(any(Historial.class));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 6. Consultas — listar, obtener, filtrar
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("consultas y listados")
    class ConsultasTests {

        @Test
        @DisplayName("should_retornarDTO_when_idExistente")
        void should_retornarDTO_when_idExistente() {
            Solicitud solicitud = Solicitud.builder()
                    .id(1L).descripcion("Homologación")
                    .estado(EstadoSolicitud.REGISTRADA)
                    .tipo(TipoSolicitud.HOMOLOGACION)
                    .estudiante(estudianteActivo).build();
            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));

            SolicitudDTO resultado = solicitudService.obtenerPorId(1L);

            assertNotNull(resultado);
            assertEquals(1L, resultado.id());
        }

        @Test
        @DisplayName("should_lanzarRecursoNoEncontrado_when_idInexistenteEnObtener")
        void should_lanzarRecursoNoEncontrado_when_idInexistenteEnObtener() {
            when(solicitudRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(RecursoNoEncontradoException.class,
                    () -> solicitudService.obtenerPorId(999L));
        }

        @Test
        @DisplayName("should_retornarListaVacia_when_noHaySolicitudes")
        void should_retornarListaVacia_when_noHaySolicitudes() {
            when(solicitudRepository.findAll()).thenReturn(List.of());

            List<SolicitudDTO> resultado = solicitudService.listarTodas();

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }

        @Test
        @DisplayName("should_retornarTresSolicitudes_when_hayTresRegistros")
        void should_retornarTresSolicitudes_when_hayTresRegistros() {
            Solicitud s1 = buildSolicitud(1L, EstadoSolicitud.REGISTRADA);
            Solicitud s2 = buildSolicitud(2L, EstadoSolicitud.CLASIFICADA);
            Solicitud s3 = buildSolicitud(3L, EstadoSolicitud.EN_ATENCION);

            when(solicitudRepository.findAll()).thenReturn(List.of(s1, s2, s3));

            assertEquals(3, solicitudService.listarTodas().size());
        }

        @Test
        @DisplayName("should_incluirTodosLosEstados_when_listarTodas")
        void should_incluirTodosLosEstados_when_listarTodas() {
            Solicitud s1 = buildSolicitud(1L, EstadoSolicitud.REGISTRADA);
            Solicitud s2 = buildSolicitud(2L, EstadoSolicitud.CERRADA);
            when(solicitudRepository.findAll()).thenReturn(List.of(s1, s2));

            assertEquals(2, solicitudService.listarTodas().size());
        }

        @Test
        @DisplayName("should_mapearIdCorrectamente_when_listarTodas")
        void should_mapearIdCorrectamente_when_listarTodas() {
            Solicitud s = buildSolicitud(42L, EstadoSolicitud.REGISTRADA);
            when(solicitudRepository.findAll()).thenReturn(List.of(s));

            assertEquals(42L, solicitudService.listarTodas().get(0).id());
        }

        // listarPorEstado
        @Test
        @DisplayName("should_retornarSoloRegistradas_when_filtroRegistrada")
        void should_retornarSoloRegistradas_when_filtroRegistrada() {
            Solicitud s1 = buildSolicitud(1L, EstadoSolicitud.REGISTRADA);
            Solicitud s2 = buildSolicitud(2L, EstadoSolicitud.REGISTRADA);
            when(solicitudRepository.findByEstado(EstadoSolicitud.REGISTRADA))
                    .thenReturn(List.of(s1, s2));

            List<SolicitudDTO> resultado =
                    solicitudService.listarPorEstado(EstadoSolicitud.REGISTRADA);

            assertEquals(2, resultado.size());
            assertThat(resultado).extracting(SolicitudDTO::estado)
                    .containsOnly(EstadoSolicitud.REGISTRADA);
        }

        @Test
        @DisplayName("should_retornarSoloEnAtencion_when_filtroEnAtencion")
        void should_retornarSoloEnAtencion_when_filtroEnAtencion() {
            Solicitud s = buildSolicitud(1L, EstadoSolicitud.EN_ATENCION);
            when(solicitudRepository.findByEstado(EstadoSolicitud.EN_ATENCION))
                    .thenReturn(List.of(s));

            List<SolicitudDTO> resultado =
                    solicitudService.listarPorEstado(EstadoSolicitud.EN_ATENCION);

            assertEquals(1, resultado.size());
            assertEquals(EstadoSolicitud.EN_ATENCION, resultado.get(0).estado());
        }

        @Test
        @DisplayName("should_retornarListaVacia_when_noHaySolicitudesEnEseEstado")
        void should_retornarListaVacia_when_noHaySolicitudesEnEseEstado() {
            when(solicitudRepository.findByEstado(EstadoSolicitud.CERRADA))
                    .thenReturn(List.of());

            assertTrue(solicitudService.listarPorEstado(EstadoSolicitud.CERRADA).isEmpty());
        }

        @Test
        @DisplayName("should_noMezclarEstados_when_filtrarPorClasificada")
        void should_noMezclarEstados_when_filtrarPorClasificada() {
            Solicitud s = buildSolicitud(1L, EstadoSolicitud.CLASIFICADA);
            when(solicitudRepository.findByEstado(EstadoSolicitud.CLASIFICADA))
                    .thenReturn(List.of(s));

            List<SolicitudDTO> resultado =
                    solicitudService.listarPorEstado(EstadoSolicitud.CLASIFICADA);

            assertEquals(1, resultado.size());
            assertEquals(EstadoSolicitud.CLASIFICADA, resultado.get(0).estado());
        }

        // obtenerHistorial
        @Test
        @DisplayName("should_retornarHistorialOrdenado_when_solicitudConVariosCambios")
        void should_retornarHistorialOrdenado_when_solicitudConVariosCambios() {
            Historial h1 = crearHistorial(1L, "Solicitud REGISTRADA",
                    LocalDateTime.now().minusDays(3));
            Historial h2 = crearHistorial(2L, "Triage: prioridad ALTA",
                    LocalDateTime.now().minusDays(2));
            Historial h3 = crearHistorial(3L, "Responsable asignado",
                    LocalDateTime.now().minusDays(1));

            when(historialRepository.findBySolicitudIdOrderByFechaHoraDesc(10L))                    .thenReturn(List.of(h1, h2, h3));

            List<HistorialDTO> resultado = solicitudService.obtenerHistorial(10L);

            assertEquals(3, resultado.size());
            assertEquals("Solicitud REGISTRADA",   resultado.get(0).observaciones());
            assertEquals("Triage: prioridad ALTA", resultado.get(1).observaciones());
            assertEquals("Responsable asignado",   resultado.get(2).observaciones());
        }

        @Test
        @DisplayName("should_retornarListaVacia_when_solicitudSinHistorial")
        void should_retornarListaVacia_when_solicitudSinHistorial() {
            when(historialRepository.findBySolicitudIdOrderByFechaHoraDesc(99L))
                    .thenReturn(List.of());
            assertTrue(solicitudService.obtenerHistorial(99L).isEmpty());
        }

        @Test
        @DisplayName("should_mapearFechaEnHistorial_when_obtenerHistorial")
        void should_mapearFechaEnHistorial_when_obtenerHistorial() {
            LocalDateTime fechaEsperada = LocalDateTime.of(2025, 4, 13, 10, 30);
            Historial h = crearHistorial(1L, "Estado cambiado", fechaEsperada);
            when(historialRepository.findBySolicitudIdOrderByFechaHoraDesc(1L))
                    .thenReturn(List.of(h));
            assertEquals(fechaEsperada,
                    solicitudService.obtenerHistorial(1L).get(0).fechaHora());
        }

        @Test
        @DisplayName("should_mapearDescripcionEnHistorial_when_obtenerHistorial")
        void should_mapearDescripcionEnHistorial_when_obtenerHistorial() {
            Historial h = crearHistorial(1L, "Solicitud cerrada por coordinador",
                    LocalDateTime.now());
            when(historialRepository.findBySolicitudIdOrderByFechaHoraDesc(1L))
                    .thenReturn(List.of(h));
            assertEquals("Solicitud cerrada por coordinador",
                    solicitudService.obtenerHistorial(1L).get(0).observaciones());
        }

        @Test
        @DisplayName("should_listarPorEstudiante_when_estudianteConSolicitudes")
        void should_listarPorEstudiante_when_estudianteConSolicitudes() {
            Solicitud s = buildSolicitud(1L, EstadoSolicitud.REGISTRADA);
            when(solicitudRepository.findByEstudianteId(1L)).thenReturn(List.of(s));

            assertEquals(1, solicitudService.listarPorEstudiante(1L).size());
        }

        @Test
        @DisplayName("should_listarPorResponsable_when_responsableConSolicitudes")
        void should_listarPorResponsable_when_responsableConSolicitudes() {
            Solicitud s = Solicitud.builder().id(1L)
                    .estado(EstadoSolicitud.EN_ATENCION)
                    .tipo(TipoSolicitud.CUPOS)
                    .estudiante(estudianteActivo)
                    .responsableAsignado(responsableActivo).build();
            when(solicitudRepository.findByResponsableAsignadoId(10L)).thenReturn(List.of(s));

            assertEquals(1, solicitudService.listarPorResponsable(10L).size());
        }
    }






    // ─────────────────────────────────────────────────────────────────────────
    // 7. Transiciones de estado — máquina de estados completa
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("transiciones de estado")
    class TransicionesEstadoTests {

        @Test
        @DisplayName("should_seguirFlujoCompleto_REGISTRADA_a_CERRADA")
        void should_seguirFlujoCompleto_REGISTRADA_a_CERRADA() {
            Solicitud solicitud = Solicitud.builder()
                    .id(1L).estado(EstadoSolicitud.REGISTRADA).build();

            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
            when(solicitudRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // REGISTRADA → CLASIFICADA
            solicitudService.realizarTriage(
                    1L,
                    new PrioridadDTO(
                            NivelSolicitud.ALTA,
                            "Alto impacto académico",
                            "urgente",
                            LocalDate.now()
                    )
            );            assertEquals(EstadoSolicitud.CLASIFICADA, solicitud.getEstado());

            // CLASIFICADA → EN_ATENCION
            when(responsableRepository.findById(10L)).thenReturn(Optional.of(responsableActivo));
            solicitudService.asignarResponsable(1L, 10L);
            assertEquals(EstadoSolicitud.EN_ATENCION, solicitud.getEstado());

            // EN_ATENCION → ATENDIDA
            solicitudService.marcarComoAtendida(1L, "Resuelto");
            assertEquals(EstadoSolicitud.ATENDIDA, solicitud.getEstado());

            // ATENDIDA → CERRADA
            solicitudService.cerrarSolicitud(1L, new CierreDTO(null, "Finalizado", null));
            assertEquals(EstadoSolicitud.CERRADA, solicitud.getEstado());
        }

        @Test
        @DisplayName("should_prohibirCierreDirecto_when_estadoRegistrada")
        void should_prohibirCierreDirecto_when_estadoRegistrada() {
            Solicitud solicitud = Solicitud.builder()
                    .id(1L).estado(EstadoSolicitud.REGISTRADA).build();
            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));

            assertThrows(IllegalStateException.class,
                    () -> solicitudService.cerrarSolicitud(1L,
                            new CierreDTO(null, "obs", null)));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // helpers
    // ─────────────────────────────────────────────────────────────────────────
    private Solicitud buildSolicitud(Long id, EstadoSolicitud estado) {
        return Solicitud.builder().id(id).estado(estado)
                .tipo(TipoSolicitud.CUPOS).estudiante(estudianteActivo).build();
    }

    private Historial crearHistorial(Long id, String descripcion, LocalDateTime fecha) {
        Historial h = new Historial();
        h.setId(id);
        h.setObservaciones(descripcion);
        h.setFechaHora(fecha);
        return h;
    }
}